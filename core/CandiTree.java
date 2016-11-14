package core;
import java.util.*;

final class CandiTree implements CandiStore {
    private static final class Node {
        final Symbol key;
        Function value;
        final List<Node> children = new ArrayList<>(0);

        Node(Symbol key) {
            this.key = key;
        }
    }

    private Node root = new Node(null);

    public CandiTree() {}

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

    public void put(Symbol[] types, Function value) {
        Node node = makeNode(types);
        if(node.value != null)
            throw new IllegalStateException();

        node.value = value;
    }

    public void forcePut(Symbol[] types, Function value) {
        makeNode(types).value = value;
    }

    public Function get(Symbol[] types) {
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

    public Function fuzzyGet(Symbol[] types, World world) {
        return fuzzyGet(root, types, 0, world);
    }

    private Function fuzzyGet(Node node, Symbol[] types, int pos,
            World world) {
        if(pos == types.length)
            return node.value;

        for(Node child : node.children) {
            if(world.canConvert(types[pos], child.key)) {
                Function found = fuzzyGet(child, types, pos + 1, world);
                if(found != null) return found;
            }
        }

        return null;
    }
}
