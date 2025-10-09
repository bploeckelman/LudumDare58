package lando.systems.ld58.assets;

import java.util.EnumMap;

public sealed interface AssetType<T> permits
    AnimType,
    ColorType,
    EffectType,
    EmitterType,
    FontType,
    IconType,
    ImageType,
    MusicType,
    SkinType,
    SoundType
{
    T get();

    static <E extends Enum<E>, T> EnumMap<E, T> createContainer(Class<E> enumClass) {
        return new EnumMap<>(enumClass);
    }
}
