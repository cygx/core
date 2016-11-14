package core;

final class StashKey {
    public final String name;
    public final int arity;
    private final int hashCode;

    public StashKey(String name, int arity) {
        this.name = name;
        this.arity = arity;
        this.hashCode = 31 * arity + name.hashCode();
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof StashKey)) return false;
        StashKey key = (StashKey)obj;
        return key.name.equals(name) && key.arity == arity;
    }
}
