package lando.systems.ld58.game.actions.types;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import lando.systems.ld58.game.actions.Action;

public class DelayAction extends Action {

    private final float duration;

    public DelayAction(float duration) {
        this.duration = duration;
    }

    @Override
    public void start(Entity entity, Engine engine) {/* nothing to do */}

    @Override
    public void update(Entity entity, Engine engine, float delta) {/* nothing to do */}

    @Override
    public boolean isComplete() {
        return elapsed >= duration;
    }
}
