package lando.systems.ld58.game.components.enemies;

import com.badlogic.ashley.core.Component;

public class EnemyGoombaCyborg extends Enemy implements Component {
    public EnemyGoombaCyborg() {
        behavior = Behavior.CUSTOM;
        walkAccel = 100f;
    }
}
