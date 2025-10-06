package lando.systems.ld58.particles.effects;

import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld58.assets.AnimType;
import lando.systems.ld58.game.Systems;
import lando.systems.ld58.game.components.Position;
import lando.systems.ld58.particles.ParticleData;
import lando.systems.ld58.particles.ParticleEffect;
import lando.systems.ld58.particles.ParticleEffectParams;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BlockBreakEffect implements ParticleEffect {

    public static class Params implements ParticleEffectParams {
        public Position target;
        public boolean once;

        public Params(Position target ) {
            this.target = target;
            this.once = false;
        }

        @Override
        public boolean isComplete() {
            return once;
        }
    }

    @Override
    public List<ParticleData> spawn(ParticleEffectParams parameters) {
        var params = (BlockBreakEffect.Params) parameters;
        // Only emit once...
        if (params.once) return Collections.emptyList();
        else params.once = true;

        var pool = Systems.particles.pool;
        return IntStream.range(0, 20).boxed()
            .map(i -> {
                var angle = MathUtils.random(0f, 360f);
                var speed = MathUtils.random(100f, 150f);
                var startSize = MathUtils.random(8f, 12f);
                var ttl = MathUtils.random(0.5f, 1f);

                return ParticleData.initializer(pool.obtain())
                    .animation(AnimType.COIN_BLOCK.get())
                    .startPos(params.target.x, params.target.y)
                    .velocity(speed, angle)
                    .startSize(startSize)
                    .endSize(startSize/4f, startSize/4f)
                    .endAlpha(0f)
                    .timeToLive(ttl)
                    .init();
            })
            .collect(Collectors.toList());
    }
}
