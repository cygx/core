package core;

public class Polymorph implements Value {
    public final VTable vtable;
    public final Value target;

    public Polymorph(VTable vtable, Value target) {
        this.vtable = vtable;
        this.target = target;
    }

    public Symbol type() {
        return vtable.virtualType;
    }

    public Value virtualCall(int id, World world, Value... args) {
        return vtable.entries[id].call(world, args);
    }
}
