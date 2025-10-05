package lando.systems.ld58.game.actions.types;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.github.tommyettinger.digital.Stringf;
import lando.systems.ld58.assets.AnimType;
import lando.systems.ld58.game.Components;
import lando.systems.ld58.game.Signals;
import lando.systems.ld58.game.actions.Action;
import lando.systems.ld58.game.components.renderable.Animator;
import lando.systems.ld58.game.components.Id;
import lando.systems.ld58.game.signals.AnimationEvent;
import lando.systems.ld58.utils.Util;

public class PlayAnimAction extends Action {

    private static final String TAG = PlayAnimAction.class.getSimpleName();

    private final AnimType animType;
    private boolean dispatched;

    public PlayAnimAction(AnimType animType) {
        this.animType = animType;
        this.dispatched = false;
    }

    @Override
    public void start(Entity entity, Engine engine) {
        var animator = Components.get(entity, Animator.class);
        if (animator == null) {
            Util.warn(TAG, Stringf.format("Cannot perform action because Entity %s doesn't have an Animator component",
                Components.optional(entity, Id.class).orElse(Id.UNKNOWN)));
            return;
        }

        Signals.animStart.dispatch(new AnimationEvent.Start(animator, animType));
        dispatched = true;
    }

    @Override
    public void update(Entity entity, Engine engine, float delta) {/* nothing to do */}

    @Override
    public boolean isComplete() {
        return dispatched;
    }
}
