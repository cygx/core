package core;

public class TupleLayout {
    public final Symbol tupleType = new Symbol();
    public final Symbol[] elementTypes;

    public TupleLayout(Symbol... elementTypes) {
        this.elementTypes = elementTypes;
    }

    public int size() {
        return elementTypes.length;
    }
}
