package lando.systems.ld58.game.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld58.Main;
import lando.systems.ld58.assets.AnimType;
import lando.systems.ld58.utils.Util;

public class Animator extends Renderable {

    public AnimType type = null;
    public TextureRegion keyframe = null;
    public Animation<TextureRegion> animation = null;

    public float stateTime = 0;
    public int facing = 1;

    public Animator(AnimType type) {
        this(type.get());
        this.type = type;
    }

    public Animator(AnimType type, Vector2 origin) {
        this(type);
        this.origin.set(origin);
    }

    public Animator(Animation<TextureRegion> animation) {
        this(animation.getKeyFrame(0));
        this.animation = animation;
    }

    public Animator(TextureRegion keyframe) {
        this.keyframe = keyframe;
        this.size.set(keyframe.getRegionWidth(), keyframe.getRegionHeight());
    }

    public TextureRegion keyframe() {
        return keyframe;
    }

    public float start(AnimType type) {
        stateTime = 0;
        return play(type);
    }

    public float play(AnimType type) {
        this.type = type;
        return play(type.get());
    }

    public float play(Animation<TextureRegion> anim) {
        if (anim == null) return 0;
        this.animation = anim;
        return this.animation.getAnimationDuration();
    }

    public boolean isComplete() {
        var isNormal = animation.getPlayMode() == Animation.PlayMode.NORMAL;
        var isFinished = animation.isAnimationFinished(stateTime);
        return isNormal && isFinished;
    }

    public boolean hasAnimation() {
        return animation != null;
    }

    public boolean hasKeyframe() {
        return keyframe != null;
    }

    public boolean isIncomplete() {
        return !isComplete();
    }
}
