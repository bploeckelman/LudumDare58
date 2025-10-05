package lando.systems.ld58.assets;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.github.tommyettinger.digital.Stringf;
import lando.systems.ld58.particles.ParticleEffect;
import lando.systems.ld58.particles.effects.TestEffect;

import java.util.EnumMap;

public enum EmitterType implements AssetType<ParticleEffect> {
    TEST(TestEffect.class)
    ;

    private static final String TAG = EmitterType.class.getSimpleName();
    private static final EnumMap<EmitterType, ParticleEffect> container = AssetType.createContainer(EmitterType.class);

    public final Class<? extends ParticleEffect> effectType;

    EmitterType(Class<? extends ParticleEffect> effectType) {
        this.effectType = effectType;
    }

    @Override
    public ParticleEffect get() {
        return container.get(this);
    }

    public static void init(Assets assets) {
        for (var type : EmitterType.values()) {
            try {
                var effect = ClassReflection.newInstance(type.effectType);
                container.put(type, effect);
            } catch (ReflectionException e) {
                throw new GdxRuntimeException(
                    Stringf.format("%s: effect '%s' not found for type '%s'",
                    TAG, type.effectType.getSimpleName(), type.name()));
            }
        }
    }
}
