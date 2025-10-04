package lando.systems.ld58.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld58.assets.AnimType;
import lando.systems.ld58.game.Components;
import lando.systems.ld58.game.Systems;
import lando.systems.ld58.game.components.Animator;
import lando.systems.ld58.game.components.Collider;
import lando.systems.ld58.game.components.Enemy;
import lando.systems.ld58.game.components.Velocity;
import lando.systems.ld58.game.components.collision.CollisionRect;
import lando.systems.ld58.utils.FramePool;

public class EnemySystem extends IteratingSystem {

    public EnemySystem() {
        super(Family.one(Enemy.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        var enemy = Components.get(entity, Enemy.class);

        // Progress simple state machine
        enemy.stateTime += delta;

        // Act on state machine
        if (enemy.type == Enemy.Type.MARIO) {
            updateMario(entity, enemy, delta);
        }
    }

    private void updateMario(Entity entity, Enemy enemy, float delta) {
        var anim = Components.get(entity, Animator.class);
        var vel = Components.get(entity, Velocity.class);
        var col = Components.get(entity, Collider.class);
        var speed = 300f;

        switch (enemy.state) {
            case IDLE: {
                if (enemy.stateTime < 2f) {
                    vel.stop();
                    anim.play(AnimType.MARIO_IDLE);
                } else {
                    enemy.stateTime = 0f;
                    enemy.state = Enemy.State.PATROL;
                    enemy.direction = MathUtils.randomSign();
                }
            } break;
            case PATROL: {
                if (enemy.stateTime < 4f) {
                    // Optionally get offset for collider, to look ahead at the collider edges for a platform edge
                    var colOffset = FramePool.pi2();
                    if (col != null && col.shape instanceof CollisionRect) {
                        var r = col.shape(CollisionRect.class).rectangle;
                        var x = (enemy.direction == +1) ? r.x + r.width
                              : (enemy.direction == -1) ? r.x : 0f;
                        colOffset.set(x, 0);
                    }

                    // Move forward until hitting a wall or edge, then turn around
                    var nearEdge = !Systems.collisionCheck.check(entity, enemy.direction + colOffset.x, -1);
                    var hitWall = Systems.collisionCheck.check(entity, enemy.direction, 0);
                    if (hitWall || nearEdge) {
                        enemy.direction = -enemy.direction;
                        vel.stopX();
                    }

                    anim.facing = enemy.direction;
                    anim.play(AnimType.MARIO_WALK);

                    var accel = speed * enemy.direction;
                    vel.value.x += accel * delta;
                } else {
                    enemy.stateTime = 0f;
                    enemy.state = Enemy.State.IDLE;
                    enemy.direction = 0;
                }
            } break;
        }
    }
}
