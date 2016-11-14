package core;

interface CandiStore {
    void put(Symbol[] types, Function value);
    void forcePut(Symbol[] types, Function value);
    Function get(Symbol[] types);
    Function fuzzyGet(Symbol[] types, World world);
}
