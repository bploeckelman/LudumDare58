package lando.systems.ld58.assets;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.github.tommyettinger.digital.Stringf;

import java.util.EnumMap;

public enum IconType implements AssetType<TextureRegion> {
    //@formatter:off
      CARD_STACK   ("card-stack")
    , CIRCLE_CHECK ("circle-check")
    , CIRCLE_X     ("circle-x")
    , HEART        ("heart")
    , HEART_BROKEN ("heart-broken")
    , NOTEPAD      ("notepad")
    , PERSON_PLAY  ("person-play")
    , PERSON_X     ("person-x")
    , PUZZLE       ("puzzle")
    , SKULL        ("skull")
    , MENU         ("menu")
    , WRENCH       ("wrench")
    , X            ("x")
    ;
    //@formatter:on

    private static final String TAG = IconType.class.getSimpleName();
    private static final EnumMap<IconType, TextureRegion> container = AssetType.createContainer(IconType.class);

    private final String regionName;

    IconType(String regionName) {
        this.regionName = "icons/" + regionName;
    }

    @Override
    public TextureRegion get() {
        return container.get(this);
    }

    public static void init(Assets assets) {
        var atlas = assets.atlas;
        for (var value : values()) {
            var region = atlas.findRegion(value.regionName);
            if (region == null) {
                throw new GdxRuntimeException(Stringf.format("%s: atlas region '%s' not found for '%s'", TAG, value.regionName, value.name()));
            }
            container.put(value, region);
        }
    }
}
