package lando.systems.ld58.game.components.renderable;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld58.game.Components;
import lando.systems.ld58.game.components.Position;
import lando.systems.ld58.game.components.TileLayer;
import lando.systems.ld58.utils.FramePool;

public abstract class Renderable {

    public final Color tint = Color.WHITE.cpy();

    public float depth = 0;

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

    public static Renderable getRenderable(Entity entity) {
        if (Components.has(entity, Image.class)) return Components.get(entity, Image.class);
        if (Components.has(entity, Animator.class)) return Components.get(entity, Animator.class);
        if (Components.has(entity, TileLayer.class)) return Components.get(entity, TileLayer.class);
        return null;
    }
}
