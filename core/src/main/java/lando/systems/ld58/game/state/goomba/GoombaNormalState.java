package lando.systems.ld58.game.state.goomba;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import lando.systems.ld58.assets.AnimType;
import lando.systems.ld58.assets.SoundType;
import lando.systems.ld58.game.Constants;
import lando.systems.ld58.game.Signals;
import lando.systems.ld58.game.components.Player;
import lando.systems.ld58.game.signals.AnimationEvent;
import lando.systems.ld58.game.signals.AudioEvent;
import lando.systems.ld58.game.signals.CooldownEvent;
import lando.systems.ld58.game.state.PlayerState;
import lando.systems.ld58.utils.Calc;

public class GoombaNormalState extends PlayerState {

    private boolean isGrounded;
    private boolean wasGrounded;

    public GoombaNormalState(Engine engine, Entity entity) {
        super(engine, entity);
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        wasGrounded = isGrounded;
        isGrounded = collisionCheckSystem.onGround(entity);

        var justLanded = isGrounded && !wasGrounded;
        if (justLanded) {
            player().jumpState(Player.JumpState.GROUNDED);
            Signals.animScale.dispatch(new AnimationEvent.Scale(animator(), 1.2f, 0.8f));
        }

        // Set the correct 'base' animation, may be overridden later
        if (isGrounded) {
            // Only play the walk animation if the player is actually moving,
            // play idle if they're pushing against a collider that's blocking them
            var moving           = (velocity().value.x != 0);
            var wantsToMove      = (input().moveDirX != 0);
            var colliderBlocking = collisionCheckSystem.check(entity, Calc.sign(input().moveDirX), 0);

            var actuallyMoving = (moving && wantsToMove && !colliderBlocking);

            var animType = actuallyMoving ? AnimType.GOOMBA_NORMAL_WALK : AnimType.GOOMBA_NORMAL_IDLE;
            Signals.animStart.dispatch(new AnimationEvent.Play(animator(), animType));
        }

        handleMovement(delta);
        handleJumping();
    }

    private void handleMovement(float delta) {
        var input     = input();
        var animator  = animator();
        var velocity  = velocity();

        // Update horizontal speed based on input
        var accel = isGrounded
            ? input.moveDirX * Constants.MOVE_ACCEL_GROUND
            : input.moveDirX * Constants.MOVE_ACCEL_AIR;
        velocity.value.x += accel * delta;

        // Constrain horizontal speed
        var maxSpeed = isGrounded ? Constants.MOVE_SPEED_MAX_GROUND : Constants.MOVE_SPEED_MAX_AIR;
        if (Calc.abs(velocity.value.x) > maxSpeed) {
            // NOTE: this version is a hard cap on max speed
            velocity.value.x = animator.facing * maxSpeed;

            // NOTE: this version rapidly lerps to reach the maxSpeed over some small finite time
            //velocity.value.x = Calc.approach(velocity.value.x, animator.facing * maxSpeed, 2000f * delta);
        }

        // Apply friction to slow down, if applicable
        if (input.moveDirX == 0) {
            var friction = isGrounded ? Constants.FRICTION_MAX_GROUND : Constants.FRICTION_MAX_AIR;
            velocity.value.x = Calc.approach(velocity.value.x, 0, friction * delta);
        }

        // Update animator's facing based on input, otherwise leave as-is
        if (input.moveDirX != 0) {
            var newFacing = Calc.sign(input.moveDirX);
            Signals.animFacing.dispatch(new AnimationEvent.Facing(animator, newFacing));
        }
    }

    private void handleJumping() {
        var player    = player();
        var input     = input();
        var animator  = animator();
        var cooldowns = cooldowns();
        var velocity  = velocity();

        var jumpRequested = input.wasActionPressed;
        if (jumpRequested && cooldowns.isReady("jump")) {
            if (isGrounded && player.jumpState() == Player.JumpState.GROUNDED) {
                // start a new jump!
                player.jumpState(Player.JumpState.JUMPED);

                velocity.value.y = Constants.JUMP_ACCEL_SINGLE;

                Signals.cooldownReset.dispatch(new CooldownEvent.Reset(cooldowns, "jump"));
                Signals.animScale.dispatch(new AnimationEvent.Scale(animator, 0.66f, 1.33f));
                Signals.animStart.dispatch(new AnimationEvent.Start(animator, AnimType.GOOMBA_NORMAL_IDLE));
                Signals.playSound.dispatch(new AudioEvent.PlaySound(SoundType.CLIMBER_BOUNCE));
            }
            else if (player.jumpState() == Player.JumpState.JUMPED) {
                player.jumpState(Player.JumpState.GRABBED);

                velocity.stop();

                Signals.cooldownReset.dispatch(new CooldownEvent.Reset(cooldowns, "jump"));
                Signals.animScale.dispatch(new AnimationEvent.Scale(animator, 1.2f, 1.2f));
                Signals.animStart.dispatch(new AnimationEvent.Start(animator, AnimType.GOOMBA_RAGE_IDLE));
                Signals.playSound.dispatch(new AudioEvent.PlaySound(SoundType.CLIMBER_BOUNCE));
            }
            else if (player.jumpState() == Player.JumpState.GRABBED) {
                player.jumpState(Player.JumpState.DOUBLE_JUMPED);

                velocity.value.y = Constants.JUMP_ACCEL_DOUBLE;

                Signals.cooldownReset.dispatch(new CooldownEvent.Reset(cooldowns, "jump"));
                Signals.animScale.dispatch(new AnimationEvent.Scale(animator, 0.66f, 1.33f));
                Signals.animStart.dispatch(new AnimationEvent.Start(animator, AnimType.GOOMBA_NORMAL_IDLE));
                Signals.playSound.dispatch(new AudioEvent.PlaySound(SoundType.CLIMBER_BOUNCE));
            }
            // ...otherwise if it's a double jump do nothing until the player is grounded again
        }

        // no movement when grabbing
        if (player.jumpState() == Player.JumpState.GRABBED) {
            velocity.stop();

            // For now just boop the player a bit to indicate they tried to move
            if (input.moveDirX != 0) {
                Signals.animScale.dispatch(new AnimationEvent.Scale(animator, 1.1f, animator.scale.y));
            }
        }
    }
}
