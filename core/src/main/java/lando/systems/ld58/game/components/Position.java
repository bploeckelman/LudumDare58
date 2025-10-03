package lando.systems.ld58.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.github.tommyettinger.crux.Point2;
import com.github.tommyettinger.digital.Stringf;
import com.github.tommyettinger.gdcrux.PointI2;

import java.util.Optional;

import static com.badlogic.gdx.math.MathUtils.round;

public class Position extends PointI2 implements Component {

    private static final String TAG = Position.class.getSimpleName();

    public static final Position ZERO = new Position(0, 0);
    public static final ComponentMapper<Position> mapper = ComponentMapper.getFor(Position.class);

    public static Optional<Position> get(Entity entity) {
        return Optional.ofNullable(mapper.get(entity));
    }

    public Position() {
        super();
    }

    public Position(int x, int y) {
        super(x, y);
    }

    public Position(float x, float y) {
        super(round(x), round(y));
    }

    public Position(GridPoint2 p) {
        super(p);
    }

    public Position(Vector2 p) {
        super(round(p.x), round(p.y));
    }

    public Position(PointI2 p) {
        super(p);
    }

    public Position(Point2<? extends Point2<?>> p) {
        this(p.xi(), p.yi());
    }

    public boolean equals(int x, int y) {
        return this.x == x && this.y == y;
    }

    public boolean equals(Position position) {
        return this == position || (this.x == position.x && this.y == position.y);
    }

    public Position left()  { return cpy().xi(x - 1); }
    public Position right() { return cpy().xi(x + 1); }
    public Position down()  { return cpy().yi(y - 1); }
    public Position up()    { return cpy().yi(y + 1); }

    public int manhattanDist(Position from) {
        var dx = Math.abs(from.xi() - this.xi());
        var dy = Math.abs(from.yi() - this.yi());
        return dx + dy;
    }

    @Override
    public Position cpy() {
        return new Position(this);
    }

    public Position set(Position position) {
        super.set(position.xi(), position.yi());
        return this;
    }

    @Override
    public Position set(PointI2 point) {
        super.set(point);
        return this;
    }

    @Override
    public Position sub(PointI2 point) {
        super.sub(point);
        return this;
    }

    @Override
    public Position add(PointI2 point) {
        super.add(point);
        return this;
    }

    @Override
    public Position scl(PointI2 point) {
        x *= point.x;
        y *= point.y;
        return this;
    }

    @Override
    public Position setZero() {
        set(0, 0);
        return this;
    }

    @Override
    public Position x(float next) {
        x = round(next);
        return this;
    }

    @Override
    public Position xi(int next) {
        x = next;
        return this;
    }

    @Override
    public Position y(float next) {
        y = round(next);
        return this;
    }

    @Override
    public Position yi(int next) {
        y = next;
        return this;
    }

    @Override
    public Position set(float x, float y){
        this.x = round(x);
        this.y = round(y);
        return this;
    }

    @Override
    public Position seti(int x, int y){
        this.x = x;
        this.y = y;
        return this;
    }

    /**
     * Sets the coordinates of this point to that of another.
     *
     * @param point The 2D grid point (which may be a PointI2 or GridPoint2) to copy coordinates of.
     * @return this Pos for chaining.
     */
    @Override
    public Position set(GridPoint2 point) {
        super.set(point);
        return this;
    }

    /**
     * Sets the coordinates of this Pos. Identical to {@link #seti(int, int)}.
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @return this Pos for chaining.
     */
    @Override
    public Position set(int x, int y) {
        return seti(x, y);
    }

    @Override
    public Position set(Point2<?> pt) {
        x = pt.xi();
        y = pt.yi();
        return this;
    }

    /**
     * Adds another point to this point.
     *
     * @param other The other point
     * @return this Pos for chaining.
     */
    @Override
    public Position add(GridPoint2 other) {
        super.add(other);
        return this;
    }

    /**
     * Adds another x,y,z point to this point.
     *
     * @param x The x-coordinate of the other point
     * @param y The y-coordinate of the other point
     * @return this Pos for chaining.
     */
    @Override
    public Position add(int x, int y) {
        super.add(x, y);
        return this;
    }

    @SuppressWarnings("lossy-conversions")
    public Position plus(float value) {
        x += value;
        y += value;
        return this;
    }

    /**
     * Subtracts another point from this point.
     *
     * @param other The other point
     * @return this PointI2 for chaining.
     */
    @Override
    public PointI2 sub(GridPoint2 other) {
        super.sub(other);
        return this;
    }

    /**
     * Subtracts another x,y point from this point.
     *
     * @param x The x-coordinate of the other point
     * @param y The y-coordinate of the other point
     * @return this Pos for chaining.
     */
    @Override
    public PointI2 sub(int x, int y) {
        super.sub(x, y);
        return this;
    }


    @SuppressWarnings("lossy-conversions")
    public Position minus(float value) {
        x -= value;
        y -= value;
        return this;
    }

    @SuppressWarnings("lossy-conversions")
    public Position scl(Point2<?> pt) {
        x *= pt.x();
        y *= pt.y();
        return this;
    }

    @SuppressWarnings("lossy-conversions")
    public Position times(float value) {
        x *= value;
        y *= value;
        return this;
    }

    @SuppressWarnings("lossy-conversions")
    public Position scl(float ox, float oy) {
        x *= ox;
        y *= oy;
        return this;
    }

    public Position mul(Point2<?> pt) {
        return scl(pt);
    }

    public Position mul(float value) {
        return times(value);
    }

    public Position mul(float ox, float oy) {
        return scl(ox, oy);
    }

    /**
     * Gets the component at the specified index.
     * Kotlin-compatible using square-bracket indexing.
     * @param index which component to get, in order
     * @return the component
     */
    public int get(int index) {
        if (index == 1)
            return y;
        return x;
    }

    /**
     * Sets the component at the specified index to the specified value.
     * @param index which component to set, in order
     * @param value the value to assign at index
     * @return this position for chaining
     */
    @SuppressWarnings({"DefaultNotLastCaseInSwitch", "SwitchStatementWithTooFewBranches"})
    public Position setAt(int index, int value){
        switch (index){
            default: x = value;
            case 1 : y = value;
        }
        return this;
    }

    @Override
    public Position lerp(Point2<?> target, float alpha) {
        final float invAlpha = 1.0f - alpha;
        this.x = (int)((x * invAlpha) + (target.x() * alpha));
        this.y = (int)((y * invAlpha) + (target.y() * alpha));
        return this;
    }

    /**
     * Converts this {@code Pos} to a string in the format {@code (x,y)}.
     * <p>
     * <strong>NOTE</strong>: not formatted the same as other {@link Component} {@code toString}
     * implementations in order to fulfill the {@link PointI2} contract for {@link #fromString(String)}.
     * </p>
     * @return a string representation of this object.
     */
    @Override
    public String toString() {
        return Stringf.format("(%d, %d)", x, y);
    }

    /**
     * Sets this {@code Pos} to the value represented by the specified string
     * according to the format of {@link #toString()}.
     * @param s the string.
     * @return this position for chaining
     */
    @Override
    public Position fromString(String s) {
        int s0 = s.indexOf(',', 1);
        if (s0 != -1 && s.charAt(0) == '(' && s.charAt(s.length() - 1) == ')') {
            int x = Integer.parseInt(s.substring(1, s0));
            int y = Integer.parseInt(s.substring(s0 + 1, s.length() - 1));
            return this.set(x, y);
        }
        throw new IllegalArgumentException("Not a valid format for a Pos: " + s);
    }
}
