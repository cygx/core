/*
    register virtual call:
    Symbol RUNNABLE = new Symbol();
    world.declareFunction("run", new Signature(VOID, RUNNABLE), (world, args) -> {
        Reference ref = args[0].asReference();
        return ref.virtualCall(0, world, ref.target);
    });
*/

import java.util.*;

class mockup {
    public static void main(String[] args) {
        World world = new World();
        world.registerSymbol(DoubleValue.type, "double");
        world.registerSymbol(StringValue.type, "string");

        Pkg core = world.createPkg("core");
        core.declare("double", DoubleValue.type);
        core.declare("string", StringValue.type);

        Symbol pi = world.createSymbol("pi");
        DoubleValue piDouble = new DoubleValue(3.14);
        StringValue piString = new StringValue("pi");
        world.registerConversion(pi, DoubleValue.type, (w, v) -> piDouble);
        world.registerConversion(pi, StringValue.type, (w, v) -> piString);

        Pkg math = world.createPkg("math");
        math.declare("pi", pi);

        System.out.println(world.convert(pi, DoubleValue.type));
        System.out.println(world.convert(pi, StringValue.type));
        System.out.println(math.eval("pi"));
        System.out.println(math.lookup("pi", DoubleValue.type));
        math.declare("pi", pi, DoubleValue.type, (w, v) -> null);
        System.out.println(math.lookup("pi", DoubleValue.type));
    }

    static class TypedCallable implements Callable {
        private final Symbol type;
        private final Callable callable;

        public TypedCallable(Symbol type, Callable callable) {
            this.type = type;
            this.callable = callable;
        }

        public Symbol returnType() {
            return type;
        }

        public Callable unbox() {
            return callable;
        }

        public Value call(World world, Value... args) {
            return callable.call(world, args);
        }
    }

    static interface LexEnv {
        static final Symbol[] NO_PARAMETERS = {};

        TypedCallable lookup(String name, Symbol... argTypes);

        default TypedCallable dispatch(String name, Value... args) {
            Symbol[] types = new Symbol[args.length];
            for(int i = 0; i < args.length; ++i)
                types[i] = args[i].type();

            return lookup(name, types);
        }

        void declare(String name, Symbol type, Symbol[] parameters,
                Callable callable);

        default void declare(String name, Symbol type) {
            declare(name, type, NO_PARAMETERS, (world, args) -> type);
        }

        default void declare(String name, Symbol type, NullaryCallable callable) {
            declare(name, type, NO_PARAMETERS, callable);
        }

        default void declare(String name, Symbol type, Symbol parameter,
                UnaryCallable callable) {
            declare(name, type, new Symbol[] { parameter }, callable);
        }

        default void declare(String name, Symbol type, Symbol a, Symbol b,
                BinaryCallable callable) {
            declare(name, type, new Symbol[] { a, b }, callable);
        }
    }

    static class CandiTree {
        private static final Node DUMMY = new Node(null);

        private static final class Node {
            final Symbol key;
            TypedCallable value;
            final List<Node> children = new ArrayList<>(0);
            Node(Symbol key) { this.key = key; }
        }

        private Node root = new Node(null);

        public TypedCallable get(Symbol[] parameters) {
            if(parameters.length == 0)
                return root.value;

            Node parent = root;
            LOOP: for(int i = 0;;) {
                for(Node child : parent.children) {
                    if(child.key == parameters[i]) {
                        if(++i == parameters.length)
                            return child.value;

                        parent = child;
                        continue LOOP;
                    }
                }

                return null;
            }
        }

        public TypedCallable lookup(World world, Symbol[] argTypes) {
            TypedCallable value = get(argTypes);
            return value == null
                ? fuzzyLookup(world, root, argTypes, 0)
                : value;
        }

        private TypedCallable fuzzyLookup(World world, Node node,
                Symbol[] argTypes, int pos) {
            if(pos == argTypes.length)
                return node.value;

            for(Node child : node.children) {
                if(world.convertible(argTypes[pos], child.key)) {
                    TypedCallable found =
                        fuzzyLookup(world, child, argTypes, pos + 1);

                    if(found != null) return found;
                }
            }

            return null;
        }

        public void put(Symbol type, Symbol[] parameters, Callable callable) {
            Node node = root;
            LOOP: for(int i = 0; i < parameters.length; ++i) {
                for(Node child : node.children) {
                    if(child.key == parameters[i]) {
                        node = child;
                        continue LOOP;
                    }
                }

                Node child = new Node(parameters[i]);
                node.children.add(child);
                node = child;
            }

            if(node.value != null)
                throw new IllegalStateException();

            node.value = new TypedCallable(type, callable);
        }
    }

    static class Pkg implements LexEnv {
        private static final class StashKey {
            public final String name;
            public final int arity;
            private final int hashCode;

            public StashKey(String name, int arity) {
                this.name = name;
                this.arity = arity;
                this.hashCode = 31 * arity + name.hashCode();
            }

            @Override
            public int hashCode() {
                return hashCode;
            }

            @Override
            public boolean equals(Object obj) {
                if(this == obj) return true;
                if(!(obj instanceof StashKey)) return false;
                StashKey key = (StashKey)obj;
                return key.name.equals(name) && key.arity == arity;
            }
        }

        public final String name;
        private final World world;
        private final Map<StashKey, CandiTree> stash = new HashMap<>();

