package lando.systems.ld58.game.components;

import com.badlogic.ashley.core.Component;
import lando.systems.ld58.assets.AnimType;
import lando.systems.ld58.game.Constants;

public class KirbyPower implements Component {
    public enum PowerType {KOOPA, LAKITU, HAMMER, BULLET, SUN}

    public PowerType powerType;
    public float activeTimer;

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

    public float getGravity() {
        switch (powerType) {
            case LAKITU:
            case SUN:
                return Constants.GRAVITY * .4f;
            case BULLET:
                if (activeTimer > 0) return 0;

        }
        return Constants.GRAVITY;
    }

    public float jumpImpulse() {
        switch (powerType) {
            case SUN:
            case LAKITU:
                return Constants.JUMP_ACCEL_SINGLE * .5f;
            case HAMMER:
            case KOOPA:
            case BULLET:
                return Constants.JUMP_ACCEL_SINGLE;
        }
        return Constants.JUMP_ACCEL_SINGLE;
    }

    public float jumpHeld() {
        switch (powerType) {
            case SUN:
            case LAKITU:
                return Constants.JUMP_HELD_ACCEL * .5f;
            case HAMMER:
            case KOOPA:
            case BULLET:
                return Constants.JUMP_HELD_ACCEL;
        }
        return Constants.JUMP_HELD_ACCEL;
    }

    public boolean ignoreGround() {
        switch (powerType) {
            case SUN:
            case LAKITU:
                return true;
            case HAMMER:
            case KOOPA:
            case BULLET:
                return false;
        }
        return false;
    }


    public float maxGroundSpeed() {
        switch (powerType) {
            case SUN:
            case LAKITU:
                return Constants.MOVE_SPEED_MAX_GROUND * .3f;
            case HAMMER:
            case KOOPA:
            case BULLET:
                return Constants.MOVE_SPEED_MAX_GROUND;
        }
        return Constants.MOVE_SPEED_MAX_GROUND;
    }

    public float maxAirSpeed() {
        switch (powerType) {
            case SUN:
            case LAKITU:
                    return Constants.MOVE_SPEED_MAX_AIR * .3f;
            case HAMMER:
            case KOOPA:
            case BULLET:
                return Constants.MOVE_SPEED_MAX_AIR;
        }
        return Constants.MOVE_SPEED_MAX_AIR;
    }

    public boolean isActionActive() {
        return activeTimer > 0;
    }
}
