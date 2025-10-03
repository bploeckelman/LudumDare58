package lando.systems.ld58.game;

import com.badlogic.gdx.math.Rectangle;

public class Constants {

    public static final float MOVE_SPEED_MAX_GROUND = 500f;
    public static final float MOVE_SPEED_MAX_AIR = 1000f;

    public static final float MOVE_ACCEL_GROUND = 1000f;
    public static final float MOVE_ACCEL_AIR = 500f;

    public static final float JUMP_ACCEL_SINGLE = 800f;
    public static final float JUMP_ACCEL_DOUBLE = 900f;

    public static final float FRICTION_MAX_GROUND = 1000f;
    public static final float FRICTION_MAX_AIR = 0f;
    public static final float FRICTION_CLIMBER = 200f;

    public static final float GRAVITY = -1000f;

    public static final Rectangle CLIMBER_ANIMATOR_BOUNDS = new Rectangle(48, 0, 96, 128);
    public static final Rectangle CLIMBER_COLLIDER_BOUNDS = new Rectangle(-17, 0, 34, 96);

    public static final Rectangle SQUATCH_ANIMATOR_BOUNDS = new Rectangle(80, 0, 160, 224);
    public static final Rectangle SQUATCH_COLLIDER_BOUNDS = new Rectangle(-50, 0, 100, 174);

    public static final Rectangle MARIO_ANIMATOR_BOUNDS = new Rectangle(28, 0, 56, 108);
    public static final Rectangle MARIO_COLLIDER_BOUNDS = new Rectangle(-18, 0, 36, 90);

    public static final Rectangle BOWSER_ANIMATOR_BOUNDS = new Rectangle(42, 0, 84, 162);
    public static final Rectangle BOWSER_COLLIDER_BOUNDS = new Rectangle(-27, 0, 54, 135);
}
