package lando.systems.ld58.game.signals;

import lando.systems.ld58.game.components.Pickup;
import lombok.AllArgsConstructor;

public interface TriggerEvent {

    @AllArgsConstructor
    class Dialog implements TriggerEvent {
        public final String key;
    }
    @AllArgsConstructor
    class Collect implements TriggerEvent {
        public final Pickup.Type pickupType;
    }
}
