package lando.systems.ld58.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.ashley.systems.IteratingSystem;
import lando.systems.ld58.game.components.Cooldowns;
import lando.systems.ld58.game.signals.CooldownEvent;
import lando.systems.ld58.utils.Util;

public class CooldownSystem extends IteratingSystem implements Listener<CooldownEvent> {

    private static final String TAG = CooldownSystem.class.getSimpleName();

    public CooldownSystem() {
        super(Family.one(Cooldowns.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        var cooldowns = Cooldowns.get(entity).orElseThrow();
        for (var cooldown : cooldowns.entries()) {
            cooldown.update(delta);
        }
    }

    @Override
    public void receive(Signal<CooldownEvent> signal, CooldownEvent event) {
        var cooldowns = event.cooldowns();
        if (event instanceof CooldownEvent.Reset) {
            var reset = (CooldownEvent.Reset) event;
            cooldowns.reset(reset.name);
        } else {
            Util.warn(TAG, "unhandled cooldown event type: " + event.getClass().getSimpleName());
        }
    }
}
