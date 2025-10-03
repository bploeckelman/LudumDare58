package lando.systems.ld58.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.github.tommyettinger.digital.Stringf;
import lando.systems.ld58.game.components.collision.*;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class Collider implements Component {

    public final CollisionShape shape;
    public final CollisionMask mask;
    public final List<CollisionMask> collidesWith;

    private static final String TAG = Collider.class.getSimpleName();

    public Collider(CollisionShape shape, CollisionMask mask, CollisionMask... collidesWith) {
        this(shape, mask, Arrays.asList(collidesWith));
    }

    public Shape2D shape2d() {
        return shape.shape2d();
    }

    public CollisionShape shape() { return shape; }
    public CollisionMask mask() { return mask; }
    public List<CollisionMask> collidesWith() { return collidesWith; }

    public boolean collidesWith(CollisionMask mask) {
        return collidesWith.contains(mask);
    }

    public <T extends CollisionShape> T shape(Class<T> shapeClass) {
        if (!shapeClass.isInstance(shape)) {
            throw new GdxRuntimeException(Stringf.format("%s: %s is not an instance of %s",
                TAG, shape.getClass().getSimpleName(), shapeClass.getSimpleName()));
        }
        return shapeClass.cast(shape);
    }

    // ------------------------------------------------------------------------
    // Factory methods
    // ------------------------------------------------------------------------

    public static Collider rect(CollisionMask mask, Rectangle rect, CollisionMask... collidesWith) {
        return rect(mask, rect.x, rect.y, rect.width, rect.height, collidesWith);
    }

    public static Collider rect(CollisionMask mask, float x, float y, float width, float height, CollisionMask... collidesWith) {
        if (width <= 0 || height <= 0) throw new GdxRuntimeException("width and height must be positive");
        return new Collider(new CollisionRect(x, y, width, height), mask, collidesWith);
    }

    public static Collider circ(CollisionMask mask, float x, float y, float radius, CollisionMask... collidesWith) {
        if (radius <= 0) throw new GdxRuntimeException("radius must be positive");
        return new Collider(new CollisionCirc(x, y, radius), mask, collidesWith);
    }

    public static Collider grid(CollisionMask mask, int cellSize, int cols, int rows, CollisionMask... collidesWith) {
        if (cellSize <= 0 || cols <= 0 || rows <= 0)
            throw new GdxRuntimeException("cellSize, cols, and rows must be positive");
        return new Collider(new CollisionGrid(cellSize, rows, cols), mask, collidesWith);
    }
}
