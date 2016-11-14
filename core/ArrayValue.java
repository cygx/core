package core;

public interface ArrayValue extends Value {
    default String gist() {
        return getClass().getSimpleName() + toString();
    }
}
