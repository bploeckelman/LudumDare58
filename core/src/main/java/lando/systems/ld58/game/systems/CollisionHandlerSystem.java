package lando.systems.ld58.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.Gdx;
import com.github.tommyettinger.digital.Stringf;
import lando.systems.ld58.Flag;
import lando.systems.ld58.assets.EmitterType;
import lando.systems.ld58.assets.SoundType;
import lando.systems.ld58.game.Components;
import lando.systems.ld58.game.components.BlockBreakable;
import lando.systems.ld58.game.Factory;
import lando.systems.ld58.game.Signals;
import lando.systems.ld58.game.components.*;
import lando.systems.ld58.game.components.collision.CollisionResponse;
import lando.systems.ld58.game.components.renderable.RelicPickupRender;
import lando.systems.ld58.game.signals.AudioEvent;
import lando.systems.ld58.game.signals.CollisionEvent;
import lando.systems.ld58.particles.effects.BlockBreakEffect;
import lando.systems.ld58.utils.FramePool;
import lando.systems.ld58.utils.Time;
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
        // Player/Enemy collision
        if ((Components.has(move.mover(),  Player.class) && Components.hasEnemyComponent(move.target()))
         || (Components.has(move.target(), Player.class) && Components.hasEnemyComponent(move.mover()))) {
            handlePlayerEnemyCollision(move);
        }

        var fireball
            = Components.has(move.entityA(), Fireball.class) ? move.entityA()
            : Components.has(move.entityB(), Fireball.class) ? move.entityB()
            : null;

        var tilemap
            = Components.has(move.entityA(), Tilemap.class) ? move.entityA()
            : Components.has(move.entityB(), Tilemap.class) ? move.entityB()
            : null;

        var player
            = Components.has(move.entityA(), Player.class) ? move.entityA()
            : Components.has(move.entityB(), Player.class) ? move.entityB()
            : null;
        var enemy
            = Components.hasEnemyComponent(move.entityA()) ? move.entityA()
            : Components.hasEnemyComponent(move.entityB()) ? move.entityB()
            : null;
        var blockBreak
            = Components.has(move.entityA(), BlockBreakable.class) ? move.entityA()
            : Components.has(move.entityB(), BlockBreakable.class) ? move.entityB()
            : null;
        var blockSpike
            = Components.has(move.entityA(), BlockSpike.class) ? move.entityA()
            : Components.has(move.entityB(), BlockSpike.class) ? move.entityB()
            : null;
        var blockLava
            = Components.has(move.entityA(), BlockLava.class) ? move.entityA()
            : Components.has(move.entityB(), BlockLava.class) ? move.entityB()
            : null;

        // TODO: rework player/enemy collision instead of using block at top of method
