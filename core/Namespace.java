package core;

public class Namespace implements LexicalEnvironment {
    public final String name;
    private final World world;
    private final Stash stash;

    public Namespace(String name, World world,
            Class<? extends CandiStore> storeClass) {
        this.name = name;
        this.world = world;
        this.stash = new Stash(storeClass);
    }

    public Namespace(String name, World world) {
        this(name, world, CandiTable.class);
    }

    public void declare(String name, Symbol type, Symbol[] parameters,
            Callable callable) {
        stash.put(name, parameters, Function.box(callable, type, parameters));
    }

    public Function lookup(String name, Symbol... argTypes) {
        Function fn = stash.get(name, argTypes);
        if(fn != null) return fn;

        fn = stash.fuzzyGet(name, argTypes, world);
        return fn == null ? null : fn.withArgumentConversions();
    }

    public Value eval(String name, Value... args) {
        return dispatch(name, args).call(world, args);
    }
}
