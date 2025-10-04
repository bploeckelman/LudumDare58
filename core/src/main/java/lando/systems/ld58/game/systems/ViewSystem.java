package lando.systems.ld58.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld58.game.Components;
import lando.systems.ld58.game.components.*;
import lando.systems.ld58.utils.Calc;
import lando.systems.ld58.utils.Util;
import lombok.RequiredArgsConstructor;

public class ViewSystem extends IteratingSystem {

    private static final String TAG = ViewSystem.class.getSimpleName();
    private static final Vector2 SPEED = new Vector2(500f, 2000f);

    private Entity map;
    private Entity view;
    private Target target;
    private float ratchet; // TODO: have a better way to enable / disable ratchet
    private boolean initialized;

    public boolean zoomFit;
    public boolean stayWithinBounds;

    public ViewSystem() {
        super(Family.one(Viewer.class, Tilemap.class).get());
        this.map = null;
        this.view = null;
        this.target = null;
        this.ratchet = 0;
        this.initialized = false;
        this.zoomFit = true;
        this.stayWithinBounds = true;
    }

    public void target(Entity entity)   { target(new Target.EntityPos(entity)); }
    public void target(Vector2 vector2) { target(new Target.Vec2(vector2)); }
    public void target(Viewer viewer, Interp interp, Bounds bounds) {
        target(new Target.Scroll(viewer, interp, bounds));
    }

    public void target(Target newTarget) {
        target = newTarget;
        initialized = false;
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        var viewer = Components.get(entity, Viewer.class);
        if (viewer != null) {
            viewer.camera().update();
        }
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if (target == null) return;

        var entities = getEntities();
        if (map == null) {
            map = Util.streamOf(entities)
                .filter(entity -> Components.has(entity, Tilemap.class))
                .findFirst()
                .orElse(null);
        }
        if (view == null) {
            view = Util.streamOf(entities)
                .filter(entity -> Components.has(entity, Viewer.class))
                .findFirst()
                .orElse(null);
        }

        if (map == null || view == null) {
            Util.log(TAG, "unable to find map or view entities, skipping update");
            return;
        }

        var bounds = Components.optional(map, Bounds.class).orElseThrow();
        var viewer = Components.optional(view, Viewer.class).orElseThrow();
        var camera = viewer.camera();

        // set initial values for ratchet height and target position
        if (!initialized) {
            initialized = true;
            if (target != null) {
                if (target instanceof Target.Scroll) {
                    ratchet = bounds.rect.y + viewer.height() / 2f;
                }
                camera.position.set(target.x(), target.y(), 0);
                camera.update();
            }
        }

        // zoom to fit the boundary width
        if (zoomFit) {
            camera.zoom = bounds.rect.width / camera.viewportWidth;
        }

        // get half dimensions of the camera viewport, adjusted for the zoom factor
        var camHalfWidth  = viewer.width() / 2f;
        var camHalfHeight = viewer.height() / 2f;

        // follow target
        var x = Calc.approach(camera.position.x, target.x(), delta * SPEED.x);
        var y = Calc.approach(camera.position.y, target.y(), delta * SPEED.y);

        // contain within boundary
        if (stayWithinBounds) {
            var rect = bounds.rect;
            var left = rect.x + camHalfWidth;
            var bottom = rect.y + camHalfHeight;
            var right = rect.x + rect.width - camHalfWidth;
            var top = rect.y + rect.height - camHalfHeight;
            x = Calc.clampf(x, left, right);
            y = Calc.clampf(y, bottom, top);
        }

        // Ratchet up if appropriate, only really relevant for non-auto-scrolling targets
        // but doesn't hurt anything for now so leaving it alone until there's a reason to change
        if (target != null && target instanceof Target.Scroll) {
            if (y < ratchet) {
                y = ratchet;
            } else {
                ratchet = y;
            }
        }

        // Update actual camera position
        camera.position.set(x, y, 0);
    }

    public interface Target {
        float x();
        float y();

        @RequiredArgsConstructor
        class EntityPos implements Target {

            public final Entity entity;
            public final Position position;

            public Entity entity() { return entity; }
            public Position position() { return position; }

            public EntityPos(Entity entity) {
                this(entity, Components.optional(entity, Position.class).orElseThrow());
            }

            public float x() { return position.x; }
            public float y() { return position.y; }
        }

        @RequiredArgsConstructor
        class Vec2 implements Target {

            public final Vector2 vec2;

            public float x() { return vec2.x; }
            public float y() { return vec2.y; }
        }

        @RequiredArgsConstructor
        class Scroll implements Target {
            public final Viewer viewer;
            public final Interp interp;
            public final Bounds bounds;

            public Viewer viewer() { return viewer; }
            public Interp interp() { return interp; }
            public Bounds bounds() { return bounds; }

            public float x() { return 0; }

            public float y() {
                if (viewer == null) {
                    Util.log(TAG, "Viewer component missing from Target.Scroll");
                    return bounds.bottom();
                }

                // Calculate current target y pos for camera, adjusting interp min/max y for viewer's centered origin
                var viewerOffset = viewer.height() / 2f;
                var min = bounds.bottom() + viewerOffset;
                var max = bounds.top()    + viewerOffset;
                return interp.apply(min, max);
            }
        }
    }
}