//        if (player != null && enemy != null) {
//        // Player/Enemy collision
//            handlePlayerEnemyCollision(move, player, enemy);
//        } else
        if (player != null && blockBreak != null) {
            handlePlayerBlockBreakCollision(move, player, blockBreak);
        }
        else if (player != null && blockSpike != null) {
            handlePlayerBlockSpikeCollision(move, player, blockSpike);
        }
        else if (player != null && blockLava != null) {
            handlePlayerBlockLavaCollision(move, player, blockLava);
        }
        // TODO: add 'handleProjectileDestructibleCollision(projectile, destructible);


        if (fireball != null && tilemap != null) {
            handleFireballMapCollisions(move);
        }

        if (fireball != null && enemy != null) {
            handleEnemyFireballCollision(move, fireball, enemy);
        }
    }

    private void handleOverlapCollision(CollisionEvent.Overlap overlap) {
        var player = Components.has(overlap.entityA(), Player.class) ? overlap.entityA()
                   : Components.has(overlap.entityB(), Player.class) ? overlap.entityB()
                   : null;
        var pickup = Components.has(overlap.entityA(), Pickup.class) ? overlap.entityA()
                   : Components.has(overlap.entityB(), Pickup.class) ? overlap.entityB()
                   : null;

        if (player != null && pickup != null) {
            handlePlayerPickupCollision(player, pickup);
        } else {
            Gdx.app.debug(TAG, "Overlap collision that wasn't handled");
        }
    }

    private void handleFireballMapCollisions(CollisionEvent.Move move) {
        var fireball = move.mover();
        var fireballVel = fireball.getComponent(Velocity.class);

        if (move.dir().x != 0) {
            fireballVel.value.x *= -1;
        }
        if (move.dir().y != 0) {
            fireballVel.value.y *= -1;
        }
        move.response = CollisionResponse.KEEP_VELOCITY;
    }

    private void handleEnemyFireballCollision(CollisionEvent.Move move, Entity fireball, Entity enemy) {
        if (!enemy.getComponent(EnemyTag.class).alive) {
            return;
        }

        enemy.getComponent(EnemyTag.class).alive = false;
        getEngine().removeEntity(enemy);
        getEngine().removeEntity(fireball);
        Util.log("called");
        Time.do_after_delay(3f, (args) -> {
            Util.findScene(getEngine()).spawnEntity(enemy.getComponent(MySpawner.class).spawner);
            Util.log("spawned");
        });

        move.response = CollisionResponse.PASSTHROUGH;
    }

    private void handlePlayerPickupCollision(Entity playerEntity, Entity pickupEntity) {
        var player = Components.get(playerEntity, Player.class);
        var pickup = Components.get(pickupEntity, Pickup.class);

        // TODO: handle the pickup differently depending what it is...
        //  - mark a relic as obtained and trigger level completion
        //  - increment a coin counter
        switch (pickup.type) {
            case COIN: {
                Signals.playSound.dispatch(new AudioEvent.PlaySound(SoundType.COIN));
            } break;
            case RELIC_PLUNGER:
            case RELIC_TORCH:
            case RELIC_WRENCH: {
                Signals.playSound.dispatch(new AudioEvent.PlaySound(SoundType.BREAK));
                playerEntity.add(new RelicPickupRender(pickup.type));
            } break;
        }

        getEngine().removeEntity(pickupEntity);
    }

    private void handlePlayerEnemyCollision(CollisionEvent.Move move) {
        // Handle both directions: player->enemy and enemy->player
        var entityA = move.mover();
        var entityB = move.target();

        // Ensure one is player, one is enemy
        boolean aIsPlayer = Components.has(entityA, Player.class);
        boolean bIsPlayer = Components.has(entityB, Player.class);
        boolean aIsEnemy = Components.hasEnemyComponent(entityA);
        boolean bIsEnemy = Components.hasEnemyComponent(entityB);

        if (!((aIsPlayer && bIsEnemy) || (aIsEnemy && bIsPlayer))) {
            return; // Not a player-enemy collision
        }

        // Get components for both entities
        var posA = Components.get(entityA, Position.class);
        var velA = Components.get(entityA, Velocity.class);
        var posB = Components.get(entityB, Position.class);
        var velB = Components.get(entityB, Velocity.class);

        // Tuning constants
        final float SIDE_BOUNCE_STRENGTH = 250f;  // Side collision bounce (lower than vertical)
        final float VERTICAL_BOUNCE = 400f;       // Upward bounce when landing on enemy
        final float HEADBUTT_BOUNCE = 200f;       // Bounce when hitting from below
        final float MOMENTUM_TRANSFER = 0.3f;     // How much velocity difference matters

        // Calculate collision normal (direction from A to B)
        var normal = FramePool.vec2(posB.x - posA.x, posB.y - posA.y).nor();

        // Determine collision type based on normal direction
        boolean fromAbove = normal.y < -0.5f;  // A is above B (normal points down)
        boolean fromBelow = normal.y > 0.5f;   // A is below B (normal points up)
        boolean fromSide = Math.abs(normal.y) <= 0.5f; // Mostly horizontal

        if (fromAbove && aIsPlayer) {
            // Player stomps on enemy - player bounces up
            velA.value.y = VERTICAL_BOUNCE;

            // Enemy gets pushed down/away slightly
            velB.value.y = -VERTICAL_BOUNCE * 0.3f;
            velB.value.x += normal.x * SIDE_BOUNCE_STRENGTH * 0.3f;

            // Set player jump state for air control
            var player = Components.get(entityA, Player.class);
            player.jumpState(Player.JumpState.JUMPED);

            move.response = CollisionResponse.KEEP_VELOCITY;
        }
        else if (fromBelow && aIsPlayer) {
            // Player headbutts enemy from below
            velA.value.y *= 0.5f; // Reduce player's upward velocity
            velB.value.y = HEADBUTT_BOUNCE; // Enemy pops up

            move.response = CollisionResponse.KEEP_VELOCITY;
        }
        else if (fromSide) {
            // Side collision - gentler horizontal bounce

            // Calculate relative velocity
            var relativeVelX = velB.value.x - velA.value.x;

            // Only bounce if moving toward each other
            var separationSpeed = normal.x * (velA.value.x - velB.value.x);
            if (separationSpeed < 0) {
                // Apply horizontal bounce with momentum transfer
                var bounceX = normal.x * SIDE_BOUNCE_STRENGTH;
                var momentumX = relativeVelX * MOMENTUM_TRANSFER;

                velA.value.x = -bounceX + momentumX;
                velB.value.x = bounceX - momentumX;
            }

            move.response = CollisionResponse.KEEP_VELOCITY;
        }
        else {
            // Diagonal collision - use the original general case but with moderate force
            var relativeVel = FramePool.vec2(
                velB.value.x - velA.value.x,
                velB.value.y - velA.value.y
            );

            var bounceStrength = 300f; // Moderate for diagonal
            var bounceX = normal.x * bounceStrength;
            var bounceY = normal.y * bounceStrength;

            var momentumX = relativeVel.x * MOMENTUM_TRANSFER;
            var momentumY = relativeVel.y * MOMENTUM_TRANSFER;

            velA.value.x += -bounceX + momentumX;
            velA.value.y += -bounceY + momentumY;

            velB.value.x += bounceX - momentumX;
            velB.value.y += bounceY - momentumY;

            move.response = CollisionResponse.KEEP_VELOCITY;
        }
    }

    private void handlePlayerBlockBreakCollision(CollisionEvent.Move move, Entity playerEntity, Entity destructibleEntity) {
        // Need a power to destroy it
        var playerPower = Components.get(playerEntity, KirbyPower.class);
        if (playerPower == null) {
            move.response = CollisionResponse.STOP_BOTH;
            return;
        }

        // Need bullet bill power to be active to destroy it
        var isBulletBill = (playerPower.powerType == KirbyPower.PowerType.BULLET);
        var canSmash = isBulletBill && playerPower.isActionActive();
        if (!canSmash) {
            move.response = CollisionResponse.STOP_BOTH;
            return;
        }

        // TODO: replace with 'block breaking' sound
        Signals.playSound.dispatch(new AudioEvent.PlaySound(SoundType.BREAK));

        // Particle effect
        var destructPos = Components.get(destructibleEntity, Position.class);
        var params = new BlockBreakEffect.Params(destructPos);
        var emitter = Factory.emitter(EmitterType.BLOCK_BREAK, params);
        getEngine().addEntity(emitter);

        // Remove the block
        getEngine().removeEntity(destructibleEntity);

        // Stop remaining movement this frame, but keep velocity just scale it down
        var playerVel = Components.get(playerEntity, Velocity.class);
        playerVel.set(playerVel.x() * 0.75f, playerVel.y());
        move.response = CollisionResponse.KEEP_VELOCITY;
    }

    private void handlePlayerBlockSpikeCollision(CollisionEvent.Move move, Entity playerEntity, Entity spikeEntity) {
        var playerPos = Components.get(playerEntity, Position.class);
        var playerSpawn = Components.get(playerEntity, MySpawner.class).spawner;

        // Need a power to safely traverse it, otherwise move back to spawn
        var playerPower = Components.get(playerEntity, KirbyPower.class);
        if (playerPower == null) {
            // TODO: play 'ouch' sound and particle effect
            playerPos.set(playerSpawn.x, playerSpawn.y);
            if (Flag.LOG_DEBUG.isEnabled()) {
                Util.log(TAG, Stringf.format("player touched danger block, moving back to spawn: id=%s, name=%s",
                    Components.optional(playerEntity, Id.class).orElse(Id.UNKNOWN),
                    Components.optional(playerEntity, Name.class).orElse(Name.UNKNOWN)));
            }
            move.response = CollisionResponse.STOP_BOTH;
            return;
        }

        // Need koopa power to be active to traverse it
        var isKoopaPower = (playerPower.powerType == KirbyPower.PowerType.KOOPA);
        var canTraverse = isKoopaPower && playerPower.isActionActive();
        if (!canTraverse) {
            // TODO: play 'ouch' sound and particle effect
            playerPos.set(playerSpawn.x, playerSpawn.y);
            if (Flag.LOG_DEBUG.isEnabled()) {
                Util.log(TAG, Stringf.format("player touched danger block, moving back to spawn: id=%s, name=%s",
                    Components.optional(playerEntity, Id.class).orElse(Id.UNKNOWN),
                    Components.optional(playerEntity, Name.class).orElse(Name.UNKNOWN)));
            }
            move.response = CollisionResponse.STOP_BOTH;
            return;
        }

        // TODO: hot foot effect
//        // Particle effect
//        var destructPos = Components.get(destructibleEntity, Position.class);
//        var params = new BlockBreakEffect.Params(destructPos);
//        var emitter = Factory.emitter(EmitterType.BLOCK_BREAK, params);
//        getEngine().addEntity(emitter);

//        var blockPos = Components.get(spikeEntity, Position.class);
//        var blockCol = Components.get(spikeEntity, Collider.class);
//        var blockColRect =
//
//        // Continue moving if we're moving in a direction that allows it
//        var playerVel = Components.get(playerEntity, Velocity.class);
//        if (move.dir().x != 0) {
//
//        }
//        else if (move.dir().y != 0) {
//
//        }
//        move.response = CollisionResponse.KEEP_VELOCITY;
    }

    private void handlePlayerBlockLavaCollision(CollisionEvent.Move move, Entity playerEntity, Entity lavaEntity) {
        var playerPos = Components.get(playerEntity, Position.class);
        var playerSpawn = Components.get(playerEntity, MySpawner.class).spawner;

        // Need a power to safely traverse it, otherwise move back to spawn
        var playerPower = Components.get(playerEntity, KirbyPower.class);
        if (playerPower == null) {
            // TODO: play 'ouch' sound and particle effect
            playerPos.set(playerSpawn.x, playerSpawn.y);
            if (Flag.LOG_DEBUG.isEnabled()) {
                Util.log(TAG, Stringf.format("player touched danger block, moving back to spawn: id=%s, name=%s",
                    Components.optional(playerEntity, Id.class).orElse(Id.UNKNOWN),
                    Components.optional(playerEntity, Name.class).orElse(Name.UNKNOWN)));
            }
            move.response = CollisionResponse.STOP_BOTH;
            return;
        }

        // Need koopa power to be active to traverse it
        var isKoopaPower = (playerPower.powerType == KirbyPower.PowerType.KOOPA);
        var canTraverse = isKoopaPower && playerPower.isActionActive();
        if (!canTraverse) {
            // TODO: play 'ouch' sound and particle effect
            playerPos.set(playerSpawn.x, playerSpawn.y);
            if (Flag.LOG_DEBUG.isEnabled()) {
                Util.log(TAG, Stringf.format("player touched danger block, moving back to spawn: id=%s, name=%s",
                    Components.optional(playerEntity, Id.class).orElse(Id.UNKNOWN),
                    Components.optional(playerEntity, Name.class).orElse(Name.UNKNOWN)));
            }
            move.response = CollisionResponse.STOP_BOTH;
            return;
        }
    }
}
