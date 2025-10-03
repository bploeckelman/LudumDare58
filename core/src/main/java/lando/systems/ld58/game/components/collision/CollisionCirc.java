package lando.systems.ld58.game.components.collision;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Shape2D;
import com.github.tommyettinger.gdcrux.PointI2;
import lando.systems.ld58.game.components.Position;
import lando.systems.ld58.utils.Calc;
import lando.systems.ld58.utils.FramePool;
import lando.systems.ld58.utils.Util;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CollisionCirc implements CollisionShape {

    private static final String TAG = CollisionCirc.class.getSimpleName();

    public final Circle circle;

    public CollisionCirc(float x, float y, float radius) {
        this(new Circle(x, y, radius));
    }

    @Override
    public Shape2D shape2d() {
        return circle;
    }

    public Circle circle(Position position) {
        return FramePool.circle(position.x + circle.x, position.y + circle.y, circle.radius);
    }

    @Override
    public boolean overlaps(CollisionShape that, Position thisPos, Position thatPos, PointI2 offset) {
        var thisCirc = circle(thisPos);

        // Apply offset so 'thisCirc' is in the potential new position before checking for overlap
        thisCirc.x += offset.x;
        thisCirc.y += offset.y;

        if (that instanceof CollisionCirc) {
            var circ = (CollisionCirc) that;
            return overlapsCirc(thisCirc, circ, thatPos);
        } else if (that instanceof CollisionRect) {
            var rect = (CollisionRect) that;
            return overlapsRect(thisCirc, rect, thatPos);
        } else if (that instanceof CollisionGrid) {
            var grid = (CollisionGrid) that;
            return overlapsGrid(thisCirc, grid, thisPos, thatPos, offset);
        }

        Util.warn(TAG, "unknown collision shape type: " + that.getClass().getSimpleName());
        return false;
    }

    private boolean overlapsCirc(Circle thisCirc, CollisionCirc circ, Position thatPos) {
        return Intersector.overlaps(thisCirc, circle(thatPos));
    }

    private boolean overlapsRect(Circle thisCirc, CollisionRect rect, Position thatPos) {
        var thatRect = rect.rectangle(thatPos);
        return Intersector.overlaps(thisCirc, thatRect);
    }

    private boolean overlapsGrid(Circle thisCirc, CollisionGrid grid, Position thisPos, Position thatPos, PointI2 offset) {
        var rows = grid.rows();
        var cols = grid.cols();
        var cellSize = grid.cellSize();

        // Only worth checking against the grid tiles if the circle is within the grid bounds
        var gridBounds = grid.bounds(thatPos);
        if (!Intersector.overlaps(thisCirc, gridBounds)) {
            return false;
        }

        // Calc the rectangular extents of the circle relative to the grid (instead of relative to the world)
        // this is needed so that we can determine what horiz/vert ranges of tiles could have an overlap
        var circRelativeX = thisCirc.x - thatPos.x;
        var circRelativeY = thisCirc.y - thatPos.y;
        var circLeft   = circRelativeX - thisCirc.radius;
        var circRight  = circRelativeX + thisCirc.radius;
        var circTop    = circRelativeY + thisCirc.radius;
        var circBottom = circRelativeY - thisCirc.radius;

        // get the range of grid tiles that the circle overlaps on each axis
        int left   = Calc.clampInt((int) Calc.floor(  circLeft   / (float) cellSize), 0, cols);
        int right  = Calc.clampInt((int) Calc.ceiling(circRight  / (float) cellSize), 0, cols);
        int top    = Calc.clampInt((int) Calc.ceiling(circTop    / (float) cellSize), 0, rows);
        int bottom = Calc.clampInt((int) Calc.floor(  circBottom / (float) cellSize), 0, rows);

        // check each tile in the possible overlap range for solidity
        var cellRect = FramePool.rect();
        for (int y = bottom; y < top; y++) {
            for (int x = left; x < right; x++) {
                if (grid.get(x, y).solid) {
                    cellRect.set(
                        gridBounds.x + x * cellSize,
                        gridBounds.y + y * cellSize,
                        cellSize, cellSize);

                    if (Intersector.overlaps(thisCirc, cellRect)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
