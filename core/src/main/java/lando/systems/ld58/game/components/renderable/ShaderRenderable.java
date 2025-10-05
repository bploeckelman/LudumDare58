package lando.systems.ld58.game.components.renderable;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld58.game.components.Position;
import lando.systems.ld58.utils.FramePool;

public abstract class ShaderRenderable {
    public ShaderProgram shaderProgram;
    public float accum = 0;
    public Texture texture;
    public Rectangle bounds = new Rectangle();
    public Vector2 offset = new Vector2();


    public Rectangle rect(Position position) {
        return FramePool.rect(
            position.x + bounds.x,
            position.y + bounds.y,
            bounds.width,
            bounds.height);
    }
}
