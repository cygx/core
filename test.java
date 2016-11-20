import core.*;
import java.util.*;

class test {
    public static void main(String[] args) {
        System.out.println(new BoolArray(true, true, false).gist());

        World world = new World();
        world.registerSymbol(DoubleValue.type, "double");
        world.registerSymbol(StringValue.type, "string");

        Namespace core = world.namespace("core");
        core.declare("double", DoubleValue.type);
        core.declare("string", StringValue.type);

        Symbol pi = world.createSymbol("pi");
        DoubleValue piDouble = new DoubleValue(3.14);
        StringValue piString = new StringValue("pi");
        world.registerConversion(pi, DoubleValue.type, (w, v) -> piDouble);
        world.registerConversion(pi, StringValue.type, (w, v) -> piString);

        Namespace math = world.namespace("math");
        math.declare("pi", pi);

        System.out.println(world.convert(pi, DoubleValue.type));
        System.out.println(world.convert(pi, StringValue.type));
        System.out.println(math.eval("pi").gist());
        System.out.println(math.lookup("pi", DoubleValue.type));
        math.declare("pi", pi, DoubleValue.type, (w, v) -> null);
        System.out.println(math.lookup("pi", DoubleValue.type));

        Symbol void_ = new Symbol();
        Symbol dog = new Symbol();
        VTable vt = new VTable(dog, (w, v) -> {
            System.out.println("Woof!");
            return void_;
        });

        world.registerConversion(pi, dog, (w, v) -> new Polymorph(vt, v));

        Namespace main = world.namespace("main");
        main.declare("bark", void_, dog, (w, v) -> {
            Polymorph poly = (Polymorph)v;
            return poly.virtualCall(0, w, poly.target);
        });

        main.eval("bark", pi);

        main.declare("say", void_, DoubleValue.type, (w, v) -> {
            System.out.println("a double: " + v.gist());
            return void_;
        });

        main.declare("say", void_, StringValue.type, (w, v) -> {
            System.out.println("a string: " + v.gist());
            return void_;
        });

        main.eval("say", new DoubleValue(0.5));
        main.eval("say", new StringValue("0.5"));

        world.registerSymbol(MutableDoubleValue.type, "@double");
        world.registerConversion(MutableDoubleValue.type, DoubleValue.type,
            (w, v) -> new DoubleValue(((MutableDoubleValue)v).value));

        main.eval("say", new MutableDoubleValue(42));

        TupleLayout dblPair = new TupleLayout(
            DoubleValue.type, DoubleValue.type);

        Symbol[] parameters = {
            dblPair.tupleType, DoubleValue.type, DoubleValue.type
        };

        main.declare("create", dblPair.tupleType, parameters, (w, a) -> {
            Tuple tuple = new Tuple(dblPair);
            tuple.elements[0] = a[1];
            tuple.elements[1] = a[2];
            return tuple;
        });

        System.out.println(Arrays.toString(
            ((Tuple)main.eval("create", dblPair.tupleType, new DoubleValue(-1),
                new DoubleValue(0.5))).elements));
    }
}
