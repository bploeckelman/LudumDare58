package lando.systems.ld58.assets;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.io.Serializable;
import java.util.EnumMap;

public enum AnimType implements AssetType<Animation<TextureRegion>> {
      TITLE_BILLY_MORPH("title/", "title-big-billy-morph", new Data(0.1f, Animation.PlayMode.NORMAL))
    , TITLE_BILLY_WALK("title/", "title-big-billy-old-walk")
    // object animations ----------------------------------------
    , COIN(Path.OBJ_COIN)
    , COIN_BLOCK(Path.FLASHBACK, "coin-block")
    , RELIC_PLUNGER(Path.OBJ_RELIC)
    , RELIC_TORCH(Path.OBJ_RELIC)
    , RELIC_WRENCH(Path.OBJ_RELIC)
    // block animations -----------------------------------------
    , BLOCK_BREAK(Path.BLOCKS)
    , BLOCK_SPIKE_UP(Path.BLOCKS)
    , BLOCK_SPIKE_DOWN(Path.BLOCKS)
    , BLOCK_LAVA_UP(Path.BLOCKS)
    , BLOCK_LAVA_DOWN(Path.BLOCKS)
    // character animations -------------------------------------
    // -- angry sun ---------------------------------------------
    , ANGRY_SUN(Path.CHAR_ANGRY_SUN, new Data(0.2f))
    // -- billy the goomba --------------------------------------
    , BILLY_IDLE(Path.CHAR_BILLY)
    , BILLY_JUMP(Path.CHAR_BILLY)
    , BILLY_WALK(Path.CHAR_BILLY)
    , BILLY_YELL(Path.CHAR_BILLY)
    , BILLY_BULLET_BILL_ACTION(Path.CHAR_BULLET_BILL)
    , BILLY_BULLET_BILL_WALK(Path.CHAR_BULLET_BILL)
    , BILLY_LAKITU_ACTION(Path.CHAR_LAKITU)
    , BILLY_LAKITU_WALK(Path.CHAR_LAKITU)
    , BILLY_KOOPA_ACTION(Path.CHAR_KOOPA)
    , BILLY_KOOPA_WALK(Path.CHAR_KOOPA)
    // -- bullet bill -------------------------------------------
    , BULLET_BILL_ACTION(Path.CHAR_BULLET_BILL)
    , BULLET_BILL_IDLE(Path.CHAR_BULLET_BILL)
    , BULLET_BILL_WALK(Path.CHAR_BULLET_BILL)
    // -- captain-lou -------------------------------------------
    , CAPTAIN_LOU_IDLE(Path.CHAR_CAPTAIN_LOU)
    // -- koopa -------------------------------------------------
    , KOOPA_ACTION(Path.CHAR_KOOPA)
    , KOOPA_WALK(Path.CHAR_KOOPA)
    // -- goomba ------------------------------------------------
    , GOOMBA_NORMAL_IDLE(Path.CHAR_GOOMBA)
    , GOOMBA_NORMAL_WALK(Path.CHAR_GOOMBA)
    , GOOMBA_RAGE_IDLE(Path.CHAR_GOOMBA)
    , GOOMBA_RAGE_WALK(Path.CHAR_GOOMBA)
    , GOOMBA_SAD_IDLE(Path.CHAR_GOOMBA)
    , GOOMBA_SQUISH(Path.CHAR_GOOMBA)
    // -- goomba-cyborg -----------------------------------------
    , GOOMBA_CYBORG_IDLE(Path.CHAR_GOOMBA_CYBORG)
    // -- hammer-bro --------------------------------------------
    , HAMMER_BRO_IDLE(Path.CHAR_HAMMER_BRO)
    // -- koopa -------------------------------------------------
    // -- lakitu ------------------------------------------------
    , LAKITU_ACTION(Path.CHAR_LAKITU)
    , LAKITU_IDLE(Path.CHAR_LAKITU)
    , LAKITU_WALK(Path.CHAR_LAKITU)
    , LAKITU_SPINY_IDLE(Path.CHAR_LAKITU)
    , LAKITU_SPINY_WALK(Path.CHAR_LAKITU)
    // -- mario -------------------------------------------------
    , MARIO_ATTACK(Path.CHAR_MARIO)
    , MARIO_FALL(Path.CHAR_MARIO)
    , MARIO_FIREBALL(Path.CHAR_MARIO)
    , MARIO_HURT(Path.CHAR_MARIO)
    , MARIO_IDLE(Path.CHAR_MARIO)
    , MARIO_JUMP(Path.CHAR_MARIO)
    , MARIO_POWER_ATTACK(Path.CHAR_MARIO)
    , MARIO_WALK(Path.CHAR_MARIO)
    // -- misty -------------------------------------------------
    , MISTY_IDLE(Path.CHAR_MISTY)
    , MISTY_TALK(Path.CHAR_MISTY)

