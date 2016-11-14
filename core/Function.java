package core;

public interface Function extends Callable, Comparable<Function> {
    Symbol returnType();
    Symbol[] parameters();

    Function converting();

    default Callable unbox() {
        return this;
    }

    default int compareTo(Function fn) {
        Symbol[] a = parameters();
        Symbol[] b = fn.parameters();
        if(a.length != b.length)
            return a.length - b.length;

        for(int i = 0; i < a.length; ++i) {
            if(a[i] != b[i])
                return a[i].id - b[i].id;
        }

        return 0;
    }

    static Function box(Callable callable, Symbol type,
            Symbol... parameters) {
        Function converting = new Function() {
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

            public Function converting() {
                return this;
            }

            @Override
            public Callable unbox() {
                return callable;
            }
        };

        return new Function() {
            public Symbol returnType() {
                return type;
            }

            public Symbol[] parameters() {
                return parameters;
            }

            public Value call(World world, Value... args) {
                return callable.call(world, args);
            }

            public Function converting() {
                return converting;
            }

            @Override
            public Callable unbox() {
                return callable;
            }
        };
    }
}
