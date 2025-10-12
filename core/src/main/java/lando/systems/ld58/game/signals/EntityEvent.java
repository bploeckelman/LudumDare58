package lando.systems.ld58.game.signals;

import com.badlogic.ashley.core.Entity;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

public interface EntityEvent extends SignalEvent {

    String TAG = CollisionEvent.class.getSimpleName();

    Entity entity();

    static void remove(Entity entity) { signal.dispatch(new Remove(entity)); }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    class Remove implements EntityEvent {
        public final Entity entity;
        public Entity entity() { return entity; }
    }
}
