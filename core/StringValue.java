package core;

public class StringValue implements Value {
    public static final Symbol type = new Symbol();

    public final String value;

    public StringValue(String value) {
        this.value = value;
    }

    public Symbol type() {
        return type;
    }

    @Override
    public String toString() {
        return value;
    }
}
