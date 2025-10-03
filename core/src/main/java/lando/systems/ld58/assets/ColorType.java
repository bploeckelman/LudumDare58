package lando.systems.ld58.assets;

import com.badlogic.gdx.graphics.Color;
import com.github.tommyettinger.gdcrux.PointF4;

import java.util.Arrays;
import java.util.EnumMap;

public enum ColorType implements AssetType<Color> {
    // based on bootstrap colors: https://getbootstrap.com/docs/5.3/customize/color/
      PRIMARY         ( 13f / 255f, 110f / 255f, 253f / 255f)
    , PRIMARY_SUBTLE  (  3f / 255f,  22f / 255f,  51f / 255f)
    , PRIMARY_BORDER  (  8f / 255f,  66f / 255f, 152f / 255f)
    , PRIMARY_TEXT_EM ( 92f / 255f, 166f / 255f, 254f / 255f)

    , SUCCESS         ( 25f / 255f, 135f / 255f,  84f / 255f)
    , SUCCESS_SUBTLE  (  5f / 255f,  27f / 255f,  17f / 255f)
    , SUCCESS_BORDER  ( 15f / 255f,  81f / 255f,  50f / 255f)
    , SUCCESS_TEXT_EM (117f / 255f, 183f / 255f, 152f / 255f)

    , DANGER          (220f / 255f,  53f / 255f,  69f / 255f)
    , DANGER_SUBTLE   ( 44f / 255f,  11f / 255f,  14f / 255f)
    , DANGER_BORDER   (132f / 255f,  32f / 255f,  41f / 255f)
    , DANGER_TEXT_EM  (234f / 255f, 134f / 255f, 143f / 255f)

    , WARNING         (255f / 255f, 193f / 255f,   7f / 255f)
    , WARNING_SUBTLE  ( 51f / 255f,  39f / 255f,   1f / 255f)
    , WARNING_BORDER  (153f / 255f, 116f / 255f,   4f / 255f)
    , WARNING_TEXT_EM (255f / 255f, 218f / 255f, 106f / 255f)

    , INFO            ( 13f / 255f, 202f / 255f, 240f / 255f)
    , INFO_SUBTLE     (  3f / 255f,  40f / 255f,  48f / 255f)
    , INFO_BORDER     (  8f / 255f, 121f / 255f, 144f / 255f)
    , INFO_TEXT_EM    (110f / 255f, 223f / 255f, 246f / 255f)

    , LIGHT           (248f / 255f, 249f / 255f, 250f / 255f)
    , LIGHT_SUBTLE    ( 52f / 255f,  58f / 255f,  64f / 255f)
    , LIGHT_BORDER    ( 73f / 255f,  80f / 255f,  87f / 255f)
    , LIGHT_TEXT_EM   (248f / 255f, 249f / 255f, 247f / 255f)

    , DARK            ( 33f / 255f,  37f / 255f,  41f / 255f)
    , DARK_SUBTLE     ( 26f / 255f,  29f / 255f,  32f / 255f)
    , DARK_BORDER     ( 52f / 255f,  58f / 255f,  64f / 255f)
    , DARK_TEXT_EM    (222f / 255f, 226f / 255f, 227f / 255f)
    ;

    private static final EnumMap<ColorType, Color> container = AssetType.createContainer(ColorType.class);

    public final Color color;
    public final PointF4 rgba;
    public final int rgba8888;

    ColorType(float r, float g, float b) {
        this(r, g, b, 1);
    }

    ColorType(float r, float g, float b, float a) {
        this.color = new Color(r, g, b, a);
        this.rgba = new PointF4(r, g, b, a);
        this.rgba8888 = color.toIntBits();
    }

    @Override
    public Color get() {
        return container.get(this);
    }

    public static void init(Assets assets) {
        Arrays.stream(values()).forEach(value -> container.put(value, value.color));
    }
}
