package lando.systems.ld58.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import lando.systems.ld58.game.Factory;
import lando.systems.ld58.game.components.collision.CollisionMask;
import lando.systems.ld58.game.signals.EntityEvent;
import lando.systems.ld58.utils.Util;

import java.util.HashMap;
import java.util.Map;

public class Sensors implements Component {

    private static final String TAG = Sensors.class.getSimpleName();

    public final Map<String, Entity> entities;

    public Sensors() {
        this.entities = new HashMap<>();
    }

    // TODO: flesh this out with a builder or whatever params end up making sense
    public Entity create(String key) {
        if (entities.containsKey(key)) {
            Util.warn(TAG, "creating sensor entity with existing key '"+key+"', this overrides the existing entity!");
        }

        var entity = Factory.createEntity();

        var pos = new Position();

        var radius = 2f;
        var collidesWith = new CollisionMask[] { CollisionMask.SOLID, CollisionMask.PLAYER };
        var col = Collider.circ(CollisionMask.SENSOR, pos.x, pos.y, radius, collidesWith);

        entity.add(pos);
        entity.add(col);

        entities.put(key, entity);
        return entity;
    }

    public void remove(String key) {
        var entity = entities.remove(key);
        if (entity != null) {
            EntityEvent.remove(entity);
        }
    }

    public void clear() {
        for (var entity : entities.values()) {
            EntityEvent.remove(entity);
        }
    }
}
