package lando.systems.ld58.game.components.enemies;

import com.badlogic.ashley.core.Component;

public class EnemyAngrySun extends Enemy implements Component {

    public static final float CIRCLE_RADIUS = 32f;
    public static final float CIRCLE_SPEED = 3f; // radians/sec^2
    public static final float CHASE_ACCEL = 100f; // pixels/sec^2

    public float angle = 0f;

    public EnemyAngrySun() {
        behavior = Behavior.CUSTOM;
    }
}
