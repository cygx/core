package core;

@FunctionalInterface
public interface NullaryCallable extends Callable {
    Value call(World world);

    default Value call(World world, Value... args) {
        return call(world);
    }
}

