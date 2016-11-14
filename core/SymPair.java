package core;

final class SymPair {
    public final Symbol a;
    public final Symbol b;
    private final int hashCode;

    public SymPair(Symbol a, Symbol b) {
        this.a = a;
        this.b = b;
        this.hashCode = 31 * a.hashCode() + b.hashCode();
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof SymPair)) return false;
        SymPair pair = (SymPair)obj;
        return pair.a == a && pair.b == b;
    }
}
