package lando.systems.ld58.game.components.renderable;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld58.game.components.Position;
import lando.systems.ld58.utils.FramePool;

public abstract class ShaderRenderable {

    public final Rectangle bounds = new Rectangle();
    public final Vector2 offset = new Vector2();

    public ShaderProgram shaderProgram;
    public Texture texture;
    public float accum = 0;

    public Rectangle rect(Position position) {
        return FramePool.rect(
            position.x + bounds.x,
            position.y + bounds.y,
            bounds.width,
            bounds.height);
    }
}
