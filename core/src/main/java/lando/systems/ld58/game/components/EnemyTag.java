package lando.systems.ld58.game.components;

import com.badlogic.ashley.core.Component;

public class EnemyTag implements Component {
    public boolean alive;

    public EnemyTag() {
        alive = true;
    }
}
