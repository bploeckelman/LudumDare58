package lando.systems.ld58.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import lando.systems.ld58.game.Components;
import lando.systems.ld58.game.Constants;
import lando.systems.ld58.game.Factory;
import lando.systems.ld58.game.Signals;
import lando.systems.ld58.game.components.Emitter;
import lando.systems.ld58.game.components.Particle;
import lando.systems.ld58.game.components.Position;
import lando.systems.ld58.game.components.renderable.Animator;
import lando.systems.ld58.game.components.renderable.Image;
import lando.systems.ld58.game.components.renderable.Outline;
import lando.systems.ld58.game.components.renderable.Renderable;
import lando.systems.ld58.game.signals.EntityEvent;
import lando.systems.ld58.particles.ParticleData;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ParticleSystem extends IteratingSystem implements Disposable {

    private static final int MAX_PARTICLES = 5000;

    public Pool<ParticleData> pool = Pools.get(ParticleData.class, MAX_PARTICLES);
    public List<ParticleData> activeData = new ArrayList<>();
    public List<Entity> activeParticles = new ArrayList<>();

    public ParticleSystem() {
        super(Family.one(Emitter.class, Particle.class).get());
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        // Remove dead particle entities
        for (int i = activeParticles.size() - 1; i >= 0; i--) {
            var entity = activeParticles.get(i);
            var particle = Components.get(entity, Particle.class);
            if (particle != null && particle.data.isDead()) {
                activeParticles.remove(i);
                Signals.removeEntity.dispatch(new EntityEvent.Remove(entity));
            }
        }

        // Free dead particle data back to the pool
        for (int i = activeData.size() - 1; i >= 0; --i) {
            var particleData = activeData.get(i);
            if (particleData.isDead()) {
                activeData.remove(i);
                pool.free(particleData);
            }
        }
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        var particle = Components.get(entity, Particle.class);
        var emitter = Components.get(entity, Emitter.class);

        if (particle != null) {
            updateParticle(entity, particle, delta);
        }
        else if (emitter != null) {
            var particlesData = emitter.spawn();
            if (!particlesData.isEmpty()) {
                var entities = particlesData.stream()
                    .map(this::createEntity)
                    .collect(Collectors.toList());

                entities.forEach(getEngine()::addEntity);

                activeData.addAll(particlesData);
                activeParticles.addAll(entities);
            }
        }
    }

    @Override
    public void dispose() {
        clear();
    }

    public void clear() {
        activeData.forEach(pool::free);
        activeData.clear();
        activeParticles.forEach(getEngine()::removeEntity);
        activeParticles.clear();
    }

    private Entity createEntity(ParticleData data) {
        var entity = Factory.createEntity();
        entity.add(new Particle(data));
        entity.add(new Position(data.xStart, data.yStart));
        entity.add(new Outline(Color.DARK_GRAY, Color.CLEAR_WHITE, 4f));

        Renderable renderable = null;
        if (data.animation != null) {
            renderable = new Animator(data.animation);
            entity.add((Animator) renderable);
        }
        else if (data.keyframe  != null) {
            renderable = new Image(data.keyframe);
            entity.add((Image) renderable);
        }
        if (renderable != null) {
            renderable.tint.set(data.rStart, data.gStart, data.bStart, data.aStart);
            renderable.origin.set(data.widthStart/2f, data.heightStart/2f);
            renderable.size.set(data.widthStart, data.heightStart);
            renderable.depth = Constants.Z_DEPTH_FOREGROUND;
        }
        return entity;
    }

    private void updateParticle(Entity entity, Particle particle, float delta) {
        var data = particle.data;
        var position = Components.get(entity, Position.class);
        var animator = Components.get(entity, Animator.class);
        var image = Components.get(entity, Image.class);

        float lifetime, progress;

        if (data.timed) {
            data.ttl -= delta;
            if (data.ttl <= 0f && !data.persistent) {
                data.dead = true;
            }
            lifetime = MathUtils.clamp(data.ttl / data.ttlMax , 0f, 1f);
        } else {
            data.ttl += delta;
            lifetime = MathUtils.clamp(data.ttl, 0f, 1f);
        }
        progress = data.interpolation.apply(0f, 1f, MathUtils.clamp(1f - lifetime, 0f, 1f));

        // TODO: push to Animator component
        if (data.animation != null) {
            if (!data.persistent && !data.animUnlocked && data.timed) {
                data.animTime = progress * data.animation.getAnimationDuration();
            } else {
                data.animTime += delta;
            }
            data.keyframe = data.animation.getKeyFrame(data.animTime);
        }

        if (data.path != null) {
            // https://github.com/libgdx/libgdx/wiki/Path-interface-and-Splines#make-the-sprite-traverse-at-constant-speed
            var pathPos = data.path.derivativeAt(progress);
            var arcLength = progress + (delta * data.ttl / data.path.spanCount()) / pathPos.len();
            pathPos.set(data.path.valueAt(arcLength));
            position.set(pathPos.x, pathPos.y);
        }
        else if (data.targeted) {
            position.set(
                MathUtils.lerp(data.xStart, data.xTarget, progress),
                MathUtils.lerp(data.yStart, data.yTarget, progress));
        }
        else {
            data.accel.x *= data.accDamp;
            data.accel.y *= data.accDamp;
            if (MathUtils.isEqual(data.accel.x, 0f, 0.01f)) data.accel.x = 0f;
            if (MathUtils.isEqual(data.accel.y, 0f, 0.01f)) data.accel.y = 0f;

            data.velocity.x += data.accel.x * delta;
            data.velocity.y += data.accel.y * delta;

            position.x += (int) (data.velocity.x * delta);
            position.y += (int) (data.velocity.y * delta);
        }

        data.width  = MathUtils.lerp(data.widthStart,  data.widthEnd,  progress);
        data.height = MathUtils.lerp(data.heightStart, data.heightEnd, progress);
        if (animator != null) {
            animator.origin.set(data.width/2f, data.height/2f);
            animator.size.set(data.width, data.height);
        } else if (image != null) {
            image.origin.set(data.width/2f, data.height/2f);
            image.size.set(data.width, data.height);
        }

        // TODO: this won't work because Image and Animator don't support rotation
        data.rotation = MathUtils.lerp(data.rotationStart, data.rotationEnd, progress);

        data.r = MathUtils.lerp(data.rStart, data.rEnd, progress);
        data.g = MathUtils.lerp(data.gStart, data.gEnd, progress);
        data.b = MathUtils.lerp(data.bStart, data.bEnd, progress);
        data.a = MathUtils.lerp(data.aStart, data.aEnd, progress);
        if (animator != null) {
            animator.tint.set(data.r, data.g, data.b, data.a);
        } else if (image != null) {
            image.tint.set(data.r, data.g, data.b, data.a);
        }

        data.collisionRect.set(
            position.x - data.width/2,
            position.y - data.height/2,
            data.width, data.height);
    }
}
