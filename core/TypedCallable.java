package core;

public interface TypedCallable extends Callable {
    Symbol returnType();
    Symbol[] parameters();

    TypedCallable converting();

    default Callable unbox() {
        return this;
    }

    static TypedCallable box(Callable callable, Symbol type,
            Symbol... parameters) {
        TypedCallable converting = new TypedCallable() {
            public Symbol returnType() {
                return type;
            }

            public Symbol[] parameters() {
                return parameters;
            }

            public Value call(World world, Value... args) {
                Value[] convertedArgs = new Value[parameters.length];
                for(int i = 0; i < parameters.length; ++i)
                    convertedArgs[i] = world.convert(args[i], parameters[i]);

                return callable.call(world, convertedArgs);
            }

            public TypedCallable converting() {
                return this;
            }

            @Override
            public Callable unbox() {
                return callable;
            }
        };

        return new TypedCallable() {
            public Symbol returnType() {
                return type;
            }

            public Symbol[] parameters() {
                return parameters;
            }

            public Value call(World world, Value... args) {
                return callable.call(world, args);
            }

            public TypedCallable converting() {
                return converting;
            }

            @Override
            public Callable unbox() {
                return callable;
            }
        };
    }
}
