package core;
import java.util.*;

public class BoolArray implements ArrayValue {
    public static final Symbol type = new Symbol();

    public static BoolArray alloc(int size) {
        return new BoolArray(new boolean[size]);
    }

    public final boolean[] values;

    public BoolArray(boolean... values) {
        this.values = values;
    }

    public Symbol type() {
        return type;
    }

    public int size() {
        return values.length;
    }

    public String toString() {
        return Arrays.toString(values);
    }
}
