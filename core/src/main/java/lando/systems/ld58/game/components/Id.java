package lando.systems.ld58.game.components;

import com.badlogic.ashley.core.Component;
import com.github.tommyettinger.digital.Stringf;

public class Id implements Component {

    private static final String TAG = Id.class.getSimpleName();

    public static final Id UNKNOWN = new Id(-1);

    private static int NEXT = 1;

    public final int id;

    public Id() {
        this(NEXT++);
    }

    private Id(int id) {
        this.id = id;
    }

    public int id() { return id; }

    @Override
    public String toString() {
        return Stringf.format("%s{id=%d}", TAG, id);
    }
}
