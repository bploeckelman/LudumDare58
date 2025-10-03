package lando.systems.ld58.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

import java.util.Optional;

public class Gravity implements Component {

    public static final ComponentMapper<Gravity> mapper = ComponentMapper.getFor(Gravity.class);

    public static Optional<Gravity> get(Entity entity) {
        return Optional.ofNullable(mapper.get(entity));
    }

    public float value;

    public Gravity() {
        this(0);
    }

    public Gravity(float value) {
        this.value = value;
    }

    public float value() { return value; }
}
