package lando.systems.ld58.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.github.tommyettinger.digital.Stringf;
import lando.systems.ld58.game.Components;
import lando.systems.ld58.game.components.Id;
import lando.systems.ld58.game.components.Interp;
import lando.systems.ld58.utils.Util;

public class InterpSystem extends IteratingSystem {

    private static final String TAG = InterpSystem.class.getSimpleName();

    public InterpSystem() {
        super(Family.one(Interp.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        var interp = Components.optional(entity, Interp.class).orElseThrow();
        if (interp.isPaused()) return;

        // Enforce speed multiplier constraint
        if (interp.speed <= 0) {
            var id = Components.optional(entity, Id.class).orElse(Id.UNKNOWN);
            Util.log(TAG, Stringf.format("interpolator %d has invalid speed multiplier %.2f <= 0 - reset to 1", id.id, interp.speed));
            interp.speed = 1f;
        }

        interp.update(delta);
    }
}
