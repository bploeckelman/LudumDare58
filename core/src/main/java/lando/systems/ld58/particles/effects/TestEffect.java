package lando.systems.ld58.particles.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld58.assets.IconType;
import lando.systems.ld58.game.Systems;
import lando.systems.ld58.particles.ParticleData;
import lando.systems.ld58.particles.ParticleEffect;
import lando.systems.ld58.particles.ParticleEffectParams;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TestEffect implements ParticleEffect {

    public static class Params implements ParticleEffectParams {
        public float startX;
        public float startY;
        public Color startColor;
        public boolean once;

        public Params(float x, float y, Color startColor) {
            this.startX = x;
            this.startY = y;
            this.startColor = startColor;
            this.once = false;
        }
    }

    // TODO: still need a way to potentially limit these
    //  to X particles per Y time, maybe add as a Param field?

    @Override
    public List<ParticleData> spawn(ParticleEffectParams parameters) {
        var params = (Params) parameters;
        if (params.once) return Collections.emptyList();
        params.once = true;

        var pool = Systems.particles.pool;
        return IntStream.range(0, 100).boxed().map(i -> {
            var angle = MathUtils.random(0f, 360f);
            var speed = MathUtils.random(50f, 100f);
            var endRot = MathUtils.random(angle - 360f, angle + 360f);
            var startSize = MathUtils.random(10f, 20f);
            return ParticleData.initializer(pool.obtain())
                .keyframe(IconType.HEART.get())
                .startPos(params.startX, params.startY)
                .startRotation(angle)
                .endRotation(endRot)
                .velocity(
                    MathUtils.cosDeg(angle) * speed,
                    MathUtils.sinDeg(angle) * speed)
                .startColor(params.startColor)
                .startSize(startSize)
                .endSize(0f, startSize * 2f)
                .timeToLive(MathUtils.random(.25f, .5f))
                .init();
        })
        .collect(Collectors.toList());
    }
}
