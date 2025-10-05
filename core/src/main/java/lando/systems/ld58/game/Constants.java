package lando.systems.ld58.game;

import com.badlogic.gdx.math.Rectangle;

public class Constants {

    public static final float MOVE_SPEED_MAX_GROUND = 250f;
    public static final float MOVE_SPEED_MAX_AIR = 500f;

    public static final float MOVE_ACCEL_GROUND = 500f;
    public static final float MOVE_ACCEL_AIR = 250f;

    public static final float JUMP_ACCEL_SINGLE = 200f; // 400 ~= 10 tiles, 250 ~= 4 tiles (16px)
    public static final float JUMP_HELD_ACCEL = 20f;
    public static final float JUMP_ACCEL_DOUBLE = 350f;

    public static final float FRICTION_MAX_GROUND = 1000f;
    public static final float FRICTION_MAX_AIR = 0f;
    public static final float FRICTION_CLIMBER = 200f;

    public static final float GRAVITY = -500f;

    public static final int BACKROUND_Z_LEVEL = -100;
    public static final int FOREGROUND_Z_LEVEL = 100;

    public static final Rectangle BILLY_ANIMATOR_BOUNDS = new Rectangle(16, 0, 32, 32);
    public static final Rectangle BILLY_COLLIDER_BOUNDS = new Rectangle(-8, 0, 16, 20);

    public static final Rectangle GOOMBA_ANIMATOR_BOUNDS = new Rectangle(8, 0, 16, 16);
    public static final Rectangle GOOMBA_COLLIDER_BOUNDS = new Rectangle(-5, 0, 10, 14);

    public static final Rectangle MARIO_ANIMATOR_BOUNDS = new Rectangle(17, 0, 34, 34);
    public static final Rectangle MARIO_COLLIDER_BOUNDS = new Rectangle(-6, 0, 12, 31);

    public static final Rectangle BOWSER_ANIMATOR_BOUNDS = new Rectangle(42, 0, 84, 162);
    public static final Rectangle BOWSER_COLLIDER_BOUNDS = new Rectangle(-27, 0, 54, 135);
}
