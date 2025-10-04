package lando.systems.ld58.game.components.enemies;

import com.badlogic.ashley.core.Component;

public class EnemyAngrySun implements Enemy, Component {

    public static final float CIRCLE_RADIUS = 32f;
    public static final float CIRCLE_SPEED = 4f;
    public static final float CHASE_SPEED = 50f;

    public float angle = 0f;
}
