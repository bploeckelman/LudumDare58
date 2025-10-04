package lando.systems.ld58.game;

import com.badlogic.gdx.math.Rectangle;

public class Constants {

    public static final float MOVE_SPEED_MAX_GROUND = 250f;
    public static final float MOVE_SPEED_MAX_AIR = 500f;

    public static final float MOVE_ACCEL_GROUND = 500f;
    public static final float MOVE_ACCEL_AIR = 250f;

    public static final float JUMP_ACCEL_SINGLE = 400f;
    public static final float JUMP_ACCEL_DOUBLE = 500f;

    public static final float FRICTION_MAX_GROUND = 1000f;
    public static final float FRICTION_MAX_AIR = 0f;
    public static final float FRICTION_CLIMBER = 200f;

    public static final float GRAVITY = -500f;

    public static final Rectangle GOOMBA_ANIMATOR_BOUNDS = new Rectangle(8, 0, 16, 16);
    public static final Rectangle GOOMBA_COLLIDER_BOUNDS = new Rectangle(-6, 0, 12, 16);

    public static final Rectangle MARIO_ANIMATOR_BOUNDS = new Rectangle(28, 0, 56, 108);
    public static final Rectangle MARIO_COLLIDER_BOUNDS = new Rectangle(-18, 0, 36, 90);

    public static final Rectangle BOWSER_ANIMATOR_BOUNDS = new Rectangle(42, 0, 84, 162);
    public static final Rectangle BOWSER_COLLIDER_BOUNDS = new Rectangle(-27, 0, 54, 135);
}
