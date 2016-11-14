package core;

final class BoxingFunction implements Function {
    private final Callable callable;
    private final Symbol returnType;
    private final Symbol[] parameters;
    Function withConversions = this;
    Function withoutConversions = this;

    public BoxingFunction(Callable callable, Symbol type, Symbol[] parameters) {
        this.callable = callable;
        this.returnType = type;
        this.parameters = parameters;
    }

    public Symbol returnType() {
        return returnType;
    }

    public Symbol[] parameters() {
        return parameters;
    }

    public Value call(World world, Value... args) {
        return callable.call(world, args);
    }

    public Function withArgumentConversions() {
        return withConversions;
    }

    public Function withoutArgumentConversions() {
        return withoutConversions;
    }
}
