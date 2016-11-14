package core;

public class Symbol implements Value {
    public Symbol type() {
        return this;
    }

    @Override
    public String toString() {
        return String.format("@%x", hashCode());
    }
}
