package core;
import java.util.concurrent.atomic.AtomicInteger;

public final class Symbol implements Value {
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
}
