package lando.systems.ld58.game.components.collision;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.github.tommyettinger.digital.Stringf;
import com.github.tommyettinger.gdcrux.PointI2;
import lando.systems.ld58.game.components.Position;
import lando.systems.ld58.utils.Calc;
import lando.systems.ld58.utils.FramePool;
import lando.systems.ld58.utils.Util;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Grid of {@code solid or empty} {@link Cell}s {@code CollisionShape}
 */
@AllArgsConstructor
public class CollisionGrid implements CollisionShape, Shape2D {

    private static final String TAG = CollisionGrid.class.getSimpleName();

    public final int cellSize;
    public final int rows;
    public final int cols;
    public final Cell[] cells;

    @NoArgsConstructor
    @AllArgsConstructor
    public static class Cell {
        public boolean solid;
    }

    public CollisionGrid(int cellSize, int rows, int cols) {
        this(cellSize, rows, cols, new Cell[rows * cols]);
        for (int i = 0; i < cols * rows; i++) {
            cells[i] = new Cell();
        }
    }

    public int cellSize() { return cellSize; }
    public int rows() { return rows; }
    public int cols() { return cols; }
    public Cell[] cells() { return cells; }

    public void set(int x, int y, boolean solid) {
        if (!Calc.between(x, 0, cols - 1) || !Calc.between(y, 0, rows - 1)) {
            Util.log(TAG, Stringf.format("grid.set(%d, %d, %b) called with out of bounds coords, ignored", x, y, solid));
            return;
        }

        int index = x + y * cols;
        cells[index].solid = solid;
    }

    public Cell get(int x, int y) {
        if (!Calc.between(x, 0, cols - 1) || !Calc.between(y, 0, rows - 1)) {
            throw new GdxRuntimeException(Stringf.format("%s: grid.get(%d, %d) called with out of bounds coords!", TAG, x, y));
        }

        int index = x + y * cols;
        return cells[index];
    }

    /**
     * @return <strong>pooled</strong> {@link Rectangle} representing the overall boundary of the grid collider
     */
    public Rectangle bounds(Position position) {
        return FramePool.rect(position.x, position.y, cols * cellSize, rows * cellSize);
    }

    @Override
    public boolean contains(Vector2 point) {
        return contains(point.x, point.y);
    }

    @Override
    public boolean contains(float x, float y) {
        // TODO: implement me; should it be 'within overall boundary' or 'within solid cell boundary'?
        return false;
    }

    @Override
    public Shape2D shape2d() {
        return this;
    }

    @Override
    public boolean overlaps(CollisionShape that, Position thisPos, Position thatPos, PointI2 offset) {
        throw new GdxRuntimeException("grid->* overlap checks are not supported, such checks should go in the other direction");
    }
}
