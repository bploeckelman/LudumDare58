package lando.systems.ld58.assets;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.github.tommyettinger.digital.Stringf;

import java.util.EnumMap;

public interface AssetType<T> {

    T get();

    static <E extends Enum<E>, T> EnumMap<E, T> createContainer(Class<E> enumClass) {
        return new EnumMap<>(enumClass);
    }
}
