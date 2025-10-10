package lando.systems.ld58.assets;

import com.github.tommyettinger.textra.Font;
import lando.systems.ld58.utils.FontAssetLoader;

import java.util.EnumMap;

public enum FontType2 implements AssetType<Font> {
      ATKINSON_HYPERLEGIBLE_NEXT ("fonts/atkinson-hyperlegible-next-regular.ttf")
    , ATKINSON_HYPERLEGIBLE      ("fonts/atkinson-hyperlegible-regular.ttf")
    , CHEVYRAY_RISE              ("fonts/chevyray-rise.ttf")
    , COUSINE                    ("fonts/cousine-regular.ttf")
    , DROID_SANS_MONO            ("fonts/droid-sans-mono.ttf")
    , FEASFB                     ("fonts/feasfb-regular.ttf")
    , INCONSOLATA                ("fonts/inconsolata.otf")
    , NOTO_SANS                  ("fonts/noto-sans-cjk-jp-medium.otf")
    , ROBOTO                     ("fonts/roboto-regular.ttf")
    , ROUNDABOUT                 ("fonts/chevyray-roundabout.ttf")
    , SOURCE_CODE_PRO            ("fonts/source-code-pro-regular.otf")
    ;

    private static final String TAG = FontType2.class.getSimpleName();
    private static final EnumMap<FontType2, Font> container = AssetType.createContainer(FontType2.class);

    public final String fontFilePath;

    // TODO: currently only supports a single size per font file
    public final Integer size;

    FontType2(String fontFilePath) {
        this(fontFilePath, null);
    }

    FontType2(String fontFilePath, Integer size) {
        this.fontFilePath = fontFilePath;
        this.size = size;
    }

    @Override
    public Font get() {
        return container.get(this);
    }

    public static void load(Assets assets) {
        var mgr = assets.mgr;
        for (var type : FontType2.values()) {
            var param = (type.size != null)
                ? new FontAssetLoader.Param(type.size)
                : new FontAssetLoader.Param(); // use default size
            mgr.load(type.fontFilePath, Font.class, param);
        }
    }

    public static void init(Assets assets) {
        var mgr = assets.mgr;
        for (var type : FontType2.values()) {
            // TODO: this needs to support different sizes,
            //  but mgr.get() doesn't take a Param instance
            var font = mgr.get(type.fontFilePath, Font.class);
            container.put(type, font);
        }
    }
}
