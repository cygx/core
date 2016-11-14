package core;
import java.util.*;

public  class World {
    private final Map<SymPair, UnaryCallable> conversions = new HashMap<>();
    private final Map<Symbol, String> symbols = new HashMap<>();
    private final Map<String, Namespace> namespaces = new HashMap<>();

    public Symbol createSymbol(String name) {
        Symbol sym = new Symbol();
        symbols.put(sym, name);
        return sym;
    }

    public Namespace namespace(String name) {
        Namespace space = namespaces.get(name);
        if(space == null)
            namespaces.put(name, space = new Namespace(name, this));

        return space;
    }

    public void registerSymbol(Symbol sym, String name) {
        if(symbols.put(sym, name) != null)
            throw new IllegalStateException();
    }

    public void registerConversion(Symbol src, Symbol dest,
            UnaryCallable conversion) {
        if(conversions.put(new SymPair(src, dest), conversion) != null)
            throw new IllegalStateException();
    }

    public Value convert(Value src, Symbol destType) {
        Symbol srcType = src.type();
        if(srcType == destType) return src;

        UnaryCallable converter = conversions.get(new SymPair(srcType, destType));
        return converter == null ? null : converter.call(this, src);
    }

    public boolean canConvert(Symbol srcType, Symbol destType) {
        return srcType == destType
            || conversions.containsKey(new SymPair(srcType, destType));
    }
}
