package lando.systems.ld58.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

import java.util.Optional;

public class Friction implements Component {

    public static final ComponentMapper<Friction> mapper = ComponentMapper.getFor(Friction.class);

    public static Optional<Friction> get(Entity entity) {
        return Optional.ofNullable(mapper.get(entity));
    }

    public float value;

    public Friction() {
        this(0);
    }

    public Friction(float value) {
        this.value = value;
    }

    public float value() { return value; }
}
