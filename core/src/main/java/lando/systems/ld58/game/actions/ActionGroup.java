package lando.systems.ld58.game.actions;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.github.tommyettinger.digital.Stringf;
import lando.systems.ld58.utils.Util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ActionGroup extends Action {

    protected final List<Action> actions;

    public ActionGroup(Action... actions) {
        this.actions = Arrays.asList(actions);
    }

    @Override
    public void start(Entity entity, Engine engine) {
        Util.log(getClass().getSimpleName(), Stringf.format("Starting action group:\n%s", actions.stream()
            .map(a -> a.getClass().getSimpleName())
            .collect(Collectors.joining(", ", "[", "]"))));
    }

    public void abort() {
        for (var action : actions) {
            action.abort();
        }
    }
}
