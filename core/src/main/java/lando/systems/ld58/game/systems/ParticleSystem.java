package lando.systems.ld58.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import lando.systems.ld58.game.Components;
import lando.systems.ld58.game.components.Emitter;
import lando.systems.ld58.particles.Particle;
import lando.systems.ld58.particles.ParticleEffect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParticleSystem extends IteratingSystem implements Disposable {

    private static final int MAX_PARTICLES = 5000;

    public Pool<Particle> pool = Pools.get(Particle.class, MAX_PARTICLES);
    public List<Particle> active = new ArrayList<>();
    public final Map<ParticleEffect.Type, ParticleEffect> effects = new HashMap<>();

    public ParticleSystem() {
        super(Family.one(Emitter.class).get());
    }
    @Override
    public void update(float delta) {
        super.update(delta);
        for (int i = active.size() - 1; i >= 0; --i) {
            var particle = active.get(i);
            particle.update(delta);

            if (particle.isDead()) {
                active.remove(i);
                pool.free(particle);
            }
        }
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        var emitter = Components.get(entity, Emitter.class);
        var particles = emitter.spawn();
        active.addAll(particles);
    }

    @Override
    public void dispose() {
        clear();
    }

    @Deprecated(since = "move to render system")
    public void render(SpriteBatch batch) {
        active.forEach(particle -> particle.render(batch));
    }

    public void clear() {
        active.forEach(pool::free);
        active.clear();
    }
}
