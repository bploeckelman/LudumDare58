package lando.systems.ld58.assets;

import java.util.EnumMap;

public interface AssetType<T> {

    T get();

    static <E extends Enum<E>, T> EnumMap<E, T> createContainer(Class<E> enumClass) {
        return new EnumMap<>(enumClass);
    }
}
