package lando.systems.ld58.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld58.assets.AnimType;
import lando.systems.ld58.game.Components;
import lando.systems.ld58.game.Systems;
import lando.systems.ld58.game.components.Animator;
import lando.systems.ld58.game.components.Enemy;
import lando.systems.ld58.game.components.Velocity;

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
                    // Move forward until hitting a wall or edge, then turn around
                    var hit = Systems.collisionCheck.check(entity, vel.xSign(), 0);
                    if (hit) {
                        enemy.direction = -enemy.direction;
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
