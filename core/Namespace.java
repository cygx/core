package core;

public class Namespace implements LexicalEnvironment {
    public final String name;
    private final World world;
    private final Stash stash = new Stash();

    public Namespace(String name, World world) {
        this.name = name;
        this.world = world;
    }

    public void declare(String name, Symbol type, Symbol[] parameters,
            Callable callable) {
        stash.put(name, parameters, Function.box(callable, type, parameters));
    }

    public Function lookup(String name, Symbol... argTypes) {
        Function callable = stash.get(name, argTypes);
        if(callable != null) return callable;

        callable = stash.fuzzyGet(name, argTypes, world);
        return callable == null ? null : callable.converting();
    }

    public Value eval(String name, Value... args) {
        return dispatch(name, args).call(world, args);
    }
}
