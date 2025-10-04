package lando.systems.ld58.assets;

import com.badlogic.gdx.audio.Sound;
import com.github.tommyettinger.digital.Stringf;
import lando.systems.ld58.utils.Util;

import java.util.EnumMap;

public enum SoundType implements AssetType<Sound> {
      JUMP("jump.ogg")
    ;

    private static final String TAG = SoundType.class.getSimpleName();
    private static final EnumMap<SoundType, Sound> container = AssetType.createContainer(SoundType.class);

    private final String path;

    SoundType(String filename) {
        this.path = "audio/sounds/" + filename;
    }

    @Override
    public Sound get() {
        return container.get(this);
    }

    public static void load(Assets assets) {
        var mgr = assets.mgr;
        for (var type : SoundType.values()) {
            mgr.load(type.path, Sound.class);
        }
    }

    public static void init(Assets assets) {
        var mgr = assets.mgr;
        for (var type : SoundType.values()) {
            var sound = mgr.get(type.path, Sound.class);
            if (sound == null) {
                Util.log(TAG, Stringf.format("sound '%s' not found for type %s", type.path, type));
                continue;
            }
            container.put(type, sound);
        }
    }
}
