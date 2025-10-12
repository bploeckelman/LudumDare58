package lando.systems.ld58.game.signals;

import com.badlogic.gdx.math.Vector2;
import lando.systems.ld58.assets.AnimType;
import lando.systems.ld58.game.components.renderable.Animator;
import lando.systems.ld58.utils.FramePool;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class AnimationEvent implements SignalEvent {

    public Animator animator;
    public Animator animator() { return animator; }

    public static void facing(Animator animator, int newFacing)         { signal.dispatch(new AnimationEvent.Facing(animator, newFacing)); }
    public static void play(Animator animator, AnimType type)           { signal.dispatch(new AnimationEvent.Play(animator, type)); }
    public static void scale(Animator animator, float newX, float newY) { signal.dispatch(new AnimationEvent.Scale(animator, newX, newY)); }
    public static void scale(Animator animator, Vector2 newScale)       { signal.dispatch(new AnimationEvent.Scale(animator, newScale.x, newScale.y)); }
    public static void start(Animator animator, AnimType type)          { signal.dispatch(new AnimationEvent.Start(animator, type)); }

    public static final class Facing extends AnimationEvent {
        public int newFacing;
        private Facing(Animator animator, int newFacing) {
            super(animator);
            this.newFacing = newFacing;
        }
    }

    public static final class Play extends AnimationEvent {
        public AnimType animType;
        private Play(Animator animator, AnimType animType) {
            super(animator);
            this.animType = animType;
        }
    }

    public static final class Scale extends AnimationEvent {
        public Vector2 newScale;
        private Scale(Animator animator, float newScaleX, float newScaleY) {
            this(animator, FramePool.vec2(newScaleX, newScaleY));
        }
        private Scale(Animator animator, Vector2 newScale) {
            super(animator);
            this.newScale = newScale;
        }
    }

    public static final class Start extends AnimationEvent {
        public AnimType animType;
        private Start(Animator animator, AnimType animType) {
            super(animator);
            this.animType = animType;
        }
    }
}
