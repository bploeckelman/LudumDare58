package lando.systems.ld58.game.components;

import com.badlogic.ashley.core.Component;

public class KirbyPower implements Component {
    public enum PowerType {KOOPA, LAKITU, HAMMER, BULLET, SUN}

    public PowerType powerType;

    public KirbyPower(PowerType powerType) {
        this.powerType = powerType;
    }
}
