package lando.systems.ld58.game.state.goomba;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld58.assets.AnimType;
import lando.systems.ld58.assets.EmitterType;
import lando.systems.ld58.assets.MusicType;
import lando.systems.ld58.assets.SoundType;
import lando.systems.ld58.game.Components;
import lando.systems.ld58.game.Constants;
import lando.systems.ld58.game.Factory;
import lando.systems.ld58.game.Signals;
import lando.systems.ld58.game.components.*;
import lando.systems.ld58.game.components.collision.CollisionMask;
import lando.systems.ld58.game.components.renderable.Animator;
import lando.systems.ld58.game.components.renderable.KirbyShaderRenderable;
import lando.systems.ld58.game.scenes.Scene;
import lando.systems.ld58.game.signals.AnimationEvent;
import lando.systems.ld58.game.signals.AudioEvent;
import lando.systems.ld58.game.signals.CooldownEvent;
import lando.systems.ld58.game.state.PlayerState;
import lando.systems.ld58.particles.effects.SpitEffect;
import lando.systems.ld58.particles.effects.SuckEffect;
import lando.systems.ld58.screens.BaseScreen;
import lando.systems.ld58.utils.Calc;
import lando.systems.ld58.utils.Time;
import lando.systems.ld58.utils.Util;

public class GoombaNormalState extends PlayerState {

    private static final String TAG = GoombaNormalState.class.getSimpleName();
    private static final Family ENEMY_WITH_POWER = Family.one(KirbyPower.class).get();
    public static float COYOTE_TIME = .25f;

    private boolean isGrounded;
    private boolean wasGrounded;
    private float lastOnGround;
    private float jumpTime;
    private boolean allowGrab = false;
    private boolean suckActive = false;

    public GoombaNormalState(Engine engine, Scene<? extends BaseScreen> scene, Entity entity) {
        super(engine, scene, entity);
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

            var animType = AnimType.COIN;
            var kirbyPower = entity.getComponent(KirbyPower.class);
            if (kirbyPower == null) {
                animType = actuallyMoving ? AnimType.BILLY_WALK : AnimType.BILLY_IDLE;
            } else {
                animType = kirbyPower.getBillyEnemyWalkAnimType();
            }

            Signals.animStart.dispatch(new AnimationEvent.Play(animator(), animType));
        } else {
            lastOnGround += delta;
        }

        handleMovement(delta);
        handleJumping(delta);
        handleSuckOff(delta);
        handleAction(delta);

