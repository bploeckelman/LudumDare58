package lando.systems.ld58.particles;

import java.util.List;

public interface ParticleEffect {
    List<ParticleData> spawn(ParticleEffectParams params);
}
