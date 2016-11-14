package core;
import java.util.concurrent.atomic.AtomicInteger;

public final class Symbol implements Value, Comparable<Symbol> {
    private static final AtomicInteger nextId = new AtomicInteger();

    public final int id;

    public Symbol() {
        this.id = nextId.getAndIncrement();
    }

    public Symbol type() {
        return this;
    }

    @Override
    public String toString() {
        return String.format("@%x", id);
    }

    public int compareTo(Symbol sym) {
        return id - sym.id;
    }
}
