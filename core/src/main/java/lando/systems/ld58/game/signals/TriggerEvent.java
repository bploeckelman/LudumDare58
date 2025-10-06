package lando.systems.ld58.game.signals;

import lombok.AllArgsConstructor;

public interface TriggerEvent {

    @AllArgsConstructor
    class Dialog implements TriggerEvent {
        public final String key;
    }
}
