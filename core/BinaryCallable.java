package core;

@FunctionalInterface
public interface BinaryCallable extends Callable {
    Value call(World world, Value a, Value b);

    default Value call(World world, Value... args) {
        return call(world, args[0], args[1]);
    }
}
