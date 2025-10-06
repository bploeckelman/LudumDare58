package lando.systems.ld58.particles.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld58.game.Systems;
import lando.systems.ld58.game.components.KirbyPower;
import lando.systems.ld58.game.components.Position;
import lando.systems.ld58.particles.ParticleData;
import lando.systems.ld58.particles.ParticleEffect;
import lando.systems.ld58.particles.ParticleEffectParams;

import java.util.Collections;
import java.util.List;

public class SpitEffect implements ParticleEffect {

    public static class Params implements ParticleEffectParams {
        public KirbyPower power;
        public Position startPos;
        public boolean once;

        public Params(KirbyPower power, Position startPos) {
            this.power = power;
            this.startPos = startPos;
            this.once = false;
        }

        @Override
        public boolean isComplete() {
            return once;
        }
    }

    @Override
    public List<ParticleData> spawn(ParticleEffectParams parameters) {
        var params = (Params) parameters;
        // Only emit once...
        if (params.once) return Collections.emptyList();
        else params.once = true;

        var animType = params.power.getOriginalEnemyIdleAnimType();
        var startSize = animType.get().getKeyFrame(0f).getRegionWidth();
        var yVel = MathUtils.random(200f, 300f);
        // TODO: might be more fun to crap it out behind you,
        //  so take player facing dir and vel into account... if time
        var angle = MathUtils.random(-45f, -135f); // 90deg in upwards cone (angles are CCW)
        var scale = 2f;
        var ttl = 2f;

        var pool = Systems.particles.pool;
        var p = ParticleData.initializer(pool.obtain())
            .animation(animType.get())
            .startPos(params.startPos.x, params.startPos.y)
            .velocityDirection(yVel, angle)
            .acceleration(0f, 500f)
            .accelerationDamping(0.75f)
            .startColor(Color.WHITE)
            .endAlpha(0f)
            .startSize(startSize)
            .endSize(startSize * scale)
            .timeToLive(ttl)
            .init();

        return Collections.singletonList(p);
    }
}
