package lando.systems.ld58.particles.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld58.assets.IconType;
import lando.systems.ld58.game.Systems;
import lando.systems.ld58.game.components.Position;
import lando.systems.ld58.particles.ParticleData;
import lando.systems.ld58.particles.ParticleEffect;
import lando.systems.ld58.particles.ParticleEffectParams;
import lando.systems.ld58.utils.Time;

import java.util.Collections;
import java.util.List;

public class TestEffect implements ParticleEffect {

    public static class Params implements ParticleEffectParams {
        public Position target;
        public Color startColor;
        public float interval;
        public float timer;

        public Params(Position target, Color startColor, float interval) {
            this.target = target;
            this.startColor = startColor;
            this.interval = interval;
            this.timer = interval;
        }

        @Override
        public boolean isComplete() {
            // Perpetual emission
            return false;
        }
    }

    @Override
    public List<ParticleData> spawn(ParticleEffectParams parameters) {
        var params = (Params) parameters;

        params.timer = MathUtils.clamp(params.timer - Time.delta, 0, params.interval);
        if (params.timer == 0f) {
            params.timer = params.interval;
        } else {
            return Collections.emptyList();
        }

        var angle = MathUtils.random(0f, 360f);
        var yVel = MathUtils.random(100f, 150f);
        var endRot = MathUtils.random(angle - 360f, angle + 360f);
        var startSize = MathUtils.random(10f, 20f);
        var ttl = MathUtils.random(.3f, .6f);

        var pool = Systems.particles.pool;
        var p = ParticleData.initializer(pool.obtain())
            .keyframe(IconType.HEART.get())
            .startPos(params.target.x, params.target.y)
            .startRotation(angle)
            .endRotation(endRot)
            .velocity(0f, yVel)
            .startColor(params.startColor)
            .startSize(startSize)
            .endSize(startSize/4f, startSize/4f)
            .timeToLive(ttl)
            .init();

        return Collections.singletonList(p);
    }
}
