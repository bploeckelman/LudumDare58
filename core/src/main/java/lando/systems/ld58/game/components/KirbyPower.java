package lando.systems.ld58.game.components;

import com.badlogic.ashley.core.Component;
import lando.systems.ld58.assets.AnimType;

public class KirbyPower implements Component {
    public enum PowerType {KOOPA, LAKITU, HAMMER, BULLET, SUN}

    public PowerType powerType;

    public KirbyPower(PowerType powerType) {
        this.powerType = powerType;
    }

    public AnimType getOriginalEnemyIdleAnimType() {
        switch (powerType) {
            case KOOPA:  return AnimType.KOOPA_IDLE;
            case LAKITU: return AnimType.LAKITU_IDLE;
            case HAMMER: return AnimType.HAMMER_BRO_IDLE;
            case BULLET: return AnimType.BULLET_BILL_IDLE;
            case SUN:    return AnimType.ANGRY_SUN;
            default:     return AnimType.COIN_BLOCK;
        }
    }

    public AnimType getBillyEnemyWalkAnimType() {
        switch (powerType) {
//            case KOOPA:  return AnimType.BILLY_KOOPA_WALK;
            case LAKITU: return AnimType.BILLY_LAKITU_WALK;
//            case HAMMER: return AnimType.BILLY_HAMMER_BRO_WALK;
            case BULLET: return AnimType.BILLY_BULLET_BILL_WALK;
//            case SUN:    return AnimType.BILLY_ANGRY_SUN;
            default:     return AnimType.COIN_BLOCK;
        }
    }

    public AnimType getBillyEnemyActionAnimType() {
        switch (powerType) {
//            case KOOPA:  return AnimType.BILLY_KOOPA_ACTION;
            case LAKITU: return AnimType.BILLY_LAKITU_ACTION;
//            case HAMMER: return AnimType.HAMMER_BRO_ACTION;
            case BULLET: return AnimType.BILLY_BULLET_BILL_ACTION;
//            case SUN:    return AnimType.BILLY_ANGRY_SUN;
            default:     return AnimType.COIN_BLOCK;
        }
    }
}
