package lando.systems.ld58.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld58.game.Constants;
import lando.systems.ld58.utils.Calc;

import java.util.Optional;

public class Velocity implements Component {

    public static final ComponentMapper<Velocity> mapper = ComponentMapper.getFor(Velocity.class);

    public static Optional<Velocity> get(Entity entity) {
        return Optional.ofNullable(mapper.get(entity));
    }

    public final Vector2 value = new Vector2();
    public final Vector2 remainder;

    public float maxFallSpeed = Constants.MOVE_SPEED_MAX_FALL;
    public float maxHorizontalSpeedAir = Constants.MOVE_SPEED_MAX_AIR;
    public float maxHorizontalSpeedGround = Constants.MOVE_SPEED_MAX_GROUND;


    public Velocity(float x, float y) {
        this(new Vector2(x, y));
    }

    public Velocity(Velocity velocity) {
        this.value.set(velocity.value);
        this.remainder = velocity.remainder;
    }

    public Velocity(Vector2 value) {
        this.value.set(value);
        this.remainder = Vector2.Zero.cpy();
    }

    public float x() { return value.x; }
    public float y() { return value.y; }

    public int xSign() { return (int) Calc.sign(value.x); }
    public int ySign() { return (int) Calc.sign(value.y); }

    public Velocity set (Vector2 v) {
        value.set(v);
        return this;
    }

    public Velocity set(float x, float y) {
        value.set(x, y);
        return this;
    }

    public void stop() {
        stopX();
        stopY();
    }

    public void stopX() {
        value.x = 0f;
        remainder.x = 0f;
    }

    public void stopY() {
        value.y = 0f;
        remainder.y = 0f;
    }

    public void invertX() {
        value.x *= -1f;
        remainder.x = 0f;
    }

    public void invertY() {
        value.y *= -1f;
        remainder.y = 0f;
    }
}
