package lando.systems.ld58.assets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.github.tommyettinger.digital.Stringf;
import com.github.tommyettinger.textra.Font;
import lando.systems.ld58.utils.FontAssetLoader;

import java.util.EnumMap;

import static com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

public enum FontType implements AssetType<Font> {
      ATKINSON_HYPERLEGIBLE_NEXT ("atkinson-hyperlegible-next-regular.ttf")
    , ATKINSON_HYPERLEGIBLE      ("atkinson-hyperlegible-regular.ttf")
    , CHEVYRAY_RISE              ("chevyray-rise.ttf")
    , COUSINE                    ("cousine-regular.ttf")
    , DROID_SANS_MONO            ("droid-sans-mono.ttf")
    , FEASFB                     ("feasfb-regular.ttf")
    , INCONSOLATA                ("inconsolata.otf")
    , NOTO_SANS                  ("noto-sans-cjk-jp-medium.otf")
    , ROBOTO                     ("roboto-regular.ttf")
    , ROUNDABOUT                 ("chevyray-roundabout.ttf")
    , ROUNDABOUT_LARGE           ("chevyray-roundabout.ttf", 32)
    , SOURCE_CODE_PRO            ("source-code-pro-regular.otf")
    , SOURCE_CODE_PRO_OUTLINED   ("source-code-pro-regular.otf", ParamBuilder.withSize(20).border(2, Color.DARK_GRAY).build())
    ;

    private static final String TAG = FontType.class.getSimpleName();
    private static final EnumMap<FontType, Font> container = AssetType.createContainer(FontType.class);

    public static final int DEFAULT_SIZE = 24;

    public final String fontFilePath;
    public final FreeTypeFontParameter params;

    FontType(String fontFilePath) {
        this(fontFilePath, DEFAULT_SIZE);
    }

    FontType(String fontFilePath, int size) {
        this(fontFilePath, ParamBuilder.withSize(size).build());
    }

    FontType(String fontFilePath, FreeTypeFontParameter params) {
        this.fontFilePath = "fonts/" + fontFilePath;
        this.params = params;
    }

    @Override
    public Font get() {
        return container.get(this);
    }

    /**
     * Produces a unique AssetManager key for this font variant.
     * <ul>
     *   <li>Format: {@code "fonts/{fontFileName}#{enumName}.[o|t]tf"}</li>
     *   <li>Example: {@code ROUNDABOUT_MEDIUM -> "fonts/chevyray-roundabout#ROUNDABOUT_MEDIUM.ttf"}</li>
     * </ul>
     */
    public String uniqueKey() {
        var index = fontFilePath.indexOf(".ttf");
        if (index == -1) {
            index = fontFilePath.lastIndexOf(".otf");
        }
        return Stringf.format("%s#%s%s",
            fontFilePath.substring(0, index),
            name(),  // use enum constant name as unique identifier
            fontFilePath.substring(index));
    }

    /**
     * Get the loader parameters for this font variant
     */
    public FontAssetLoader.Param loaderParams() {
        return new FontAssetLoader.Param(fontFilePath, params);
    }

    public static void load(Assets assets) {
        var mgr = assets.mgr;
        var resolver = mgr.getFileHandleResolver();

        var fontLoader = new FreetypeFontLoader(resolver);
        var fontGenLoader = new FreeTypeFontGeneratorLoader(resolver);
        var textraFontLoader = new FontAssetLoader(resolver, assets.disposables);
        mgr.setLoader(FreeTypeFontGenerator.class, fontGenLoader);

        mgr.setLoader(BitmapFont.class, ".ttf", fontLoader);
        mgr.setLoader(BitmapFont.class, ".otf", fontLoader);
        mgr.setLoader(Font.class, ".ttf", textraFontLoader);
        mgr.setLoader(Font.class, ".otf", textraFontLoader);

        for (var type : FontType.values()) {
            var key = type.uniqueKey();
            var params = type.loaderParams();
            mgr.load(key, Font.class, params);
        }
    }

    public static void init(Assets assets) {
        var mgr = assets.mgr;
        for (var type : FontType.values()) {
            var key = type.uniqueKey();
            var font = mgr.get(key, Font.class);
            container.put(type, font);
        }
    }

    // Helper to create parameter builder

    /**
     * Builder for FreeTypeFontParameter (convenience wrapper)
     */
    private static class ParamBuilder {
        private final FreeTypeFontParameter params;

        private static ParamBuilder withSize(int size) {
            return new ParamBuilder(size);
        }

        private ParamBuilder(int size) {
            this.params = new FreeTypeFontParameter();
            params.size = size;
            // Set sensible defaults
            params.color = Color.WHITE.cpy();
            params.borderWidth = 0;
            params.borderColor = Color.WHITE.cpy();
            params.shadowOffsetX = 0;
            params.shadowOffsetY = 0;
            params.shadowColor = new Color(0, 0, 0, 0.75f);
            params.genMipMaps = false;
            params.minFilter = Texture.TextureFilter.Linear;
            params.magFilter = Texture.TextureFilter.Linear;
        }

        public ParamBuilder color(Color color) {
            params.color = color;
            return this;
        }

        public ParamBuilder border(float width, Color color) {
            params.borderWidth = width;
            params.borderColor = color;
            return this;
        }

        public ParamBuilder shadow(int x, int y, Color color) {
            params.shadowOffsetX = x;
            params.shadowOffsetY = y;
            params.shadowColor = color;
            return this;
        }

        public ParamBuilder mipmaps(boolean enable) {
            params.genMipMaps = enable;
            return this;
        }

        public ParamBuilder filters(Texture.TextureFilter min, Texture.TextureFilter mag) {
            params.minFilter = min;
            params.magFilter = mag;
            return this;
        }

        public FreeTypeFontParameter build() {
            return params;
        }
    }
}