    //--- ENDING 0----------------------------------------------
    , ALBANO_BOT_IDLE(Path.CHAR_ALBANO_BOT, new Data(.5f))
    , ALBANO_BOT_TALK(Path.CHAR_ALBANO_BOT, new Data(.5f))
    , ALBANO_BOT_STILL(Path.CHAR_ALBANO_BOT, new Data(.5f))

    // -- flash back --------------------------------------------
    , YOUNG_BILLY_NORMAL(Path.FLASHBACK, "billy-normal")
    , YOUNG_BILLY_RAGE(Path.FLASHBACK, "billy-rage")
    , DRACULA(Path.FLASHBACK, "dracula")
    , GANNON(Path.FLASHBACK, "gannon")
    , GOOMBA_CAPE(Path.FLASHBACK, "goomba-cloak")
    , HIPPO(Path.FLASHBACK, "hippo")
    , KIDS(Path.FLASHBACK, "kids")
    , LUIGI(Path.FLASHBACK, "luigi")
    , MARIO_EMBRYO(Path.FLASHBACK, "mario-embryo")
    , MARIO_SCREEN(Path.FLASHBACK, "mario-screen", new Data(.5f))
    , MARIO_SPINE(Path.FLASHBACK, "mario-spine")
    , MARIO_TUBE_LARGE(Path.FLASHBACK, "mario-tube-large")
    , MARIO_TUBE_SMALL(Path.FLASHBACK, "mario-tube-small")
    , MISTY(Path.FLASHBACK, "misty")
    , MOTHER_BRAIN(Path.FLASHBACK, "mother-brain")
    , MUSHROOM(Path.FLASHBACK, "mushroom")
    , WILLY(Path.FLASHBACK, "willy")
    ;

    private static class Path {
        private static final String CHARACTERS = "characters/";
        private static final String CHAR_ANGRY_SUN     = CHARACTERS + "angry-sun/";
        private static final String CHAR_BILLY         = CHARACTERS + "billy/";
        private static final String CHAR_BULLET_BILL   = CHARACTERS + "bullet-bill/";
        private static final String CHAR_CAPTAIN_LOU   = CHARACTERS + "captain-lou/";
        private static final String CHAR_ALBANO_BOT    = CHARACTERS + "albano-bot/";
        private static final String CHAR_GOOMBA        = CHARACTERS + "goomba/";
        private static final String CHAR_GOOMBA_CYBORG = CHARACTERS + "goomba-cyborg/";
        private static final String CHAR_HAMMER_BRO    = CHARACTERS + "hammer-bro/";
        private static final String CHAR_KOOPA         = CHARACTERS + "koopa/";
        private static final String CHAR_LAKITU        = CHARACTERS + "lakitu/";
        private static final String CHAR_MARIO         = CHARACTERS + "mario/";
        private static final String CHAR_MISTY         = CHARACTERS + "misty/";

        private static final String OBJECTS = "objects/";
        private static final String OBJ_COIN = OBJECTS + "coin/";
        private static final String OBJ_RELIC = OBJECTS + "relics/";

        private static final String BLOCKS = "blocks/";

        private static final String FLASHBACK = "flashback/";
    }

    private static final String TAG = FontType.class.getSimpleName();
    private static final EnumMap<AnimType, Animation<TextureRegion>> container = AssetType.createContainer(AnimType.class);

    private final String path;
    private final String name;
    private final Data data;

    AnimType(String path)              { this(path, null, null); }
    AnimType(String path, Data data)   { this(path, null, data); }
    AnimType(String path, String name) { this(path, name, null); }
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
