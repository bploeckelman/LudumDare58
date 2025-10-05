package lando.systems.ld58.game.components;

import com.badlogic.ashley.core.Component;
import lando.systems.ld58.particles.ParticleData;

public class Particle implements Component {

    public final ParticleData data;

    public Particle(ParticleData data) {
        this.data = data;
    }
}

