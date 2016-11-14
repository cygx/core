package core;
import java.util.*;

class CandiTree {
    private static final class Node {
        final Symbol key;
        TypedCallable value;
        final List<Node> children = new ArrayList<>(0);

        Node(Symbol key) {
            this.key = key;
        }
    }

    private Node root = new Node(null);

    private Node makeNode(Symbol[] types) {
        Node node = root;
        LOOP: for(Symbol key : types) {
            for(Node child : node.children) {
                if(child.key == key) {
                    node = child;
                    continue LOOP;
                }
            }

            Node child = new Node(key);
            node.children.add(child);
            node = child;
        }

        return node;
    }

    public void put(Symbol[] types, TypedCallable value) {
        Node node = makeNode(types);
        if(node.value != null)
            throw new IllegalStateException();

        node.value = value;
    }

    public void forcePut(Symbol[] types, TypedCallable value) {
        makeNode(types).value = value;
    }

    public TypedCallable get(Symbol[] types) {
        Node node = root;
        LOOP: for(Symbol key : types) {
            for(Node child : node.children) {
                if(child.key == key) {
                    node = child;
                    continue LOOP;
                }
            }

            return null;
        }

        return node.value;
    }

    public TypedCallable fuzzyGet(Symbol[] types, World world) {
        return fuzzyGet(root, types, 0, world);
    }

    private TypedCallable fuzzyGet(Node node, Symbol[] types, int pos,
            World world) {
        if(pos == types.length)
            return node.value;

        for(Node child : node.children) {
            if(world.canConvert(types[pos], child.key)) {
                TypedCallable found = fuzzyGet(child, types, pos + 1, world);
                if(found != null) return found;
            }
        }

        return null;
    }
}
