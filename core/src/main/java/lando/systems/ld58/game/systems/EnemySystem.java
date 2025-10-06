package lando.systems.ld58.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld58.game.Components;
import lando.systems.ld58.game.Systems;
import lando.systems.ld58.game.components.Collider;
import lando.systems.ld58.game.components.Position;
import lando.systems.ld58.game.components.SceneContainer;
import lando.systems.ld58.game.components.Velocity;
import lando.systems.ld58.game.components.collision.CollisionRect;
import lando.systems.ld58.game.components.enemies.*;
import lando.systems.ld58.game.components.renderable.Animator;
import lando.systems.ld58.utils.FramePool;
import lando.systems.ld58.utils.Util;

public class EnemySystem extends IteratingSystem {

    private static final String TAG = EnemySystem.class.getSimpleName();
    private static final Family SCENE = Family.one(SceneContainer.class).get();

    public EnemySystem() {
        super(Family.one(
              EnemyAngrySun.class
            , EnemyBulletBill.class
            , EnemyCaptainLou.class
            , EnemyGoombaCyborg.class
            , EnemyHammerBro.class
            , EnemyKoopa.class
            , EnemyLakitu.class
            , EnemyMario.class
            , EnemyMisty.class
        ).get());
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        var enemy = Enemy.getEnemyComponent(entity);
        if (enemy == null) return;

        if      (Enemy.Behavior.PATROL == enemy.behavior) updatePatrol(entity, enemy, delta);
        else if (Enemy.Behavior.CUSTOM == enemy.behavior) updateCustom(entity, enemy, delta);
    }

    private void updatePatrol(Entity entity, Enemy enemy, float delta) {
        var anim = Components.get(entity, Animator.class);
        var vel = Components.get(entity, Velocity.class);
        var col = Components.get(entity, Collider.class);

        switch (enemy.patrolState) {
            case IDLE: {
                if (enemy.stateTime < 2f) {
                    // NOTE: don't stop, just allow friction to apply
                    var animType = Enemy.ENEMY_ANIM_TYPE_IDLE.get(enemy.getClass());
                    if (animType != null) anim.play(animType);
                } else {
                    enemy.stateTime = 0f;
                    enemy.patrolState = Enemy.PatrolState.MOVE;
                    enemy.direction = MathUtils.randomSign();
                }
            } break;

            case MOVE: {
                if (enemy.stateTime < 4f) {
                    // Get collider offset to look ahead at edges
                    var colOffset = FramePool.pi2();
                    if (col != null && col.shape instanceof CollisionRect) {
                        var r = col.shape(CollisionRect.class).rectangle;
                        var x = (enemy.direction == +1) ? r.x + r.width
                              : (enemy.direction == -1) ? r.x : 0f;
                        colOffset.set(x, 0);
                    }

                    // Check for edge or wall
                    var nearEdge = !Systems.collisionCheck.check(entity, enemy.direction + colOffset.x, -1);
                    var hitWall  = Systems.collisionCheck.check(entity, enemy.direction, 0);
                    if (hitWall || nearEdge) {
                        enemy.direction = -enemy.direction;
                        vel.stopX();
                    }

                    anim.facing = enemy.direction;
                    var animType = Enemy.ENEMY_ANIM_TYPE_WALK.get(enemy.getClass());
                    if (animType != null) anim.play(animType);

                    var accel = enemy.walkAccel * enemy.direction;
                    vel.value.x += accel * delta;
                } else {
                    enemy.stateTime = 0f;
                    enemy.patrolState = Enemy.PatrolState.IDLE;
                    enemy.direction = 0;
                }
            } break;
        }

        enemy.stateTime += delta;
    }

    private void updateCustom(Entity entity, Enemy enemy, float delta) {
        if (enemy instanceof EnemyAngrySun) {
            var angrySun = (EnemyAngrySun) enemy;
            updateAngrySun(entity, angrySun, delta);
        } else {
            Util.warn(TAG, "unhandled custom behavior: " + enemy.getClass().getSimpleName());
        }
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
