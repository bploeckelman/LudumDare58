package lando.systems.ld58.assets;

import com.badlogic.gdx.utils.Null;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class AssetTypeRegistry {

    private final Map<Class<? extends AssetType<?>>, Consumer<Assets>> initializers = new HashMap<>();
    private final Map<Class<? extends lando.systems.ld58.assets.AssetType<?>>, Consumer<Assets>> loaders = new HashMap<>();

    public AssetTypeRegistry() {
        register(AnimType.class,    AnimType::init);
        register(ColorType.class,   ColorType::init);
        register(EffectType.class,  EffectType::init);
        register(EmitterType.class, EmitterType::init);
        register(FontType.class,   FontType::init,  FontType::load);
        register(IconType.class,    IconType::init);
        register(ImageType.class,   ImageType::init, ImageType::load);
        register(MusicType.class,   MusicType::init, MusicType::load);
        // TODO: restore after fixing up loading of skins with textra fonts
//        register(SkinType.class,    SkinType::init);
        register(SoundType.class,   SoundType::init, SoundType::load);
    }

    public <E extends Enum<E> & lando.systems.ld58.assets.AssetType<?>> void register(Class<E> enumClass, Consumer<Assets> initializer) {
        register(enumClass, initializer, null);
    }

    public <E extends Enum<E> & lando.systems.ld58.assets.AssetType<?>> void register(Class<E> enumClass, Consumer<Assets> initializer, @Null Consumer<Assets> loader) {
        Objects.requireNonNull(initializer, "initializer cannot be null");
        initializers.put(enumClass, initializer);

        if (loader != null) {
            loaders.put(enumClass, loader);
        }
    }

    public void initAll(Assets assets) {
        initializers.values().forEach(initializer -> initializer.accept(assets));
    }

    public void loadAll(Assets assets) {
        loaders.values().forEach(loader -> loader.accept(assets));
    }
}
