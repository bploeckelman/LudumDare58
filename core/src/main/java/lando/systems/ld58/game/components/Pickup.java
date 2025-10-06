package lando.systems.ld58.game.components;

import com.badlogic.ashley.core.Component;

public class Pickup implements Component {

    public enum Type { COIN, RELIC_PLUNGER, RELIC_TORCH, RELIC_WRENCH }

    public final Type type;

    private Pickup(Type type) {
        this.type = type;
    }

    public static Pickup coin()    { return new Pickup(Type.COIN); }
    public static Pickup plunger() { return new Pickup(Type.RELIC_PLUNGER); }
    public static Pickup torch()   { return new Pickup(Type.RELIC_TORCH); }
    public static Pickup wrench()  { return new Pickup(Type.RELIC_WRENCH); }
}
