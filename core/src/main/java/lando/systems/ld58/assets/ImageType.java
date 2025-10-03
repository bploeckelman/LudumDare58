package lando.systems.ld58.assets;

import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.github.tommyettinger.digital.Stringf;
import lando.systems.ld58.utils.Util;

import java.util.EnumMap;

public enum ImageType implements AssetType<Texture> {
    //@formatter:off
      GDX("libgdx.png")
    ;
    //@formatter:on

    private static final String TAG = ImageType.class.getSimpleName();
    private static final EnumMap<ImageType, Texture> container = AssetType.createContainer(ImageType.class);

    private final String textureName;

    ImageType(String textureName) {
        this.textureName = "images/" + textureName;
    }

    @Override
    public Texture get() {
        return container.get(this);
    }

    public static void load(Assets assets) {
        var texParamsNormal = new TextureLoader.TextureParameter();
        var texParamsRepeat = new TextureLoader.TextureParameter();
        texParamsRepeat.wrapU = Texture.TextureWrap.Repeat;
        texParamsRepeat.wrapV = Texture.TextureWrap.Repeat;
        texParamsRepeat.minFilter = Texture.TextureFilter.MipMap;
        texParamsRepeat.magFilter = Texture.TextureFilter.MipMap;
        texParamsRepeat.genMipMaps = true;

        var mgr = assets.mgr;
        for (var type : ImageType.values()) {
            var isBackground = type.textureName.contains("background");

            var params = isBackground ? texParamsRepeat : texParamsNormal;
            mgr.load(type.textureName, Texture.class, params);

            Util.log(TAG, Stringf.format("texture '%s' loaded for type '%s'", type.textureName, type.name()));
        }
    }

    public static void init(Assets assets) {
        var mgr = assets.mgr;
        for (var type : ImageType.values()) {
            var texture = mgr.get(type.textureName, Texture.class);
            if (texture == null) {
                throw new GdxRuntimeException(Stringf.format("%s: texture '%s' not found for type '%s'", TAG, type.textureName, type.name()));
            }
            container.put(type, texture);
        }
    }
}
