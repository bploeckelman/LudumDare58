package lando.systems.ld58.assets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.github.tommyettinger.digital.Stringf;
import com.github.tommyettinger.textra.Font;
import lando.systems.ld58.Main;
import lando.systems.ld58.utils.Util;
import lombok.AllArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

import static com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import static com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader.FreeTypeFontLoaderParameter;

public enum FontType implements AssetType<FontType.Data> {
    //@formatter:off
      ATKINSON_HYPERLEGIBLE_NEXT ("atkinson-hyperlegible-next-regular.ttf")
    , ATKINSON_HYPERLEGIBLE      ("atkinson-hyperlegible-regular.ttf")
    , CHEVYRAY_RISE              ("chevyray-rise.ttf")
    , COUSINE                    ("cousine-regular.ttf")
    , DROID_SANS_MONO            ("droid-sans-mono.ttf")
    , FEASFB                     ("feasfb-regular.ttf", Variant.builder("stats", 40).build())
    , INCONSOLATA                ("inconsolata.otf")
    , NOTO_SANS                  ("noto-sans-cjk-jp-medium.otf")
    , ROBOTO                     ("roboto-regular.ttf")
    , ROUNDABOUT                 ("chevyray-roundabout.ttf")
    , SOURCE_CODE_PRO            ("source-code-pro-regular.otf")
    ;
    //@formatter:on

    private static final String TAG = FontType.class.getSimpleName();
    private static final EnumMap<FontType, Data> container = AssetType.createContainer(FontType.class);

    public final String fontFileName;
    public final List<Variant> variants;

    FontType(String fontFileName) {
        this(fontFileName, Variant.builder("default", 20).build());
    }

    FontType(String fontFileName, Variant... customVariants) {
        this.fontFileName = "fonts/" + fontFileName;

        var allVariants = new ArrayList<>(Arrays.asList(customVariants));

        var customNames = Arrays.stream(customVariants).map(Variant::name).collect(Collectors.toSet());
        if (!customNames.contains("default")) allVariants.add(Variant.builder("default", 20).build());
        if (!customNames.contains("small"))   allVariants.add(Variant.builder("small",   16).build());
        if (!customNames.contains("medium"))  allVariants.add(Variant.builder("medium",  32).build());
        if (!customNames.contains("large"))   allVariants.add(Variant.builder("large",   64).build());

        this.variants = allVariants.stream()
            .sorted(Comparator.comparing(Variant::name))
            .collect(Collectors.toList());
    }

    @Override
    public Data get() {
        return container.get(this);
    }

    public Font font() { return font("default"); }

    public Font font(String variantName) {
        var font = get().font(variantName);
        return Optional.ofNullable(font).orElseGet(() -> {
            Util.log(FontType.TAG, Stringf.format("variant not found: %s - %s, using 'default'", name(), variantName));
            return font();
        });
    }

    public BitmapFont bmpFont(String variantName) {
        var mgr = Main.game.assets.mgr;
        var variant = variants.stream()
            .filter(v -> v.name().equals(variantName))
            .findFirst().orElseGet(() -> variants.get(0));
        return mgr.get(uniqueKey(variant), BitmapFont.class);
    }

    /**
     * Produces a unique string key based on the specified {@link Variant}
     * of the form {@code "fonts/{fontFileName}{#variantName}.[o|t]tf"}
     */
    public String uniqueKey(Variant variant) {
        var index = fontFileName.indexOf(".ttf");
        if (index == -1) {
            index = fontFileName.lastIndexOf(".otf");
        }
        // "fonts/foo{#variantName}.*tf"
        return Stringf.format("%s#%s%s", fontFileName.substring(0, index), variant.name, fontFileName.substring(index));
    }

    /**
     * Produces {@link FreeTypeFontLoaderParameter} for the given font {@link Variant}
     */
    public FreeTypeFontLoaderParameter loaderParams(Variant variant) {
        var params = new FreeTypeFontLoaderParameter();
        params.fontFileName = fontFileName;
        params.fontParameters = variant.fontParameters;
        return params;
    }

    public static void load(Assets assets) {
        var mgr = assets.mgr;
        for (var type : FontType.values()) {
            for (var variant : type.variants) {
                var key = type.uniqueKey(variant);
                var params = type.loaderParams(variant);
                mgr.load(key, BitmapFont.class, params);
            }
        }
    }

    public static void init(Assets assets) {
        var mgr = assets.mgr;
        for (var type : FontType.values()) {
            var data = new Data(type);

            for (var variant : type.variants) {
                var key = type.uniqueKey(variant);
                var bmpFont = mgr.get(key, BitmapFont.class);

                // NOTE: ensure color markup is enabled:
                // "[RED]Hello[] [#00ff00ff]World[]"
                bmpFont.getData().markupEnabled = true;

                var textraFont = new Font(bmpFont);
                assets.disposables.add(textraFont);
                data.put(variant.name, textraFont);
            }

            container.put(type, data);
        }
    }

    /**
     * Container for font variants associated with a particular {@link FontType} enum value / font file
     */
    @AllArgsConstructor
    public static class Data {

        public FontType fontType;
        public Map<String, String> keyByVariantName;
        public Map<String, Font> fontByVariantName;

        public Data(FontType fontType) {
            this(fontType, new HashMap<>(), new HashMap<>());
        }

        public String key(String variantName) {
            return keyByVariantName.get(variantName);
        }

        public Font font(String variantName) {
            return fontByVariantName.get(variantName);
        }

        public Data put(String variantName, Font font) {
            var key = Stringf.format("%s-%s", fontType.name().toLowerCase(), variantName);
            keyByVariantName.put(variantName, key);
            fontByVariantName.put(variantName, font);
            return this;
        }
    }

    /**
     * Encapsulates some parameters from {@link FreeTypeFontParameter}
     * used to define a variant of a given ttf font for a given {@link FontType}.
     * There are more parameters available than those exposed in the constructors here,
     * but most are rarely used, and we can add them if there's a need.
     */
    public static class Variant {

        public final String name;
        public final FreeTypeFontParameter fontParameters;

        public String name() { return name; }
        public FreeTypeFontParameter fontParameters() { return fontParameters; }

        private Variant(String name, FreeTypeFontParameter params) {
            this.name = name;
            this.fontParameters = params;
        }

        // New builder entry point
        public static Builder builder(String name, int size) {
            return new Builder(name, size);
        }

        public static class Builder {
            private final String name;
            private final FreeTypeFontParameter params;

            private Builder(String name, int size) {
                this.name = name;
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

            public Builder color(Color color) {
                params.color = color;
                return this;
            }

            public Builder border(float width, Color color) {
                params.borderWidth = width;
                params.borderColor = color;
                return this;
            }

            public Builder shadow(int x, int y, Color color) {
                params.shadowOffsetX = x;
                params.shadowOffsetY = y;
                params.shadowColor = color;
                return this;
            }

            public Builder mipmaps(boolean enable) {
                params.genMipMaps = enable;
                return this;
            }

            public Builder filters(Texture.TextureFilter min, Texture.TextureFilter mag) {
                params.minFilter = min;
                params.magFilter = mag;
                return this;
            }

            public Variant build() {
                return new Variant(name, params);
            }
        }
    }
}
