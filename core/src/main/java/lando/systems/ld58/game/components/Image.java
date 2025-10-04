package lando.systems.ld58.game.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld58.assets.ImageType;
import lando.systems.ld58.utils.Util;
import lombok.AllArgsConstructor;

public class Image extends Renderable {

    private ImageValue value;

    public Image(ImageType type)                     { this(type, type.get(), null); }
    public Image(ImageType type, Vector2 size)       { this(type, type.get(), size); }

    public Image(TextureRegion region)               { this(null, region, null); }
    public Image(TextureRegion region, Vector2 size) { this(null, region, size); }

    public Image(Texture texture)                    { this(null, texture, null); }
    public Image(Texture texture, Vector2 size)      { this(null, texture, size); }

    private Image(ImageType type, TextureRegion region, Vector2 size) {
        set(region);
        if      (size   != null) this.size.set(size);
        else if (region != null) this.size.set(region.getRegionWidth(), region.getRegionHeight());
    }

    private Image(ImageType type, Texture texture, Vector2 size) {
        set(texture);
        if      (size    != null) this.size.set(size);
        else if (texture != null) this.size.set(texture.getWidth(), texture.getHeight());
    }

    public void set(ImageType type)       { set(type.get()); }
    public void set(Texture texture)      { value = new TextureImage(texture); }
    public void set(TextureRegion region) { value = new RegionImage(region); }

    public TextureRegion getTextureRegion() {
        if (value instanceof RegionImage) {
            return ((RegionImage) value).region;
        }
        return null;
    }

    public Texture getTexture() {
        if (value instanceof TextureImage) {
            return ((TextureImage) value).texture;
        }
        return null;
    }

    // ------------------------------------------------------------------------
    // Internal structures to allow for either Texture or TextureRegion values
    // but not both in the same Image component.
    // ------------------------------------------------------------------------

    private interface ImageValue {
        int width();
        int height();
    }

    @AllArgsConstructor
    private static class TextureImage implements ImageValue {
        public final Texture texture;
        public int width() { return texture.getWidth(); }
        public int height() { return texture.getHeight(); }
    }

    @AllArgsConstructor
    private static class RegionImage implements ImageValue {
        public final TextureRegion region;
        public int width() { return region.getRegionWidth(); }
        public int height() { return region.getRegionHeight(); }
    }
}
