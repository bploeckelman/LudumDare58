package lando.systems.ld58.game.actions;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;

public class SequentialActions extends ActionGroup {

    private int currentIndex;

    public SequentialActions(Action... actions) {
        super(actions);
        this.currentIndex = 0;
    }

    @Override
    public void update(Entity entity, Engine engine, float delta) {
        if (currentIndex >= actions.size()) return;

        var currentAction = actions.get(currentIndex);
        currentAction.tick(entity, engine, delta);

        if (currentAction.isComplete()) {
            currentIndex++;
        }
    }

    @Override
    public boolean isComplete() {
        return currentIndex >= actions.size();
    }
}
