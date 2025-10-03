package lando.systems.ld58.game.actions.types;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.github.tommyettinger.digital.Stringf;
import lando.systems.ld58.game.Components;
import lando.systems.ld58.game.actions.Action;
import lando.systems.ld58.game.components.Id;
import lando.systems.ld58.game.components.Position;
import lando.systems.ld58.utils.Util;

public class MoveToAction extends Action {

    private static final String TAG = MoveToAction.class.getSimpleName();

    private final float duration;
    private final Position target;

    private Position startPos;

    public MoveToAction(float duration, Position target) {
        this.duration = duration;
        this.target = target;
    }

    @Override
    public void start(Entity entity, Engine engine) {
        var position = Components.get(entity, Position.class);
        if (position == null) {
            Util.warn(TAG, Stringf.format("Cannot perform action because Entity %s doesn't have a Position component",
                Components.optional(entity, Id.class).orElse(Id.UNKNOWN)));
            elapsed = duration;
            return;
        }

        startPos = position;

        // TODO: might need to disable Collider and/or set Velocity to zero also?
    }

    @Override
    public void update(Entity entity, Engine engine, float delta) {
        if (startPos == null) return;

        var progress = Math.min(elapsed / duration, 1f);
        Components.get(entity, Position.class).set(
            (int) (startPos.x + (target.x - startPos.x) * progress),
            (int) (startPos.y + (target.y - startPos.y) * progress));
    }

    @Override
    public boolean isComplete() {
        return elapsed >= duration;
    }
}
