package core;
import java.util.*;
import java.lang.reflect.*;

final class Stash extends HashMap<StashKey, CandiStore> {
    private final Constructor<? extends CandiStore> storeConstructor;

    public Stash(Class<? extends CandiStore> storeClass) {
        try { this.storeConstructor = storeClass.getConstructor(); }
        catch(NoSuchMethodException e) { throw new RuntimeException(e); }
    }

    private CandiStore createStore() {
        try { return storeConstructor.newInstance(); }
        catch(InstantiationException e) { throw new RuntimeException(e); }
        catch(IllegalAccessException e) { throw new RuntimeException(e); }
        catch(InvocationTargetException e) { throw new RuntimeException(e); }
    }

    public Function get(String name, Symbol[] types) {
        CandiStore store = get(new StashKey(name, types.length));
        return store == null ? null : store.get(types);
    }

    public Function fuzzyGet(String name, Symbol[] types, World world) {
        CandiStore store = get(new StashKey(name, types.length));
        return store == null ? null : store.fuzzyGet(types, world);
    }

    private CandiStore makeStore(String name, int arity) {
        StashKey key = new StashKey(name, arity);
        CandiStore store = get(key);
        if(store == null) put(key, store = createStore());
        return store;
    }

    public void put(String name, Symbol[] types, Function value) {
        makeStore(name, types.length).put(types, value);
    }

    public void forcePut(String name, Symbol[] types, Function value) {
        makeStore(name, types.length).forcePut(types, value);
    }
}
