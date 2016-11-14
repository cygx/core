package core;

public class Tuple implements Value {
    public final TupleLayout layout;
    public final Value[] elements;

    public Tuple(TupleLayout layout) {
        this.layout = layout;
        this.elements = new Value[layout.size()];
    }

    public Symbol type() {
        return layout.tupleType;
    }
}
