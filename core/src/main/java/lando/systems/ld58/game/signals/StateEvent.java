package lando.systems.ld58.game.signals;

import com.badlogic.ashley.core.Entity;
import lando.systems.ld58.game.state.PlayerState;
import lombok.RequiredArgsConstructor;

public interface StateEvent {

    Entity entity();

    @RequiredArgsConstructor
    class Change implements StateEvent {
        public final Entity entity;
        public final Class<? extends PlayerState> fromState;
        public final Class<? extends PlayerState> toState;

        @Override
        public Entity entity() {
            return entity;
        }

        public Class<? extends PlayerState> fromState() { return fromState; }
        public Class<? extends PlayerState> toState() { return toState; }
    }
}
