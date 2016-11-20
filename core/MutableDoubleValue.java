package core;

public class MutableDoubleValue implements Value {
    public static final Symbol type = new Symbol();

    public double value;

    public MutableDoubleValue(double value) {
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