        public Pkg(String name, World world) {
            this.name = name;
            this.world = world;
        }

        public void declare(String name, Symbol type, Symbol[] parameters, 
                Callable callable) {
            StashKey key = new StashKey(name, parameters.length);
            CandiTree tree = stash.get(key);
            if(tree == null) stash.put(key, tree = new CandiTree());
            tree.put(type, parameters, callable);
        }

        public TypedCallable lookup(String name, Symbol... argTypes) {
            CandiTree tree = stash.get(new StashKey(name, argTypes.length));
            return tree == null ? null : tree.lookup(world, argTypes);
        }

        public Value eval(String name, Value... args) {
            return dispatch(name, args).call(world, args);
        }
    }

    static class World {
        private final Map<SymPair, UnaryCallable> conversions = new HashMap<>();
        private final Map<Symbol, String> symbols = new HashMap<>();
        private final Map<String, Pkg> packages = new HashMap<>();

        public Symbol createSymbol(String name) {
            Symbol sym = new Symbol();
            symbols.put(sym, name);
            return sym;
        }

        public Pkg createPkg(String name) {
            Pkg pkg = new Pkg(name, this);
            if(packages.put(name, pkg) != null)
                throw new IllegalStateException();

            return pkg;
        }

        public void registerSymbol(Symbol sym, String name) {
            if(symbols.put(sym, name) != null)
                throw new IllegalStateException();
        }

        public void registerConversion(Symbol src, Symbol dest,
                UnaryCallable conversion) {
            if(conversions.put(new SymPair(src, dest), conversion) != null)
                throw new IllegalStateException();
        }

        public Value convert(Value src, Symbol destType) {
            UnaryCallable converter = conversions.get(
                new SymPair(src.type(), destType));

            return converter == null ? null : converter.call(this, src);
        }

        public boolean convertible(Symbol srcType, Symbol destType) {
            return conversions.get(new SymPair(srcType, destType)) != null;
        }
    }

    static final class SymPair {
        public final Symbol a;
        public final Symbol b;
        private final int hashCode;

        public SymPair(Symbol a, Symbol b) {
            this.a = a;
            this.b = b;
            this.hashCode = 31 * a.hashCode() + b.hashCode();
        }

        @Override
        public int hashCode() {
            return hashCode;
        }

        @Override
        public boolean equals(Object obj) {
            if(this == obj) return true;
            if(!(obj instanceof SymPair)) return false;
            SymPair pair = (SymPair)obj;
            return pair.a == a && pair.b == b;
        }
    }

    static interface Value {
        Symbol type();

        default boolean is(Symbol type) {
            return type() == type;
        }
    }

    static class Symbol implements Value {
        public Symbol type() {
            return this;
        }
    }

    static class StringValue implements Value {
        public static final Symbol type = new Symbol();

        public final String value;

        public StringValue(String value) {
            this.value = value;
        }

        public Symbol type() {
            return type;
        }

        public String toString() {
            return "string(" + value + ")";
        }
    }

    static class DoubleValue implements Value {
        public static final Symbol type = new Symbol();

        public final double value;

        public DoubleValue(double value) {
            this.value = value;
        }

        public Symbol type() {
            return type;
        }

        public String toString() {
            return "double(" + value + ")";
        }
    }

    static class BoolArray implements Value {
        public static final Symbol type = new Symbol();

        public static BoolArray alloc(int size) {
            return new BoolArray(new boolean[size]);
        }

        public final boolean[] values;

        public BoolArray(boolean... values) {
            this.values = values;
        }

        public Symbol type() {
            return type;
        }

        public int size() {
            return values.length;
        }
    }

    static class TupleSpec {
        public final Symbol type = new Symbol();
        public final Symbol[] elementTypes;

        public TupleSpec(Symbol... elementTypes) {
            this.elementTypes = elementTypes;
        }

        public int size() {
            return elementTypes.length;
        }
    }

    static class Tuple implements Value {
        public final TupleSpec spec;
        public final Value[] elements;

        public Tuple(TupleSpec spec) {
            this.spec = spec;
            this.elements = new Value[spec.size()];
        }

        public Symbol type() {
            return spec.type;
        }
    }

    static class Role {
        public final Symbol type;
        public final Callable[] vtable;

        public Role(Symbol type, Callable... vtable) {
            this.type = type;
            this.vtable = vtable;
        }
    }

    static class Polymorph implements Value {
        public final Role role;
        public final Value target;

        public Polymorph(Value target, Role role) {
            this.role = role;
            this.target = target;
        }

        public Symbol type() {
            return role.type;
        }

        public Value virtualCall(int i, World world, Value... args) {
            return role.vtable[i].call(world, args);
        }
    }

    @FunctionalInterface
    static interface Callable {
        Value call(World world, Value... args);
    }

    @FunctionalInterface
    static interface NullaryCallable extends Callable {
        Value call(World world);
        default Value call(World world, Value... args) {
            return call(world);
        }
    }

    @FunctionalInterface
    static interface UnaryCallable extends Callable {
        Value call(World world, Value arg);
        default Value call(World world, Value... args) {
            return call(world, args[0]);
        }
    }

    @FunctionalInterface
    static interface BinaryCallable extends Callable {
        Value call(World world, Value a, Value b);
        default Value call(World world, Value... args) {
            return call(world, args[0], args[1]);
        }
    }
}
