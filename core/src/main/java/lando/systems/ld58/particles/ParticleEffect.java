package lando.systems.ld58.particles;

import lando.systems.ld58.particles.effects.TestEffect;

import java.util.List;

// TODO: rework into AssetType
public abstract class ParticleEffect {

    public abstract List<Particle> spawn(ParticleEffectParams params);

    public enum Type {
        TEST(TestEffect.class)
//        DIRT(DirtEffect.class),
//        SPARK(SparkEffect.class),
//        SHAPE(ShapeEffect.class),
//        BLOOD_SPLAT(BloodSplatEffect.class),
//        BLOOD(BloodEffect.class),
//        BLOOD_FOUNTAIN(BloodFountainEffect.class),
//        FIRE(FireEffect.class),
//        BULLET_EXPLOSION(BulletExplosionEffect.class),
        ;

        public final Class<? extends ParticleEffect> particleEffect;

        Type(Class<? extends ParticleEffect> particleEffect) {
            this.particleEffect = particleEffect;
        }
    }
}
