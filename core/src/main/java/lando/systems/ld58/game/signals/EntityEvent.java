package lando.systems.ld58.game.signals;

import com.badlogic.ashley.core.Entity;
import lombok.RequiredArgsConstructor;

public interface EntityEvent {

    String TAG = CollisionEvent.class.getSimpleName();

    Entity entity();

    @RequiredArgsConstructor
    class Remove implements EntityEvent {
        public final Entity entity;
        public Entity entity() { return entity; }
    }
}
