package core;

public interface Function extends Callable, Comparable<Function> {
    Symbol returnType();
    Symbol[] parameters();

    Function withArgumentConversions();
    Function withoutArgumentConversions();

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

    static Function box(Callable callable, Symbol type, Symbol... parameters) {
        Callable boxed = (world, args) -> {
            Value[] convertedArgs = new Value[parameters.length];
            for(int i = 0; i < parameters.length; ++i)
                convertedArgs[i] = world.convert(args[i], parameters[i]);

            return callable.call(world, convertedArgs);
        };

        BoxingFunction basic = new BoxingFunction(callable, type, parameters);
        BoxingFunction converting = new BoxingFunction(boxed, type, parameters);
        basic.withConversions = converting;
        converting.withoutConversions = basic;
        return basic;
    }
}
