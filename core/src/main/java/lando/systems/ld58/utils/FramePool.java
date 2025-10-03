package lando.systems.ld58.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.github.tommyettinger.gdcrux.PointF2;
import com.github.tommyettinger.gdcrux.PointI2;
import com.github.tommyettinger.gdcrux.PointI3;
import com.github.tommyettinger.gdcrux.PointI4;
import lando.systems.ld58.game.components.Position;

import java.util.HashMap;
import java.util.Map;

public class FramePool {

    private final static FramePool instance = new FramePool();

    public static FramePool get() {
        return instance;
    }

    private final Pool<Vector2> vec2;
    private final Pool<Vector3> vec3;
    private final Pool<PointF2> pf2;
    private final Pool<PointI2> pi2;
    private final Pool<PointI3> pi3;
    private final Pool<PointI4> pi4;
    private final Pool<Circle> circle;
    private final Pool<Position> pos;
    private final Pool<Rectangle> rect;
    private final Pool<Color> color;

    private final Map<Class<?>, Pool<?>> pools;

    private FramePool() {
        this.vec2 = Pools.get(Vector2.class, 500);
        this.vec3 = Pools.get(Vector3.class, 500);
        this.pf2 = Pools.get(PointF2.class, 500);
        this.pi2 = Pools.get(PointI2.class, 500);
        this.pi3 = Pools.get(PointI3.class, 500);
        this.pi4 = Pools.get(PointI4.class, 500);
        this.pos = Pools.get(Position.class, 500);
        this.circle = Pools.get(Circle.class, 500);
        this.rect = Pools.get(Rectangle.class, 500);
        this.color = Pools.get(Color.class, 100);

        this.pools = new HashMap<>();
        this.pools.put(Vector2.class, this.vec2);
        this.pools.put(Vector3.class, this.vec3);
        this.pools.put(PointF2.class, this.pf2);
        this.pools.put(PointI2.class, this.pi2);
        this.pools.put(PointI3.class, this.pi3);
        this.pools.put(PointI4.class, this.pi4);
        this.pools.put(Position.class, this.pos);
        this.pools.put(Circle.class, this.circle);
        this.pools.put(Rectangle.class, this.rect);
        this.pools.put(Color.class, this.color);
    }

    public void clear() {
        pools.values().forEach(Pool::clear);
    }

    // ------------------------------------------------------------------------
    // Vector types
    // ------------------------------------------------------------------------

    public static Vector2 vec2() {
        return instance.vec2.obtain();
    }

    public static Vector2 vec2(float x, float y) {
        return vec2().set(x, y);
    }

    public static Vector3 vec3() {
        return instance.vec3.obtain();
    }

    public static Vector3 vec3(Vector2 xy) {
        return vec3(xy.x, xy.y);
    }

    public static Vector3 vec3(float x, float y) {
        return vec3(x, y, 0);
    }

    public static Vector3 vec3(float x, float y, float z) {
        return vec3().set(x, y, z);
    }

    // ------------------------------------------------------------------------
    // Point types
    // ------------------------------------------------------------------------

    public static PointF2 pf2() {
        return instance.pf2.obtain();
    }

    public static PointF2 pf2(float x, float y) {
        return pf2().set(x, y);
    }

    public static PointI2 pi2() {
        return instance.pi2.obtain();
    }

    public static PointI2 pi2(int x, int y) {
        return pi2().set(x, y);
    }

    public static PointI3 pi3() {
        return instance.pi3.obtain();
    }

    public static PointI3 pi3(int x, int y, int z) {
        return pi3().set(x, y, z);
    }

    public static PointI4 pi4() {
        return instance.pi4.obtain();
    }

    public static PointI4 pi4(int x, int y, int z, int w) {
        return pi4().set(x, y, z, w);
    }

    // ------------------------------------------------------------------------
    // Components
    // ------------------------------------------------------------------------

    public static Position pos() {
        return instance.pos.obtain();
    }

    public static Position pos(int x, int y) {
        return pos().set(x, y);
    }

    // ------------------------------------------------------------------------
    // Circle
    // ------------------------------------------------------------------------

    public static Circle circle() {
        return instance.circle.obtain();
    }

    public static Circle circle(float x, float y, float r) {
        // NOTE: Circle is the only Shape2D that doesn't return 'this' for chaining
        var c = circle();
        c.set(x, y, r);
        return c;
    }

    // ------------------------------------------------------------------------
    // Rect
    // ------------------------------------------------------------------------

    public static Rectangle rect() {
        return instance.rect.obtain();
    }

    public static Rectangle rect(float x, float y, float w, float h) {
        return rect().set(x, y, w, h);
    }

    // ------------------------------------------------------------------------
    // Color
    // ------------------------------------------------------------------------

    public static Color color() {
        return instance.color.obtain();
    }

    public static Color color(float r, float g, float b) {
        return color(r, g, b, 1);
    }

    public static Color color(float r, float g, float b, float a) {
        return color().set(r, g, b, a);
    }
}
