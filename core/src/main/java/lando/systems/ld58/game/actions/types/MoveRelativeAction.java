package lando.systems.ld58.game.actions.types;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.github.tommyettinger.digital.Stringf;
import lando.systems.ld58.game.Components;
import lando.systems.ld58.game.actions.Action;
import lando.systems.ld58.game.components.Id;
import lando.systems.ld58.game.components.Position;
import lando.systems.ld58.game.components.Velocity;
import lando.systems.ld58.utils.Util;

public class MoveRelativeAction extends Action {

    private static final String TAG = MoveRelativeAction.class.getSimpleName();

    private final float duration;
    private final Position amount;

    private Position startPos;
    private Position target;

    public MoveRelativeAction(float duration, int xAmount, int yAmount) {
        this(duration, new Position(xAmount, yAmount));
    }

    public MoveRelativeAction(float duration, Position amount) {
        this.duration = duration;
        this.amount = amount;
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
        target = new Position(startPos.x + amount.x, startPos.y + amount.y);
    }

    @Override
    public void update(Entity entity, Engine engine, float delta) {
        if (startPos == null) return;

        var progress = Math.min(elapsed / duration, 1f);
        Components.get(entity, Position.class).set(
            (int) (startPos.x + (target.x - startPos.x) * progress),
            (int) (startPos.y + (target.y - startPos.y) * progress));

        // Override velocity because we're moving manually over the given duration
        Components.optional(entity, Velocity.class).ifPresent(Velocity::stop);
    }

    @Override
    public boolean isComplete() {
        return elapsed >= duration;
    }
}
