package lando.systems.ld58.assets;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.github.tommyettinger.digital.Stringf;

import java.util.EnumMap;

public interface AssetType<T> {

    static <E extends Enum<E>, T> EnumMap<E, T> createContainer(Class<E> enumClass) {
        return new EnumMap<>(enumClass);
    }

    T get();

    // ------------------------------------------------------------------------
    // Workarounds for quirks of the java enum implementation and
    // lack of automatic dispatch for 'overridden' static methods
    //
    // The public static methods perform 'manual' dispatch to the equivalent
    // static methods in the concrete AssetEnum type, if such a method exists,
    // and throws if the method is supposed to exist but wasn't implemented
    // as a sort of 'poor man's override' for static methods
    // ------------------------------------------------------------------------

    static void load(Class<? extends AssetType<?>> enumClass, Assets assets) {
        dispatch(enumClass, "load", assets, false);
    }

    static void init(Class<? extends AssetType<?>> enumClass, Assets assets) {
        dispatch(enumClass, "init", assets, true);
    }

    static void dispatch(Class<? extends AssetType<?>> enumClass, String methodName, Assets assets, boolean required) {
        try {
            ClassReflection.getDeclaredMethod(enumClass, methodName, Assets.class).invoke(null, assets);
        } catch (Exception e) {
            if (e instanceof ReflectionException) {
                if (required) {
                    throw new GdxRuntimeException(Stringf.format("%s must implement static %s(Assets) method", enumClass.getSimpleName(), methodName));
                }
            } else {
                throw new GdxRuntimeException(Stringf.format("Failed to call %s.%s", enumClass.getSimpleName(), methodName), e);
            }
        }
    }
}
