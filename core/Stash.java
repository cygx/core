package core;
import java.util.*;

final class Stash extends HashMap<StashKey, CandiTree> {
    public Function get(String name, Symbol[] types) {
        CandiTree tree = get(new StashKey(name, types.length));
        return tree == null ? null : tree.get(types);
    }

    public Function fuzzyGet(String name, Symbol[] types, World world) {
        CandiTree tree = get(new StashKey(name, types.length));
        return tree == null ? null : tree.fuzzyGet(types, world);
    }

    private CandiTree makeTree(String name, int arity) {
        StashKey key = new StashKey(name, arity);
        CandiTree tree = get(key);
        if(tree == null) put(key, tree = new CandiTree());
        return tree;
    }

    public void put(String name, Symbol[] types, Function value) {
        makeTree(name, types.length).put(types, value);
    }

    public void forcePut(String name, Symbol[] types, Function value) {
        makeTree(name, types.length).forcePut(types, value);
    }
}
