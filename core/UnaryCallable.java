package core;

@FunctionalInterface
public interface UnaryCallable extends Callable {
    Value call(World world, Value arg);

    default Value call(World world, Value... args) {
        return call(world, args[0]);
    }
}
