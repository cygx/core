package core;

public class VTable {
    public final Symbol virtualType;
    public final Callable[] entries;

    public VTable(Symbol type, Callable... entries) {
        this.virtualType = type;
        this.entries = entries;
    }
}