        var fireballs = engine.getEntitiesFor(Family.all(Fireball.class).get());
        for (Entity fireball : fireballs) {
            fireball.getComponent(Fireball.class).ttl -= delta;
            if (fireball.getComponent(Fireball.class).ttl <= 0) {
                engine.removeEntity(fireball);
            }
        }
    }

    private void handleMovement(float delta) {
        var input     = input();
        var animator  = animator();
        var velocity  = velocity();
        var kirby     = kirby();

        // Update horizontal speed based on input
        var accel = isGrounded
            ? input.moveDirX * Constants.MOVE_ACCEL_GROUND
            : input.moveDirX * Constants.MOVE_ACCEL_AIR;
        velocity.value.x += accel * delta;

        // Constrain horizontal speed
        var maxSpeed = isGrounded ? velocity.maxHorizontalSpeedGround : velocity.maxHorizontalSpeedAir;
        if (Calc.abs(velocity.value.x) > maxSpeed) {
            // NOTE: this version is a hard cap on max speed
            velocity.value.x = animator.facing * maxSpeed;

            // NOTE: this version rapidly lerps to reach the maxSpeed over some small finite time
            //velocity.value.x = Calc.approach(velocity.value.x, animator.facing * maxSpeed, 2000f * delta);
        }

        // Apply friction to slow down, if applicable
        if (input.moveDirX == 0) {
            var friction = isGrounded ? friction().ground : friction().air;
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
        var kirby = kirby();

        jumpTime += delta;
        var ignoreGround = kirby != null && kirby.ignoreGround();

        var jumpRequested = input.wasJumpJustPressed;
        if (jumpRequested && cooldowns.isReady("jump")) {
            if ((ignoreGround || (isGrounded || lastOnGround < COYOTE_TIME) && player.jumpState() == Player.JumpState.GROUNDED)) {
                // start a new jump!
                jumpTime = 0;
                player.jumpState(Player.JumpState.JUMPED);

                var jumpAccel = kirby() == null ? Constants.JUMP_ACCEL_SINGLE : kirby().jumpImpulse();
                velocity.value.y = jumpAccel;

                Signals.cooldownReset.dispatch(new CooldownEvent.Reset(cooldowns, "jump"));
                Signals.animScale.dispatch(new AnimationEvent.Scale(animator, 0.66f, 1.33f));
                Signals.animStart.dispatch(new AnimationEvent.Start(animator, getJumpAnimation()));
                Signals.playSound.dispatch(new AudioEvent.PlaySound(SoundType.JUMP));

                if ((kirby != null ? kirby.powerType : null) == KirbyPower.PowerType.KOOPA) {
                    kirby.activeTimer = 0;
                }
            }
        }

        var jumpHeldAccel = kirby() == null ? Constants.JUMP_HELD_ACCEL : kirby().jumpHeld();
        if (player.jumpState() == Player.JumpState.JUMPED) {
            if (input.isJumpHeld || input.wasControllerJumpButtonDown) {
                float jumpAccelAmount = MathUtils.clamp(.5f - jumpTime, 0f, 1f);
                velocity.value.y += jumpAccelAmount * jumpHeldAccel;
            }
        }
    }

    private void handleSuckOff(float delta) {
        // if you don't have a power you can suck a guy off
        var kirby = Components.optional(entity, KirbyShaderRenderable.class).orElse(null);
        var power = Components.optional(entity, KirbyPower.class).orElse(null);
        if (kirby == null) return;

        if (suckActive) {
            kirby.targetStrength = 1f;
        } else {
            kirby.targetStrength = 0f;
        }

        if (power == null) {
            if (input().isDownJustPressed){
                suckActive = true;
            }
            // no power right now
            if (input().isDownHeld && suckActive) {
                Signals.playMusic.dispatch(new AudioEvent.PlayMusic(MusicType.SUCK, 0.25f));

                var enemiesWithPower = engine.getEntitiesFor(ENEMY_WITH_POWER);
                for (var enemyEntity : enemiesWithPower) {
                    if (enemyEntity == this.entity) continue; // Don't take from yourself?

                    var playerPos = entity.getComponent(Position.class);
                    var enemyPos = enemyEntity.getComponent(Position.class);
                    if (enemyPos.dst(playerPos) < KirbyShaderRenderable.radius) {
                        var enemyPower = Components.get(enemyEntity, KirbyPower.class);
                        if (enemyPower == null) continue;

                        // Suck this guy off....
                        Signals.playSound.dispatch(new AudioEvent.PlaySound(SoundType.SLURP, .75f));

                        // Create a particle emitter to show the enemy power being sucked in
                        var params = new SuckEffect.Params(enemyPower, entity, enemyPos);
                        var emitter = Factory.emitter(EmitterType.POWER_SUCK, params);
                        engine.addEntity(emitter);

                        // Give power to billy by cloning power component and attaching to billy
                        enemyEntity.remove(KirbyPower.class);
                        gainPower(enemyPower.powerType);

                        // Respawn enemy from their MySpawner so there's always an enemy of that type in the level
                        // TODO: technically we don't need this extra complexity of removing and respawning,
                        //  the suck effect can look like we're sucking out the 'spirit power'
                        //  and we can just leave the enemy in place
                        var enemySpawner = Components.get(enemyEntity, MySpawner.class);
                        if (enemySpawner == null) {
                            Util.warn(TAG, "unable to respawn sucked enemy, missing MySpawner component: "
                                    + Components.get(enemyEntity, Name.class));
                        } else {
                            // Spawn a new entity from their spawner after some interval
                            if (scene != null) {
                                Time.do_after_delay(3f, (args) -> scene.spawnEntity(enemySpawner.spawner));
                            }
                            // Delete the original enemy entity
                            engine.removeEntity(enemyEntity);
                        }
                        break;
                    }
                }
            } else {
                Signals.stopMusic.dispatch(new AudioEvent.StopMusic(MusicType.SUCK));
                suckActive = false;
            }
        } else {
            // You have a power
            suckActive = false;
            Signals.stopMusic.dispatch(new AudioEvent.StopMusic(MusicType.SUCK));

            if (input().isDownHeld && input().isActionJustPressed) {
                // Create a particle emitter to show the enemy power being spat out
                var params = new SpitEffect.Params(power, position());
                var emitter = Factory.emitter(EmitterType.POWER_SPIT, params);
                engine.addEntity(emitter);

                // Remove the power from billy and restore his original animation
                entity.remove(KirbyPower.class);
                Signals.animStart.dispatch(new AnimationEvent.Play(animator(), AnimType.BILLY_IDLE));
            }
        }
    }

    public void gainPower(KirbyPower.PowerType powerType) {
        var kirbyPower = new KirbyPower(powerType);
        this.entity.add(kirbyPower);
        var animator = Components.get(entity, Animator.class);
        if (animator == null) return;
        Signals.animStart.dispatch(new AnimationEvent.Play(animator(), kirbyPower.getBillyEnemyWalkAnimType()));
    }

    float actionDelay = 0;
    public void handleAction(float delta) {
        var gravity = gravity();
        var velocity = velocity();
        var friction = friction();
        var animator = animator();

        gravity.value = Constants.GRAVITY;
        velocity.maxHorizontalSpeedAir = Constants.MOVE_SPEED_MAX_AIR;
        velocity.maxHorizontalSpeedGround = Constants.MOVE_SPEED_MAX_GROUND;
        velocity.maxFallSpeed = Constants.MOVE_SPEED_MAX_AIR;
        friction.ground = Constants.FRICTION_MAX_GROUND;
        friction.air = Constants.FRICTION_MAX_AIR;
        animator.animation = getWalkAnimation().get();
        actionDelay -= delta;
        var kirby = kirby();


        if (kirby == null) return;
        kirby.activeTimer -= delta;
        gravity.value = kirby.getGravity();
        velocity.maxFallSpeed = kirby().maxGroundSpeed();
        velocity.maxHorizontalSpeedGround = kirby().maxGroundSpeed();
        velocity.maxHorizontalSpeedAir = kirby().maxAirSpeed();
        friction.ground = kirby.getFriction();
        if (kirby.isActionActive()) {
            animator.animation = getActionAnimation().get();
        }

        if (input().isActionJustPressed && actionDelay <= 0f) {
            actionDelay = .5f;
            switch (kirby.powerType) {
                case KOOPA:
                    kirby.activeTimer = 10f;
                    break;
                case LAKITU:
                    break;
                case HAMMER:
                    break;
                case BULLET:
                    velocity.value.x = animator.facing * 500f;
                    kirby.activeTimer = .5f;
                    break;
                case SUN:
                    spawnFireball();
                    kirby.activeTimer = .5f;
                    break;
            }
        }

    }

    public AnimType getWalkAnimation() {
        var kirbyPower = Components.get(entity, KirbyPower.class);
        if (kirbyPower == null) {
            return AnimType.BILLY_WALK;
        } else {
            return kirbyPower.getBillyEnemyWalkAnimType();
        }
    }

    public AnimType getJumpAnimation() {
        var kirbyPower = Components.get(entity, KirbyPower.class);
        if (kirbyPower == null) {
            return AnimType.BILLY_JUMP;
        } else {
            return kirbyPower.getBillyEnemyWalkAnimType();
        }
    }

    public AnimType getActionAnimation() {
        var kirbyPower = Components.get(entity, KirbyPower.class);
        if (kirbyPower == null) {
            return AnimType.BILLY_WALK;
        } else {
            return kirbyPower.getBillyEnemyActionAnimType();
        }
    }

    public void spawnFireball() {

        var animator = animator();
        var vel = new Velocity(0,0);
        vel.value.y = 0;
        vel.value.x = 100 * animator.facing;
        vel.maxFallSpeed = 100f;

        var fireballEntity = engine.createEntity();
        fireballEntity.add(new Position(position().x, position().y + 20));
        fireballEntity.add(vel);
        fireballEntity.add(new Gravity(Constants.GRAVITY));
        fireballEntity.add(new Fireball());
        var fireballAnim = new Animator(AnimType.MARIO_FIREBALL);
        fireballAnim.size.set(16,16f);
        fireballAnim.depth = 15f;
        fireballEntity.add(fireballAnim);

        var collidesWith  = new CollisionMask[] { CollisionMask.SOLID , CollisionMask.DESTRUCTIBLE, CollisionMask.ENEMY};
        fireballEntity.add(Collider.circ(CollisionMask.PROJECTILE, 8, 8, 8f, collidesWith));

        engine.addEntity(fireballEntity);
    }
}
