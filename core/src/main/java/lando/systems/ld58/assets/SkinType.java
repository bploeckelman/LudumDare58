package lando.systems.ld58.assets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.github.tommyettinger.freetypist.FreeTypistSkinLoader;
import com.github.tommyettinger.textra.Styles;

import java.util.EnumMap;

public enum SkinType implements AssetType<Skin> {
    ZENDO("ui/uiskin.json");

    private static final String TAG = SkinType.class.getSimpleName();
    private static final EnumMap<SkinType, Skin> container = AssetType.createContainer(SkinType.class);

    public final String skinFilePath;

    SkinType(String skinFilePath) {
        this.skinFilePath = skinFilePath;
    }

    @Override
    public Skin get() {
        return container.get(this);
    }

    public static void load(Assets assets) {
        var mgr = assets.mgr;
        var resolver = mgr.getFileHandleResolver();

        mgr.setLoader(Skin.class, new FreeTypistSkinLoader(resolver));

        for (var type : values()) {
            mgr.load(type.skinFilePath, Skin.class);
        }
    }

    public static void init(Assets assets) {
        var mgr = assets.mgr;
        for (var type : values()) {
            var skin = mgr.get(type.skinFilePath, Skin.class);

            // Add all FontType fonts to the skin for ease of use,
            // and create custom LabelStyle for each using the textra style
            for (var fontType : FontType.values()) {
                var font = fontType.get();
                var fontKey = fontType.name().toLowerCase();
                skin.add(fontKey, font);

                // Create and add a TypingLabel style for this font
                var labelStyle = new Styles.LabelStyle(font, Color.WHITE);
                skin.add(fontType.labelStyleName, labelStyle);

                // Create and add a TextraLabel style for this font
                var textraLabelStyle = new Styles.LabelStyle(font, Color.WHITE);
                skin.add(fontType.textraLabelStyleName, textraLabelStyle);

                // TODO: maybe add styles similarly for other textra widget types:
                //  - TextraArea, TextraButton, TextraCheckBox, ...
                //  - TypingButton, TypingCheckBox, TypingTooltip, ...
            }

            container.put(type, skin);
        }
    }
}
