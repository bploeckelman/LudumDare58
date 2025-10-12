package lando.systems.ld58.game;

import com.badlogic.ashley.signals.Signal;
import lando.systems.ld58.game.signals.CooldownEvent;
import lando.systems.ld58.game.signals.EntityEvent;
import lando.systems.ld58.game.signals.StateEvent;

public class Signals {
    // TODO: replace these w/SignalEvent usage and remove the Signals class
    public static final Signal<CooldownEvent> cooldownReset = new Signal<>();
    public static final Signal<StateEvent> changeState = new Signal<>();
    public static final Signal<EntityEvent> removeEntity = new Signal<>();
}
