package core;
import java.util.*;

final class CandiTable implements CandiStore {
    private Function[] entries = new Function[1];
    private int size;
    private boolean dirty;

    public CandiTable() {}

    private void normalize() {
        if(!dirty) return;
        dirty = false;
        Arrays.sort(entries, 0, size);
    }

    public void put(Symbol[] types, Function value) {
        if(size == entries.length) {
            Function[] newEntries = new Function[(size + 1) * 2];
            System.arraycopy(entries, 0, newEntries, 0, size);
            entries = newEntries;
        }

        entries[size++] = value;
        dirty = true;
    }

    public void forcePut(Symbol[] types, Function value) {
        throw new RuntimeException("TODO: design needs a rethink");
    }

    public Function get(Symbol[] types) {
        normalize();
        throw new RuntimeException("TODO");
    }

    public Function fuzzyGet(Symbol[] types, World world) {
        normalize();
        throw new RuntimeException("TODO");
    }
}
