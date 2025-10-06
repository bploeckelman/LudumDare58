package lando.systems.ld58.game.state.goomba;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld58.assets.AnimType;
import lando.systems.ld58.assets.SoundType;
import lando.systems.ld58.game.Components;
import lando.systems.ld58.game.Constants;
import lando.systems.ld58.game.Signals;
import lando.systems.ld58.game.components.KirbyPower;
import lando.systems.ld58.game.components.Player;
import lando.systems.ld58.game.components.renderable.KirbyShaderRenderable;
import lando.systems.ld58.game.signals.AnimationEvent;
import lando.systems.ld58.game.signals.AudioEvent;
import lando.systems.ld58.game.signals.CooldownEvent;
import lando.systems.ld58.game.state.PlayerState;
import lando.systems.ld58.utils.Calc;

public class GoombaNormalState extends PlayerState {

    public static float COYOTE_TIME = .25f;

    private boolean isGrounded;
    private boolean wasGrounded;
    private float lastOnGround;
    private float jumpTime;

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
            lastOnGround = 0;
            // Only play the walk animation if the player is actually moving,
            // play idle if they're pushing against a collider that's blocking them
            var moving           = (velocity().value.x != 0);
            var wantsToMove      = (input().moveDirX != 0);
            var colliderBlocking = collisionCheckSystem.check(entity, Calc.sign(input().moveDirX), 0);

            var actuallyMoving = (moving && wantsToMove && !colliderBlocking);

            var animType = actuallyMoving ? AnimType.BILLY_WALK : AnimType.BILLY_IDLE;
            Signals.animStart.dispatch(new AnimationEvent.Play(animator(), animType));
        } else {
            lastOnGround += delta;
        }

        handleMovement(delta);
        handleJumping(delta);
        handleSuckOff(delta);
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

    private void handleJumping(float delta) {
        var player    = player();
        var input     = input();
        var animator  = animator();
        var cooldowns = cooldowns();
        var velocity  = velocity();

        jumpTime += delta;

        var jumpRequested = input.wasJumpJustPressed;
        if (jumpRequested && cooldowns.isReady("jump")) {
            if ((isGrounded || lastOnGround < COYOTE_TIME) && player.jumpState() == Player.JumpState.GROUNDED) {
                // start a new jump!
                jumpTime = 0;
                player.jumpState(Player.JumpState.JUMPED);

                velocity.value.y = Constants.JUMP_ACCEL_SINGLE;

                Signals.cooldownReset.dispatch(new CooldownEvent.Reset(cooldowns, "jump"));
                Signals.animScale.dispatch(new AnimationEvent.Scale(animator, 0.66f, 1.33f));
                Signals.animStart.dispatch(new AnimationEvent.Start(animator, AnimType.BILLY_JUMP));
                Signals.playSound.dispatch(new AudioEvent.PlaySound(SoundType.JUMP));
            }
            else if (player.jumpState() == Player.JumpState.JUMPED) {
                player.jumpState(Player.JumpState.GRABBED);

                velocity.stop();

                Signals.cooldownReset.dispatch(new CooldownEvent.Reset(cooldowns, "jump"));
                Signals.animScale.dispatch(new AnimationEvent.Scale(animator, 1.2f, 1.2f));
                Signals.animStart.dispatch(new AnimationEvent.Start(animator, AnimType.BILLY_YELL));
                Signals.playSound.dispatch(new AudioEvent.PlaySound(SoundType.JUMP));
            }
            else if (player.jumpState() == Player.JumpState.GRABBED) {
                player.jumpState(Player.JumpState.DOUBLE_JUMPED);

                velocity.value.y = Constants.JUMP_ACCEL_DOUBLE;

                Signals.cooldownReset.dispatch(new CooldownEvent.Reset(cooldowns, "jump"));
                Signals.animScale.dispatch(new AnimationEvent.Scale(animator, 0.66f, 1.33f));
                Signals.animStart.dispatch(new AnimationEvent.Start(animator, AnimType.BILLY_JUMP));
                Signals.playSound.dispatch(new AudioEvent.PlaySound(SoundType.JUMP));
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

        if (player.jumpState() == Player.JumpState.JUMPED) {
            if (input.isJumpHeld || input.wasControllerJumpButtonDown) {
                float jumpAccelAmount = MathUtils.clamp(.5f - jumpTime, 0f, 1f);
                velocity.value.y += jumpAccelAmount * Constants.JUMP_HELD_ACCEL;
            }
        }
    }

    private void handleSuckOff(float delta) {
        // if you don't have a power you can suck a guy off
        var kirby = Components.optional(entity, KirbyShaderRenderable.class).orElse(null);
        var power = Components.optional(entity, KirbyPower.class).orElse(null);
        if (kirby == null) return;


        if (power == null) {
            // no power right now
            if (input().isDownHeld) {
                kirby.targetStrength = 1f;
                var enemies = engine.getEntitiesFor(Family.one(KirbyPower.class).get());
            } else {
                kirby.targetStrength = 0f;
            }
        } else {
            // You have a power
            kirby.targetStrength = 0;
        }

    }
}
