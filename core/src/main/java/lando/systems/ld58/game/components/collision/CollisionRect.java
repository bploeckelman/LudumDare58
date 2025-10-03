package lando.systems.ld58.game.components.collision;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.github.tommyettinger.gdcrux.PointI2;
import lando.systems.ld58.game.components.Position;
import lando.systems.ld58.utils.Calc;
import lando.systems.ld58.utils.FramePool;
import lando.systems.ld58.utils.Util;
import lombok.AllArgsConstructor;

/**
 * {@link Rectangle} {@code CollisionShape}
 */
@AllArgsConstructor
public class CollisionRect implements CollisionShape {

    private static final String TAG = CollisionRect.class.getSimpleName();

    public final Rectangle rectangle;

    public CollisionRect(float x, float y, float w, float h) {
        this(new Rectangle(x, y, w, h));
    }

    @Override
    public Shape2D shape2d() {
        return rectangle;
    }

    public void rectangle(Rectangle newRect) {
        rectangle.set(newRect);
    }

    public Rectangle rectangle(Position position) {
        return FramePool.rect(position.x + rectangle.x, position.y + rectangle.y, rectangle.width, rectangle.height);
    }

    public float left(Position position)   { var r = rectangle(position); return r.x; }
    public float right(Position position)  { var r = rectangle(position); return r.x + r.width; }
    public float top(Position position)    { var r = rectangle(position); return r.y + r.height; }
    public float bottom(Position position) { var r = rectangle(position); return r.y; }

    @Override
    public boolean overlaps(CollisionShape that, Position thisPos, Position thatPos, PointI2 offset) {
        var thisRect = rectangle(thisPos);

        // Apply offset so 'thisRect' is in the potential new position before checking for overlap
        thisRect.x += offset.x;
        thisRect.y += offset.y;

        if (that instanceof CollisionCirc) {
            var circ = (CollisionCirc) that;
            return overlapsCirc(thisRect, circ, thatPos);
        } else if (that instanceof CollisionRect) {
            var rect = (CollisionRect) that;
            return overlapsRect(thisRect, rect, thatPos);
        } else if (that instanceof CollisionGrid) {
            var grid = (CollisionGrid) that;
            return overlapsGrid(thisRect, grid, thisPos, thatPos, offset);
        }

        Util.warn(TAG, "unknown collision shape type: " + that.getClass().getSimpleName());
        return false;
    }

    private boolean overlapsCirc(Rectangle thisRect, CollisionCirc circ, Position thatPos) {
        var thatCirc = circ.circle(thatPos);
        // NOTE: arg order is only switched to match Intersector method signature
        return Intersector.overlaps(thatCirc, thisRect);
    }

    private boolean overlapsRect(Rectangle thisRect, CollisionRect rect, Position thatPos) {
        var thatRect = rect.rectangle(thatPos);
        return thisRect.overlaps(thatRect);
    }

    private boolean overlapsGrid(Rectangle thisRect, CollisionGrid grid, Position thisPos, Position gridPos, PointI2 offset) {
        var rows = grid.rows();
        var cols = grid.cols();
        var cellSize = grid.cellSize();

        // Only worth checking against the grid tiles if the rectangle is within the grid bounds
        if (!thisRect.overlaps(grid.bounds(gridPos))) {
            return false;
        }

        // Calc the rectangular extents of the rectangle relative to the grid (instead of relative to the world)
        // this is needed so that we can determine what horiz/vert ranges of tiles could have an overlap
        var rectRelativeX = thisRect.x - gridPos.x;
        var rectRelativeY = thisRect.y - gridPos.y;
        var rectLeft   = rectRelativeX;
        var rectRight  = rectRelativeX + thisRect.width;
        var rectTop    = rectRelativeY + thisRect.height;
        var rectBottom = rectRelativeY;

        // get the range of grid tiles that the rectangle overlaps on each axis
        int left   = Calc.clampInt((int) Calc.floor  (rectLeft   / (float) cellSize), 0, cols);
        int right  = Calc.clampInt((int) Calc.ceiling(rectRight  / (float) cellSize), 0, cols);
        int top    = Calc.clampInt((int) Calc.ceiling(rectTop    / (float) cellSize), 0, rows);
        int bottom = Calc.clampInt((int) Calc.floor  (rectBottom / (float) cellSize), 0, rows);

        // check each tile in the possible overlap range for solidity
        for (int y = bottom; y < top; y++) {
            for (int x = left; x < right; x++) {
                if (grid.get(x, y).solid) {
                    return true;
                }
            }
        }

        return false;
    }
}
