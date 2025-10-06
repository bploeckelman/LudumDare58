package lando.systems.ld58.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import lando.systems.ld58.game.Constants;

import java.util.Optional;

public class Friction implements Component {

    public static final ComponentMapper<Friction> mapper = ComponentMapper.getFor(Friction.class);

    public static Optional<Friction> get(Entity entity) {
        return Optional.ofNullable(mapper.get(entity));
    }

    public float ground;
    public float air;

    public Friction() {
        this(Constants.FRICTION_MAX_GROUND, Constants.FRICTION_MAX_AIR);
    }

    public Friction(float ground, float air) {
        this.ground = ground;
        this.air = air;
    }
}
