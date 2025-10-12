package lando.systems.ld58.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.github.tommyettinger.freetypist.FreeTypistSkin;
import com.github.tommyettinger.textra.Styles;

import java.util.EnumMap;

public enum SkinType implements AssetType<FreeTypistSkin> {
    //@formatter:off
      ZENDO("ui/uiskin.json")
    ;
    //@formatter:on

    private static final String TAG = SkinType.class.getSimpleName();
    private static final EnumMap<SkinType, FreeTypistSkin> container = AssetType.createContainer(SkinType.class);

    public final String skinFilePath;

    SkinType(String skinFilePath) {
        this.skinFilePath = skinFilePath;
    }

    @Override
    public FreeTypistSkin get() {
        return container.get(this);
    }

    public static void init(Assets assets) {
        // Manually instantiate a FreeTypistSkin for each Skins.Type,
        // adding the textratypist font to the skin using its variant name
        // and creating a custom LabelStyle (using the textratypist version) for each
        for (var type : values()) {
            var file = Gdx.files.internal(type.skinFilePath);
            var skin = new FreeTypistSkin(file);
            assets.disposables.add(skin);

            // Add all FontType fonts to the skin for ease of use
            for (var fontType : FontType.values()) {
                var font = fontType.get();
                var fontKey = fontType.name().toLowerCase();
                skin.add(fontKey, font);

                // Create and add a TypingLabel style for this font
                var labelStyleKey = "label-" + fontKey;
                var labelStyle = new Styles.LabelStyle(font, Color.WHITE);
                skin.add(labelStyleKey, labelStyle);

                // Create and add a TextraLabel style for this font
                var textraLabelStyleKey = "textra-label-" + fontKey;
                var textraLabelStyle = new Styles.LabelStyle(font, Color.WHITE);
                skin.add(textraLabelStyleKey, textraLabelStyle);

                // TODO: maybe add styles similarly for other textra widget types:
                //  - TextraArea, TextraButton, TextraCheckBox, ...
                //  - TypingButton, TypingCheckBox, TypingTooltip, ...
            }

            container.put(type, skin);
        }
    }
}
