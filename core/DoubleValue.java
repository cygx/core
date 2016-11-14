package core;

public class DoubleValue implements Value {
    public static final Symbol type = new Symbol();

    public final double value;

    public DoubleValue(double value) {
        this.value = value;
    }

    public Symbol type() {
        return type;
    }

    @Override
    public String toString() {
        return Double.toString(value);
    }
}
