package lando.systems.ld58.particles.effects;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Interpolation;
import lando.systems.ld58.game.Components;
import lando.systems.ld58.game.Systems;
import lando.systems.ld58.game.components.KirbyPower;
import lando.systems.ld58.game.components.Position;
import lando.systems.ld58.particles.ParticleData;
import lando.systems.ld58.particles.ParticleEffect;
import lando.systems.ld58.particles.ParticleEffectParams;

import java.util.Collections;
import java.util.List;

public class SuckEffect implements ParticleEffect {

    public static class Params implements ParticleEffectParams {
        public KirbyPower power;
        public Entity playerEntity;
        public Position enemyPos;
        public boolean once;

        public Params(KirbyPower power, Entity playerEntity, Position enemyPos) {
            this.power = power;
            this.playerEntity = playerEntity;
            this.enemyPos = enemyPos;
            this.once = false;
        }

        @Override
        public boolean isComplete() {
            return once;
        }
    }

    @Override
    public List<ParticleData> spawn(ParticleEffectParams parameters) {
        var params = (SuckEffect.Params) parameters;
        // Only emit once...
        if (params.once) return Collections.emptyList();
        else params.once = true;

        var playerPos = Components.get(params.playerEntity, Position.class);
        var animType = params.power.getOriginalEnemyIdleAnimType();
        var startSize = animType.get().getKeyFrame(0f).getRegionWidth();
        var scale = 4f;
        var ttl = 0.5f;

        var pool = Systems.particles.pool;
        var p = ParticleData.initializer(pool.obtain())
            .animation(animType.get())
            .startPos(params.enemyPos.x, params.enemyPos.y)
            .targetPos(playerPos)
            .interpolation(Interpolation.exp5Out)
            .startSize(startSize)
            .endSize(startSize / scale)
            .timeToLive(ttl)
            .init();

        return Collections.singletonList(p);
    }
}
