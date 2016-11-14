package core;

@FunctionalInterface
public interface Callable {
    Value call(World world, Value... args);
}
