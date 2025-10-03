package lando.systems.ld58.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Rectangle;
import lando.systems.ld58.utils.Calc;

import java.util.Optional;

/**
 * General-purpose bounds component that can define rectangular areas.
 * Extensible design allows for future non-rectangular shapes.
 */
public class Bounds implements Component {

    private static final String TAG = Bounds.class.getSimpleName();

    public static final ComponentMapper<Bounds> mapper = ComponentMapper.getFor(Bounds.class);

    public static Optional<Bounds> get(Entity entity) {
        return Optional.ofNullable(mapper.get(entity));
    }

    public final Rectangle rect;

    public Bounds(Rectangle rect) {
        this(rect.x, rect.y, rect.width, rect.height);
    }

    public Bounds(float width, float height) {
        this(0, 0, width, height);
    }

    public Bounds(float originX, float originY, float width, float height) {
        rect = new Rectangle(originX, originY, width, height);
    }

    public float left()   { return rect.x; }
    public float bottom() { return rect.y; }
    public float right()  { return rect.x + rect.width; }
    public float top()    { return rect.y + rect.height; }

    public float halfWidth() { return rect.width / 2; }
    public float halfHeight() { return rect.height / 2; }

    public boolean contains(float x, float y) {
        return Calc.between(x, left(), right())
            && Calc.between(y, bottom(), top());
    }

    public boolean contains(Position position) {
        return contains(position.x, position.y);
    }

    public boolean intersects(Bounds other) {
        return !(other.right() < left() || other.left() > right()
              || other.top() < bottom() || other.bottom() > top());
    }

    public boolean overlaps(int x, int y, int w, int h) {
        return contains(x, y)
            || contains(x + w - 1, y)
            || contains(x, y + h - 1)
            || contains(x + w - 1, y + h - 1);
    }

    /**
     * Creates {@link Bounds} component centered on a position
     */
    public static Bounds centered(Position center, int radius) {
        return new Bounds(
            center.x - radius,
            center.y - radius,
            radius * 2 + 1,
            radius * 2 + 1);
    }

    @Override
    public String toString() {
        return "%s{origin=(%.1f, %.1f), size=%.1fx%.1f}".formatted(TAG, rect.x, rect.y, rect.width, rect.height);
    }
}
