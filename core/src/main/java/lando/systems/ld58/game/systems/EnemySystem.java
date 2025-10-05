package lando.systems.ld58.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld58.assets.AnimType;
import lando.systems.ld58.game.Components;
import lando.systems.ld58.game.Systems;
import lando.systems.ld58.game.components.*;
import lando.systems.ld58.game.components.collision.CollisionRect;
import lando.systems.ld58.game.components.enemies.EnemyAngrySun;
import lando.systems.ld58.game.components.enemies.EnemyMario;
import lando.systems.ld58.utils.FramePool;

public class EnemySystem extends IteratingSystem {

    private static final Family SCENE = Family.one(SceneContainer.class).get();

    public EnemySystem() {
        super(Family.one(EnemyMario.class, EnemyAngrySun.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        if (Components.has(entity, EnemyMario.class)) {
            var mario = Components.get(entity, EnemyMario.class);
            updateMario(entity, mario, delta);
        }
        else if (Components.has(entity, EnemyAngrySun.class)) {
            var sun = Components.get(entity, EnemyAngrySun.class);
            updateAngrySun(entity, sun, delta);
        }
    }

    private void updateMario(Entity entity, EnemyMario mario, float delta) {
        var anim = Components.get(entity, Animator.class);
        var vel = Components.get(entity, Velocity.class);
        var col = Components.get(entity, Collider.class);

        switch (mario.state) {
            case IDLE: {
                if (mario.stateTime < 2f) {
                    // NOTE: don't stop, just allow friction to apply
                    anim.play(AnimType.MARIO_IDLE);
                } else {
                    mario.stateTime = 0f;
                    mario.state = EnemyMario.State.PATROL;
                    mario.direction = MathUtils.randomSign();
                }
            } break;

            case PATROL: {
                if (mario.stateTime < 4f) {
                    // Get collider offset to look ahead at edges
                    var colOffset = FramePool.pi2();
                    if (col != null && col.shape instanceof CollisionRect) {
                        var r = col.shape(CollisionRect.class).rectangle;
                        var x = (mario.direction == +1) ? r.x + r.width
                              : (mario.direction == -1) ? r.x : 0f;
                        colOffset.set(x, 0);
                    }

                    // Check for edge or wall
                    var nearEdge = !Systems.collisionCheck.check(entity, mario.direction + colOffset.x, -1);
                    var hitWall = Systems.collisionCheck.check(entity, mario.direction, 0);
                    if (hitWall || nearEdge) {
                        mario.direction = -mario.direction;
                        vel.stopX();
                    }

                    anim.facing = mario.direction;
                    anim.play(AnimType.MARIO_WALK);

                    var accel = EnemyMario.WALK_ACCEL * mario.direction;
                    vel.value.x += accel * delta;
                } else {
                    mario.stateTime = 0f;
                    mario.state = EnemyMario.State.IDLE;
                    mario.direction = 0;
                }
            } break;
        }

        mario.stateTime += delta;
    }

    private void updateAngrySun(Entity entity, EnemyAngrySun sun, float delta) {
        // TODO: flesh this out with swoop behavior instead of just spiraling towards the player
        var pos = Components.get(entity, Position.class);
        var vel = Components.get(entity, Velocity.class);

        var player = getPlayerEntity();
        if (player == null) {
            // No player, just apply friction to slow down
            vel.value.scl(0.95f);
            return;
        }

        // Calculate chase acceleration - move toward player
        var playerPos = Components.get(player, Position.class);
        var chaseAccel = FramePool.vec2(playerPos.x, playerPos.y)
            .sub(pos.x, pos.y)
            .nor()
            .scl(EnemyAngrySun.CHASE_ACCEL);

        // Calculate circular acceleration - perpendicular motion
        sun.angle += EnemyAngrySun.CIRCLE_SPEED * delta;
        var circleAccel = FramePool.vec2(-MathUtils.sin(sun.angle), MathUtils.cos(sun.angle))
            .scl(EnemyAngrySun.CIRCLE_RADIUS * EnemyAngrySun.CIRCLE_SPEED);

        // Apply accelerations to velocity (additive, doesn't clobber collision impulses)
        vel.value.add(chaseAccel.scl(delta)).add(circleAccel.scl(delta));

        // Apply friction/drag to naturally decay all velocity (AI + collision impulses)
        vel.value.scl(0.95f);
    }

    private Entity getPlayerEntity() {
        var sceneEntities = getEngine().getEntitiesFor(SCENE);
        if (sceneEntities.size() == 1) {
            var scene = Components.get(sceneEntities.get(0), SceneContainer.class).scene;
            return scene.player;
        }
        return null;
    }
}
