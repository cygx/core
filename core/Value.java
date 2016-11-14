package core;

public interface Value {
    Symbol type();

    default boolean is(Symbol type) {
        return type() == type;
    }

    default String gist() {
        return getClass().getSimpleName() + '(' + toString() + ')';
    }
}
