package lando.systems.ld58.game.actions.types;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.github.tommyettinger.digital.Stringf;
import lando.systems.ld58.game.Components;
import lando.systems.ld58.game.actions.Action;
import lando.systems.ld58.game.components.Id;
import lando.systems.ld58.game.components.Position;
import lando.systems.ld58.utils.Util;

public class SetPositionAction extends Action {

    private static final String TAG = SetPositionAction.class.getSimpleName();

    private final Position target;

    private boolean isPositionSet;

    public SetPositionAction(int x, int y) {
        this(new Position(x, y));
    }

    public SetPositionAction(Position target) {
        this.target = target;
        this.isPositionSet = false;
    }

    @Override
    public void start(Entity entity, Engine engine) {
        var position = Components.get(entity, Position.class);
        if (position == null) {
            Util.warn(TAG, Stringf.format("Cannot perform action because Entity %s doesn't have a Position component",
                Components.optional(entity, Id.class).orElse(Id.UNKNOWN)));
            isPositionSet = true;
            return;
        }

        position.set(target);
        isPositionSet = true;
    }

    @Override
    public void update(Entity entity, Engine engine, float delta) {/* nothing to do */}

    @Override
    public boolean isComplete() {
        return isPositionSet;
    }
}
