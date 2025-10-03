package lando.systems.ld58.game.components;

import com.badlogic.ashley.core.Component;
import com.github.tommyettinger.digital.Stringf;

public class Name implements Component {

    private static final String TAG = Name.class.getSimpleName();

    public static final Name UNKNOWN = new Name();

    public String name;

    public Name() {
        this("???");
    }

    public Name(String name) {
        this.name = name;
    }

    public String name() { return name; }

    public boolean is(String name) {
        return this.name.equals(name);
    }

    @Override
    public String toString() {
        return Stringf.format("%s{%s}", TAG, name);
    }
}
