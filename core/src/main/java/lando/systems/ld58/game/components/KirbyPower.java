package lando.systems.ld58.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lando.systems.ld58.assets.AnimType;

public class KirbyPower implements Component {
    public enum PowerType {KOOPA, LAKITU, HAMMER, BULLET, SUN}

    public PowerType powerType;

    public KirbyPower(PowerType powerType) {
        this.powerType = powerType;
    }

    public AnimType getWalkAnimation() {
        switch (powerType) {
            case KOOPA:
                break;
            case LAKITU:
                return AnimType.BILLY_LAKITU_WALK;
            case HAMMER:
                break;
            case BULLET:
                return AnimType.BILLY_BULLET_BILL_WALK;
            case SUN:
                break;
        }
        return AnimType.COIN_BLOCK;
    }

    public AnimType getActionAnimation() {
        switch (powerType) {
            case KOOPA:
                break;
            case LAKITU:
                return AnimType.BILLY_LAKITU_ACTION;
            case HAMMER:
                break;
            case BULLET:
                return AnimType.BILLY_BULLET_BILL_ACTION;
            case SUN:
                break;
        }

        return AnimType.COIN_BLOCK;
    }
}
