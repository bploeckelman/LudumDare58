package lando.systems.ld58.game.signals;

import lando.systems.ld58.game.components.Pickup;
import lando.systems.ld58.game.components.TilemapObject;
import lombok.AllArgsConstructor;

public interface TriggerEvent extends SignalEvent {

    static void dialog(TilemapObject.Trigger trigger) {
        signal.dispatch(new Dialog(trigger.type));
        trigger.activated = true;
    }

    static void collect(Pickup.Type type) {
        signal.dispatch(new Collect(type));
    }

    @AllArgsConstructor
    class Dialog implements TriggerEvent {
        public final String key;
    }

    @AllArgsConstructor
    class Collect implements TriggerEvent {
        public final Pickup.Type pickupType;
    }
}
