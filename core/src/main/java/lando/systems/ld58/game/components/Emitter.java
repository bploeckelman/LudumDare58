package lando.systems.ld58.game.components;

import com.badlogic.ashley.core.Component;
import lando.systems.ld58.assets.EmitterType;
import lando.systems.ld58.particles.Particle;
import lando.systems.ld58.particles.ParticleEffect;
import lando.systems.ld58.particles.ParticleEffectParams;

import java.util.List;

public class Emitter extends Renderable implements Component {

    public final EmitterType type;
    public final ParticleEffect effect;

    public ParticleEffectParams params;

    public Emitter(EmitterType type, ParticleEffectParams params) {
        this.type = type;
        this.effect = type.get();
        this.params = params;
    }

    public List<Particle> spawn() {
        return effect.spawn(params);
    }
}
