package core;

public interface LexicalEnvironment {
    static final Symbol[] NO_PARAMETERS = {};

    Function lookup(String name, Symbol... argTypes);
    void declare(String name, Symbol type, Symbol[] parameters, Callable callable);

    default Function dispatch(String name, Value... args) {
        Symbol[] types = new Symbol[args.length];
        for(int i = 0; i < args.length; ++i)
            types[i] = args[i].type();

        return lookup(name, types);
    }

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
