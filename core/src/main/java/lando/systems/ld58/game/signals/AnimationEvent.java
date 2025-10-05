package lando.systems.ld58.game.signals;

import com.badlogic.gdx.math.Vector2;
import lando.systems.ld58.assets.AnimType;
import lando.systems.ld58.game.components.renderable.Animator;
import lando.systems.ld58.utils.FramePool;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class AnimationEvent {

    public Animator animator;
    public Animator animator() { return animator; }

    public static final class Facing extends AnimationEvent {
        public int newFacing;

        public Facing(Animator animator, int newFacing) {
            super(animator);
            this.newFacing = newFacing;
        }
    }

    public static final class Play extends AnimationEvent {
        public AnimType animType;

        public Play(Animator animator, AnimType animType) {
            super(animator);
            this.animType = animType;
        }
    }

    public static final class Scale extends AnimationEvent {
        public Vector2 newScale;

        public Scale(Animator animator, float newScaleX, float newScaleY) {
            this(animator, FramePool.vec2(newScaleX, newScaleY));
        }

        public Scale(Animator animator, Vector2 newScale) {
            super(animator);
            this.newScale = newScale;
        }
    }

    public static final class Start extends AnimationEvent {
        public AnimType animType;

        public Start(Animator animator, AnimType animType) {
            super(animator);
            this.animType = animType;
        }
    }
}
