package lando.systems.ld58.game.signals;

import lando.systems.ld58.game.components.Pickup;
import lando.systems.ld58.game.components.TilemapObject;

public interface TriggerEvent extends SignalEvent {

    static void dialog(TilemapObject.Trigger trigger) { signal.dispatch(new Dialog(trigger)); }
    static void collect(Pickup.Type type)             { signal.dispatch(new Collect(type)); }

    class Dialog implements TriggerEvent {
        public final String key;
        private Dialog(TilemapObject.Trigger trigger) {
            this.key = trigger.type;
            trigger.activated = true;
        }
    }

    class Collect implements TriggerEvent {
        public final Pickup.Type pickupType;
        private Collect(Pickup.Type pickupType) {
            this.pickupType = pickupType;
        }
    }
}
