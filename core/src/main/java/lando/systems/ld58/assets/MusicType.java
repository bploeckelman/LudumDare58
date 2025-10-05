package lando.systems.ld58.assets;

import com.badlogic.gdx.audio.Music;
import com.github.tommyettinger.digital.Stringf;
import lando.systems.ld58.utils.Util;

import java.util.EnumMap;

public enum MusicType implements AssetType<Music> {
      CASTLEVANIA("castlevania.ogg"),
      MAIN("music1.ogg"),
    ;

    private static final String TAG = MusicType.class.getSimpleName();
    private static final EnumMap<MusicType, Music> container = AssetType.createContainer(MusicType.class);

    private final String path;

    MusicType(String filename) {
        this.path = "audio/musics/" + filename;
    }

    @Override
    public Music get() {
        return container.get(this);
    }

    public static void load(Assets assets) {
        var mgr = assets.mgr;
        for (var type : MusicType.values()) {
            mgr.load(type.path, Music.class);
        }
    }

    public static void init(Assets assets) {
        var mgr = assets.mgr;
        for (var type : MusicType.values()) {
            var music = mgr.get(type.path, Music.class);
            if (music == null) {
                Util.log(TAG, Stringf.format("music '%s' not found for type %s", type.path, type));
                continue;
            }
            container.put(type, music);
        }
    }
}
