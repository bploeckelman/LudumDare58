package lando.systems.ld58.flashback;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.environment.SpotLight;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld58.Main;
import lando.systems.ld58.utils.FramePool;
import lando.systems.ld58.utils.Util;

public class FlashbackParticle {
    public Color tintColor = new Color();
    public Vector2 position;
    public Vector2 velocity;
    public float ttl;
    public float size;

    public FlashbackParticle() {
        tintColor = Util.hsvToRgb(MathUtils.random(360f), MathUtils.random(.5f, 1f), 1f, tintColor);
        position = new Vector2(25.5f, 2.5f);
        float dir = MathUtils.random(360f);
        float speed = MathUtils.random() * 2f;
        velocity = new Vector2(MathUtils.cosDeg(dir) * speed, MathUtils.sinDeg(dir) * speed);
        var norVel = FramePool.vec2().set(velocity).nor();
        position.x += norVel.x * .1f;
        position.y += norVel.y * .1f;

        size = MathUtils.random(.05f, .1f);
        ttl = .7f;
    }

    public void update(float delta) {
        position.x +=  velocity.x * delta;
        position.y += velocity.y * delta;
        ttl -= delta;
    }

    public void render(SpriteBatch batch ) {
        batch.setColor(tintColor);
        batch.draw(Main.game.assets.pixel,  position.x, position.y, size, size);
    }
}
