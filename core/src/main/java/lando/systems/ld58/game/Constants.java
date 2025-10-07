package lando.systems.ld58.game;

import com.badlogic.gdx.math.Rectangle;

public class Constants {

    public static final float MOVE_SPEED_MAX_GROUND = 250f;
    public static final float MOVE_SPEED_MAX_AIR = 500f;
    public static final float MOVE_SPEED_MAX_FALL = 500f;

    public static final float MOVE_ACCEL_GROUND = 500f;
    public static final float MOVE_ACCEL_AIR = 250f;

    public static final float JUMP_ACCEL_SINGLE = 200f; // 400 ~= 10 tiles, 250 ~= 4 tiles (16px)
    public static final float JUMP_HELD_ACCEL = 20f;
    public static final float JUMP_ACCEL_DOUBLE = 350f;

    public static final float FRICTION_MAX_GROUND = .01f;
    public static final float FRICTION_MAX_AIR = .2f;
    public static final float FRICTION_CLIMBER = 0.95f;//200f;

    public static final float DEFAULT_GRAVITY = -500f;

    public static final int Z_DEPTH_BACKGROUND = -100;
    public static final int Z_DEPTH_DEFAULT    = 0;
    public static final int Z_DEPTH_FOREGROUND = 100;

    public static final Rectangle BILLY_ANIMATOR_BOUNDS = new Rectangle(16, 0, 32, 32);
    public static final Rectangle BILLY_COLLIDER_BOUNDS = new Rectangle(-8, 0, 16, 20);

    public static final Rectangle GOOMBA_ANIMATOR_BOUNDS = new Rectangle(8, 0, 16, 16);
    public static final Rectangle GOOMBA_COLLIDER_BOUNDS = new Rectangle(-5, 0, 10, 14);

    public static final Rectangle MARIO_ANIMATOR_BOUNDS = new Rectangle(17, 0, 34, 34);
    public static final Rectangle MARIO_COLLIDER_BOUNDS = new Rectangle(-6, 0, 12, 31);

    public static final Rectangle BOWSER_ANIMATOR_BOUNDS = new Rectangle(42, 0, 84, 162);
    public static final Rectangle BOWSER_COLLIDER_BOUNDS = new Rectangle(-27, 0, 54, 135);

    public static final float scale = 2f / 3f;
    public static final float louWidth = scale * 192f;
    public static final float louHeight = scale * 176f;
    public static final Rectangle CAPTAIN_LOU_ANIMATOR_BOUNDS = new Rectangle(louWidth / 2f, scale * 15f, louWidth, louHeight);
    public static final Rectangle CAPTAIN_LOU_COLLIDER_BOUNDS = new Rectangle(-(scale * 150f) / 2f, 0f, scale * 150f, scale * 85f);

    public static final Rectangle GOOMBA_CYBORG_ANIMATOR_BOUNDS = new Rectangle(16, 5, 32, 32);
    public static final Rectangle GOOMBA_CYBORG_COLLIDER_BOUNDS = new Rectangle(-8, 0, 16, 20);

    public static final Rectangle MISTY_ANIMATOR_BOUNDS = new Rectangle(16, 0, 32, 32);
    public static final Rectangle MISTY_COLLIDER_BOUNDS = new Rectangle(-8, 0, 16, 20);
}
