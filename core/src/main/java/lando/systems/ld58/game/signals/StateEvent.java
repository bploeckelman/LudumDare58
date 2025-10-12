package lando.systems.ld58.game.signals;

import com.badlogic.ashley.core.Entity;
import lando.systems.ld58.game.state.PlayerState;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

public interface StateEvent extends SignalEvent {

    Entity entity();

    static void change(Entity entity, Class<? extends PlayerState> fromState, Class<? extends PlayerState> toState) {
        signal.dispatch(new Change(entity, fromState, toState));
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
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
