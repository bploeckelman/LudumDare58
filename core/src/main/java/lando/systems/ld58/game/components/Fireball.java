package lando.systems.ld58.game.components;

import com.badlogic.ashley.core.Component;

public class Fireball implements Component {
    public float ttl;

    public Fireball() {
        ttl = 2f;
    }
}
