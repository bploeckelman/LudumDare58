package lando.systems.ld58.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.ashley.systems.IteratingSystem;
import lando.systems.ld58.game.Components;
import lando.systems.ld58.game.Signals;
import lando.systems.ld58.game.components.Animator;
import lando.systems.ld58.game.signals.AnimationEvent;
import lando.systems.ld58.utils.Calc;
import lando.systems.ld58.utils.Util;

public class AnimationSystem extends IteratingSystem implements Listener<AnimationEvent> {

    private static final String TAG = AnimationSystem.class.getSimpleName();

    public AnimationSystem() {
        super(Family.one(Animator.class).get());
        Signals.animFacing.add(this);
        Signals.animScale.add(this);
        Signals.animStart.add(this);
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        var anim = Components.get(entity, Animator.class);
        if (anim == null || !anim.hasAnimation()) return;

        // Update animation time and keyframe
        anim.stateTime += delta;
        anim.keyframe = anim.animation.getKeyFrame(anim.stateTime);

        // Always interpolate scale back towards default
        var sx = Calc.approach(Calc.abs(anim.scale.x), anim.defaultScale.x, delta * anim.scaleReturnSpeed);
        var sy = Calc.approach(Calc.abs(anim.scale.y), anim.defaultScale.y, delta * anim.scaleReturnSpeed);

        // Apply facing
        anim.scale.set(anim.facing * sx, sy);
    }

    @Override
    public void receive(Signal<AnimationEvent> signal, AnimationEvent event) {
        var animator = event.animator();
        if (event instanceof AnimationEvent.Facing) {
            var facing = (AnimationEvent.Facing) event;
            animator.facing = facing.newFacing;
        } else if (event instanceof AnimationEvent.Play) {
            var play = (AnimationEvent.Play) event;
            animator.play(play.animType);
        } else if (event instanceof AnimationEvent.Scale) {
            var scale = (AnimationEvent.Scale) event;
            animator.scale.set(scale.newScale);
        } else if (event instanceof AnimationEvent.Start) {
            var start = (AnimationEvent.Start) event;
            animator.start(start.animType);
        } else {
            Util.warn(TAG, "unhandled AnimationEvent type: " + event.getClass().getSimpleName());
        }
    }
}
