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

        Pkg core = world.createPkg("core");
        core.declare("double", DoubleValue.type);

        Symbol pi = world.createSymbol("pi");
        DoubleValue piValue = new DoubleValue(3.14);
        world.registerConversion(pi, DoubleValue.type, (w, v) -> piValue);

        Pkg math = world.createPkg("math");
        math.declare("pi", pi);

        Value value = world.convert(pi, DoubleValue.type);
        System.out.println(value);
    }

    static interface LexEnv {
        void declare(String name, Symbol[] signature, Callable callable);

        default void declare(String name, Symbol type) {
            Symbol[] signature = { type };
            declare(name, signature, (world, args) -> type);
        }

        default void declare(String name, Symbol type, NullaryCallable callable) {
            Symbol[] signature = { type };
            declare(name, signature, callable);
        }

        default void declare(String name, Symbol type, Symbol parameter,
                UnaryCallable callable) {
            Symbol[] signature = { type, parameter };
            declare(name, signature, callable);
        }

        default void declare(String name, Symbol type, Symbol a, Symbol b,
                BinaryCallable callable) {
            Symbol[] signature = { type, a, b };
            declare(name, signature, callable);
        }
    }

    static class Pkg implements LexEnv {
        public final String name;

        public Pkg(String name) {
            this.name = name;
        }

        public void declare(String name, Symbol[] signature, Callable callable) {
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
            Pkg pkg = new Pkg(name);
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
        /*
        default Symbol asSymbol() { throw new ... }
        default IntValue asInt() { throw new ... }
        default BoolArray asBoolArray() { throw new ... }
        ...
        */
    }

    static class Symbol implements Value {
        public Symbol type() {
            return this;
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

        public final boolean[] values;

        public BoolArray(boolean[] values) {
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

    static class Reference implements Value {
        public final Role role;
        public final Value target;

        public Reference(Role role, Value target) {
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
