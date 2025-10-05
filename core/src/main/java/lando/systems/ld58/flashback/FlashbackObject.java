package lando.systems.ld58.flashback;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class FlashbackObject {
    public Rectangle bounds;
    public Animation<TextureRegion> animation;
    public float accum;


    public void update(float dt) {
        accum += dt;
    }

    public void render(SpriteBatch batch) {
        batch.draw(animation.getKeyFrame(accum), bounds.x, bounds.y, bounds.width, bounds.height);
    }
}
