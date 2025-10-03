package lando.systems.ld58.assets;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.io.Serializable;
import java.util.EnumMap;

public enum AnimType implements AssetType<Animation<TextureRegion>> {
    // object animations ----------------------------------------
    SNOWBALL(Path.OBJECTS),
    // hero animations ------------------------------------------
    HERO_ATTACK_EFFECT(Path.HERO),
    HERO_ATTACK(Path.HERO),
    HERO_DEATH(Path.HERO),
    HERO_FALL(Path.HERO),
    HERO_IDLE(Path.HERO),
    HERO_JUMP(Path.HERO),
    HERO_LAND_EFFECT(Path.HERO),
    HERO_RUN(Path.HERO),
    // ----------------------------------------------------------
    ;

    private static class Path {
        private static final String HERO = "character/hero/";
        private static final String OBJECTS = "objects/";
    }

    private static final String TAG = FontType.class.getSimpleName();
    private static final EnumMap<AnimType, Animation<TextureRegion>> container = AssetType.createContainer(AnimType.class);

    private final String path;
    private final String name;
    private final Data data;

    AnimType(String path) {
        this(path, null, null);
    }

    AnimType(String path, Data data) {
        this(path, null, data);
    }

    AnimType(String path, String name) {
        this(path, name, null);
    }

    AnimType(String path, String name, Data data) {
        this.path = path;
        this.name = (name != null) ? name : name().toLowerCase().replace("_", "-");
        this.data = (data != null) ? data : new Data();
    }

    @Override
    public Animation<TextureRegion> get() {
        return container.get(this);
    }

    public static void init(Assets assets) {
        var atlas = assets.atlas;
        for (var type : AnimType.values()) {
            var data = type.data;
            var regions = atlas.findRegions(type.path + type.name);
            var animation = new Animation<TextureRegion>(data.frameDuration, regions, data.playMode);
            container.put(type, animation);
        }
    }

    public static class Data implements Serializable {

        private static final float DEFAULT_FRAME_DURATION = 0.1f;
        private static final Animation.PlayMode DEFAULT_PLAY_MODE = Animation.PlayMode.LOOP;

        public final float frameDuration;
        public final Animation.PlayMode playMode;

        public Data() {
            this(DEFAULT_FRAME_DURATION, DEFAULT_PLAY_MODE);
        }

        public Data(float frameDuration) {
            this(frameDuration, DEFAULT_PLAY_MODE);
        }

        public Data(float frameDuration, Animation.PlayMode playMode) {
            this.frameDuration = (frameDuration > 0) ? frameDuration : DEFAULT_FRAME_DURATION;
            this.playMode = (playMode != null) ? playMode : DEFAULT_PLAY_MODE;
        }
    }
}
