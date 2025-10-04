package lando.systems.ld58.game.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import lando.systems.ld58.assets.AnimType;
import lando.systems.ld58.game.Components;
import lando.systems.ld58.game.Constants;
import lando.systems.ld58.game.Signals;
import lando.systems.ld58.game.components.*;
import lando.systems.ld58.game.components.collision.CollisionRect;
import lando.systems.ld58.game.components.collision.CollisionResponse;
import lando.systems.ld58.game.signals.AnimationEvent;
import lando.systems.ld58.game.signals.CollisionEvent;
import lando.systems.ld58.utils.Calc;
import lando.systems.ld58.utils.Util;

public class CollisionHandlerSystem extends EntitySystem implements Listener<CollisionEvent> {

    private static final String TAG = CollisionHandlerSystem.class.getSimpleName();

    public CollisionHandlerSystem() {
        Signals.collision.add(this);
    }

    @Override
    public void receive(Signal<CollisionEvent> signal, CollisionEvent event) {
        if      (event instanceof CollisionEvent.Move)    handleMoveCollision((CollisionEvent.Move) event);
        else if (event instanceof CollisionEvent.Overlap) handleOverlapCollision((CollisionEvent.Overlap) event);
        else Util.warn(TAG, "unhandled collision event type: " + event.getClass().getSimpleName());
    }

    private void handleMoveCollision(CollisionEvent.Move move) {
        // Player on player collision
        if (Components.has(move.mover(),  Player.class)
         && Components.has(move.target(), Player.class)) {
            handlePlayerMoveCollision(move);
        }
    }

    private void handleOverlapCollision(CollisionEvent.Overlap overlap) {
        // TODO...
    }

    private void handlePlayerMoveCollision(CollisionEvent.Move move) {
        //@formatter:off
        // Constants for tuning
        final float BOUNCE_MIN_VELOCITY       = 0.9f * Constants.JUMP_ACCEL_SINGLE; // Close to 'normal' first jump
        final float UPWARD_VELOCITY_TRANSFER  = 0.3f; // 30% of target's upward velocity
        final float UPWARD_VELOCITY_REDUCTION = 0.5f; // 50% reduction
        final float UPWARD_VELOCITY_BOOST     = 100f;
        final float SIDE_BOUNCE_FACTOR        = 0.8f;
        final float SIDE_BOUNCE_MIN_VELOCITY  = 400f;

        var mover  = move.mover();
        var target = move.target();

        var moverPlayer   = Components.get(mover, Player.class);
        var moverPosition = Components.get(mover, Position.class);
        var moverVelocity = Components.get(mover, Velocity.class);
        var moverCollider = Components.get(mover, Collider.class);
        var moverAnimator = Components.get(mover, Animator.class);
        var moverRect = moverCollider.shape(CollisionRect.class).rectangle(moverPosition);

        var targetPlayer   = Components.get(target, Player.class);
        var targetPosition = Components.get(target, Position.class);
        var targetVelocity = Components.get(target, Velocity.class);
        var targetCollider = Components.get(target, Collider.class);
        var targetAnimator = Components.get(target, Animator.class);
        var targetRect = targetCollider.shape(CollisionRect.class).rectangle(targetPosition);

        // Determine collision direction based on both movement delta and relative positions
        // Check if mover is falling (negative Y velocity) and is above the target
        var moverIsFalling = moverVelocity.value.y < 0;
        var moverIsAboveTarget = moverRect.y >= (targetRect.y + targetRect.height);

        var fromAbove = moverIsFalling && moverIsAboveTarget;
        var fromBelow = move.dir().y > 0; // Mover was moving up
        var fromSide  = move.dir().x != 0 && !fromAbove; // Horizontal collision, but not from above
        //@formatter:on

        // Mover hits target from above - mover bounces up
        if (fromAbove) {
            var bounceVelocity = BOUNCE_MIN_VELOCITY;

            // Add extra bounce if target is moving upward
            if (targetVelocity.value.y > 0) {
                bounceVelocity += targetVelocity.value.y * UPWARD_VELOCITY_TRANSFER;
            }

            moverVelocity.value.y = bounceVelocity;

            // Set jump state as if mover had touched ground and re-jumped because they're bouncing off the target player
            moverPlayer.jumpState(Player.JumpState.JUMPED);

            // Squash/stretch animations
            // TODO: this could be a unique animation, like a jump but with double middle fingers or something
            Signals.animStart.dispatch(new AnimationEvent.Start(moverAnimator, AnimType.MARIO_IDLE));
            Signals.animScale.dispatch(new AnimationEvent.Scale(moverAnimator, 0.8f, 1.2f));
            Signals.animScale.dispatch(new AnimationEvent.Scale(targetAnimator, 1.2f, 0.8f));

            move.response = CollisionResponse.KEEP_VELOCITY;
        }
        // Mover hits target from below - reduce mover's upward velocity, boost target's
        else if (fromBelow) {

            moverVelocity.value.y *= UPWARD_VELOCITY_REDUCTION;
            targetVelocity.value.y += UPWARD_VELOCITY_BOOST;

            // Stretch/squash animations
            Signals.animScale.dispatch(new AnimationEvent.Scale(moverAnimator, 1.2f, 0.8f));
            Signals.animScale.dispatch(new AnimationEvent.Scale(targetAnimator, 0.8f, 1.2f));

            move.response = CollisionResponse.KEEP_VELOCITY;
        }
        // Side collision - bounce away from each other
        else if (fromSide) {
            var moverSpeed    = Math.abs(moverVelocity.value.x);
            var targetSpeed   = Math.abs(targetVelocity.value.x);
            var combinedSpeed = moverSpeed + targetSpeed;

            // Calculate bounce strength based on combined velocity
            var bounceStrength = Math.max(combinedSpeed * SIDE_BOUNCE_FACTOR, SIDE_BOUNCE_MIN_VELOCITY);

            // Faster player bounces less, slower player bounces more
            var moverBounceRatio = targetSpeed / (moverSpeed + targetSpeed + 1f); // +1 to avoid divide by zero
            var targetBounceRatio = moverSpeed / (moverSpeed + targetSpeed + 1f);

            // Apply bounces in opposite directions, ensuring minimum velocity
            int direction = Calc.sign((int) move.dir().x); // Direction mover was going
            var moverBounce = Math.max(-direction * bounceStrength * moverBounceRatio, SIDE_BOUNCE_MIN_VELOCITY);
            var targetBounce = Math.max(direction * bounceStrength * targetBounceRatio, SIDE_BOUNCE_MIN_VELOCITY);

            moverVelocity.value.x = direction > 0 ? -moverBounce : moverBounce;
            targetVelocity.value.x = direction > 0 ? targetBounce : -targetBounce;

            // Side impact animations
            Signals.animScale.dispatch(new AnimationEvent.Scale(moverAnimator, 0.9f, 1.1f));
            Signals.animScale.dispatch(new AnimationEvent.Scale(targetAnimator, 0.9f, 1.1f));

            move.response = CollisionResponse.KEEP_VELOCITY;
        }

        // If target is in grab state, knock them out
        if (targetPlayer.jumpState() == Player.JumpState.GRABBED) {
            targetPlayer.jumpState(Player.JumpState.FALLING);

            // Extra knockback when grabbed
            if (fromAbove) {
                // Knock them down and away
                targetVelocity.value.y = -50f; // Slight downward velocity
                targetVelocity.value.x += Calc.sign(move.dir().x) * 75f; // Push away horizontally
            }

            // Visual feedback for grab break
            Signals.animScale.dispatch(new AnimationEvent.Scale(targetAnimator, 1.5f, 0.7f));
        }
    }
}
