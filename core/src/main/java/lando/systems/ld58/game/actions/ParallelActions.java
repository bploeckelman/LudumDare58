package lando.systems.ld58.game.actions;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;

public class ParallelActions extends ActionGroup {

    public ParallelActions(Action... actions) {
        super(actions);
    }

    @Override
    public void update(Entity entity, Engine engine, float delta) {
        for (var action : actions) {
            if (!action.isComplete()) {
                action.tick(entity, engine, delta);
            }
        }
    }

    @Override
    public boolean isComplete() {
        return actions.stream().allMatch(Action::isComplete);
    }
}
