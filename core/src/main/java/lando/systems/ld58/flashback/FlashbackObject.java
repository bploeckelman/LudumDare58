package lando.systems.ld58.flashback;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class FlashbackObject {
    public Rectangle bounds = new Rectangle();
    public Animation<TextureRegion> animation;
    public float accum;
    public Color tintColor;

    public FlashbackObject(Animation<TextureRegion> animation, Rectangle bounds) {
        this.animation = animation;
        this.bounds.set(bounds);
        this.tintColor = new Color(Color.WHITE);
    }

    public void update(float dt) {
        accum += dt;
    }

    public void render(SpriteBatch batch) {
        batch.draw(animation.getKeyFrame(accum), bounds.x, bounds.y, bounds.width, bounds.height);
    }
}
