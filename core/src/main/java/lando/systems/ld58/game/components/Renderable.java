package lando.systems.ld58.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld58.utils.FramePool;

public abstract class Renderable implements Component {

    public final Color tint = Color.WHITE.cpy();

    public final Vector2 origin = new Vector2();
    public final Vector2 size   = new Vector2();

    public final Vector2 defaultScale     = new Vector2(1, 1);
    public final Vector2 scale            = defaultScale.cpy();
    public final float   scaleReturnSpeed = 4f;

    public Rectangle rect(Position position) {
        return FramePool.rect(
            position.x - origin.x * scale.x,
            position.y - origin.y * scale.y,
            size.x * scale.x,
            size.y * scale.y);
    }
}
