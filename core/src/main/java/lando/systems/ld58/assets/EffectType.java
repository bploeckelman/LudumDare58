package lando.systems.ld58.assets;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld58.utils.Util;

import java.util.EnumMap;

public enum EffectType implements AssetType<ShaderProgram> {
    //@formatter:off
      BLINDS
    , CIRCLECROP
    , CROSSHATCH
    , CUBE
    , DISSOLVE
    , DOOMDRIP
    , DOORWAY
    , DREAMY
    , HEART
    , PIXELIZE
    , RADIAL
    , RIPPLE
    , SIMPLEZOOM
    , STEREO
    ;
    //@formatter:on

    private static final String TAG = EffectType.class.getSimpleName();
    private static final EnumMap<EffectType, ShaderProgram> container = AssetType.createContainer(EffectType.class);

    public static EffectType random() {
        var index = MathUtils.random(values().length - 1);
        return values()[index];
    }

    @Override
    public ShaderProgram get() {
        return container.get(this);
    }

    public static void init(Assets assets) {
        var prefix = "shaders/transitions/";
        var vertex = prefix + "default.vert";
        for (var type : values()) {
            var filename = type.name().toLowerCase() + ".frag";
            var fragment = prefix + filename;
            var shader = Util.loadShader(vertex, fragment);
            container.put(type, shader);
        }
    }
}
