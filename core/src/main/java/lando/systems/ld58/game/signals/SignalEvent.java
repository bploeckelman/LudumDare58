package lando.systems.ld58.game.signals;

import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;

public interface SignalEvent {
    Signal<SignalEvent> signal = new Signal<>();

    static void addListener(Listener<SignalEvent> listener) {
        signal.add(listener);
    }

    static void removeListener(Listener<SignalEvent> listener) {
        signal.remove(listener);
    }
}
