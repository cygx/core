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

    private int binarySearch(Symbol[] key) {
        int low = 0;
        int high = size - 1;

        while(low <= high) {
            int mid = (low + high) >>> 1;
            int cmp = entries[mid].compareTo(key);

            if(cmp < 0) low = mid + 1;
            else if(cmp > 0) high = mid - 1;
            else return mid;
        }

        return ~low;
    }

    public Function get(Symbol[] types) {
        normalize();
        int idx = binarySearch(types);
        return idx < 0 ? null : entries[idx];
    }

    public Function fuzzyGet(Symbol[] types, World world) {
        LOOP: for(int i = 0; i < size; ++i) {
            Symbol[] parameters = entries[i].parameters();
            for(int j = 0; j < types.length; ++j) {
                if(!world.canConvert(types[j], parameters[j]))
                    continue LOOP;
            }

            return entries[i];
        }

        return null;
    }
}
