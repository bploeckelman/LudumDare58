package lando.systems.ld58.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import lando.systems.ld58.game.Components;
import lando.systems.ld58.game.Signals;
import lando.systems.ld58.game.components.*;
import lando.systems.ld58.game.components.collision.CollisionMask;
import lando.systems.ld58.game.signals.CollisionEvent;
import lando.systems.ld58.utils.Calc;
import lando.systems.ld58.utils.FramePool;

public class MovementSystem extends IteratingSystem {

    public static final int PRIORITY = PlayerStateSystem.PRIORITY + 10;

    private static final String TAG = MovementSystem.class.getSimpleName();

    private CollisionCheckSystem collisionCheckSystem;
    private Bounds mapBounds;

    public MovementSystem() {
        super(Family.all(Position.class, Velocity.class).get(), PRIORITY);
        this.collisionCheckSystem = null;
        this.mapBounds = null;
    }

    public void mapBounds(Bounds mapBounds) {
        this.mapBounds = mapBounds;
    }

    /**
     * Public method for simulating movement on a specific entity.
     * This allows AI systems to run physics simulation without affecting the normal update loop.
     */
    public void simulateEntity(Entity entity, float delta) {
        processEntity(entity, delta);
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        if (collisionCheckSystem == null) collisionCheckSystem = getEngine().getSystem(CollisionCheckSystem.class);

        // Get components; required components throw, optional components provide an alternative
        var position  = Components.optional(entity, Position.class).orElseThrow();
        var velocity  = Components.optional(entity, Velocity.class).orElseThrow();
        var friction  = Components.optional(entity, Friction.class).map(Friction::value).orElse(0f);
        var gravity   = Components.optional(entity, Gravity.class).map(Gravity::value).orElse(0f);
        var collider  = Components.optional(entity, Collider.class);

        // Determine whether we're grounded this frame or not
        var ignoreSolids = !collider.map(c -> c.collidesWith(CollisionMask.SOLID)).orElse(false);
        var grounded = collisionCheckSystem.onGround(entity, ignoreSolids);

        // Apply friction
        if (friction > 0 && grounded) {
            // let's change how friction works with 4 hours left in the jam, what could go wrong?
//            velocity.value.x = Calc.approach(velocity.value.x, 0, friction * delta);
            velocity.value.x *= (float) Math.pow(friction, delta);
        }

        // Apply gravity TODO: cap max y-velocity?
        if (gravity != 0 && !grounded) {
            velocity.value.y += gravity * delta;
        }

        if (velocity.value.y < -velocity.maxFallSpeed) {
            velocity.value.y = -velocity.maxFallSpeed;
        }

        // How far should we move this tick, assuming nothing is in the way
        var moveTotal = FramePool.vec2(
            velocity.remainder.x + velocity.value.x * delta,
            velocity.remainder.y + velocity.value.y * delta);

        // Round to integer values because movement happens a pixel at a time
        int movePixelsX = (int) moveTotal.x;
        int movePixelsY = (int) moveTotal.y;

        // Track the fractional remainder so we don't lose movement over time
        velocity.remainder.set(
            moveTotal.x - movePixelsX,
            moveTotal.y - movePixelsY);

        // X-Axis: try to move, a pixel at a time -----------------------------
        if (collider.isEmpty()) {
            position.x += movePixelsX;
        } else {
            // For each pixel, if moving there wouldn't collide then move,
            // otherwise run onHit callback or stop if no callback is set
            var sign = Calc.sign(movePixelsX);
            while (movePixelsX != 0) {
                var hitEntity = collisionCheckSystem.getFirstOverlappingEntity(entity, sign, 0);
                if (hitEntity != null) {
                    var collisionEvent = CollisionEvent.move(entity, hitEntity, sign, 0);
                    Signals.collision.dispatch(collisionEvent);

                    if (collisionEvent.response().stopVelocity()) velocity.stopX();
                    if (collisionEvent.response().stopMovement()) break;
                }

                movePixelsX -= sign;
                position.x += sign;
            }
        }

        // Y-Axis: try to move, a pixel at a time -----------------------------
        if (collider.isEmpty()) {
            position.y += movePixelsY;
        } else {
            // For each pixel, if moving there wouldn't collide then move,
            // otherwise run onHit callback or stop if no callback is set
            var sign = Calc.sign(movePixelsY);
            while (movePixelsY != 0) {
                var hitEntity = collisionCheckSystem.getFirstOverlappingEntity(entity, 0, sign);
                if (hitEntity != null) {
                    var collisionEvent = CollisionEvent.move(entity, hitEntity, 0, sign);
                    Signals.collision.dispatch(collisionEvent);

                    if (collisionEvent.response().stopVelocity()) velocity.stopY();
                    if (collisionEvent.response().stopMovement()) break;
                }

                movePixelsY -= sign;
                position.y += sign;
            }
        }
    }
}
