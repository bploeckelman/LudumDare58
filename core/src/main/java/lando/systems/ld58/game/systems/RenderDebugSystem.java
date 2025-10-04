package lando.systems.ld58.game.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld58.Flag;
import lando.systems.ld58.assets.ColorType;
import lando.systems.ld58.game.Components;
import lando.systems.ld58.game.Scene;
import lando.systems.ld58.game.components.*;
import lando.systems.ld58.game.components.collision.CollisionCirc;
import lando.systems.ld58.game.components.collision.CollisionGrid;
import lando.systems.ld58.game.components.collision.CollisionRect;
import lando.systems.ld58.utils.FramePool;
import lando.systems.ld58.utils.Util;
import space.earlygrey.shapedrawer.JoinType;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class RenderDebugSystem extends EntitySystem {

    private static final Vector2 GRAVITY_DIR = new Vector2(0, -1);
    private static final Family SCENE = Family.one(SceneContainer.class).get();

    private ImmutableArray<Entity> entities;
    private Scene<?> scene;

    public boolean drawEntityPositions = true;
    public boolean drawEntityAnimators = true;
    public boolean drawEntityColliders = true;
    public boolean drawEntityGravities = false; // NOTE: currently gravity is constant for just the player, doesn't really pay to draw it
    public boolean drawEntityVelocities = true;

    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntities();
    }

    public void draw(ShapeDrawer shapes) {
        if (Flag.DEBUG_RENDER.isDisabled()) return;

        scene = Util.streamOf(getEngine().getEntitiesFor(SCENE))
            .map(SceneContainer::get)
            .map(SceneContainer::scene)
            .findFirst().orElse(null);
        if (scene == null) return;

        drawEntityPositions(shapes);
        drawEntityAnimators(shapes);
        drawEntityColliders(shapes);
        drawEntityGravities(shapes);
        drawEntityVelocities(shapes);
    }

    private void drawEntityPositions(ShapeDrawer shapes) {
        if (!drawEntityPositions) return;

        var triangleOffset = FramePool.vec2(4f, 8f);

        var prevColor = shapes.getPackedColor();
        for (var entity : entities) {
            var pos = Components.get(entity, Position.class);
            if (pos == null) continue;

            shapes.filledTriangle(
                pos.x, pos.y,
                pos.x - triangleOffset.x, pos.y - triangleOffset.y,
                pos.x + triangleOffset.x, pos.y - triangleOffset.y,
                ColorType.PRIMARY.get());

            shapes.triangle(
                pos.x, pos.y,
                pos.x - triangleOffset.x, pos.y - triangleOffset.y,
                pos.x + triangleOffset.x, pos.y - triangleOffset.y,
                1f, JoinType.SMOOTH, ColorType.PRIMARY_BORDER.get().toFloatBits());
        }
        shapes.setColor(prevColor);
    }

    private void drawEntityAnimators(ShapeDrawer shapes) {
        if (!drawEntityAnimators) return;

        var prevColor = shapes.getPackedColor();
        for (var entity : entities) {
            var pos  = Components.get(entity, Position.class);
            var anim = Components.get(entity, Animator.class);
            if (pos  == null) continue;
            if (anim == null) continue;

            var rect = anim.rect(pos);
            shapes.rectangle(rect, ColorType.INFO.get(), 1f);
        }
        shapes.setColor(prevColor);
    }

    private void drawEntityColliders(ShapeDrawer shapes) {
        if (!drawEntityColliders) return;

        var joinType = JoinType.SMOOTH;
        var gridCell = FramePool.rect();

        var prevColor = shapes.getPackedColor();
        for (var entity : entities) {
            var pos = Components.get(entity, Position.class);
            var col = Components.get(entity, Collider.class);
            if (pos == null) continue;
            if (col == null) continue;

            if (col.shape() instanceof CollisionCirc) {
                var circ = col.shape(CollisionCirc.class);
                var circle = circ.circle(pos);
                shapes.setColor(Color.MAGENTA);
                shapes.circle(circle.x, circle.y, circle.radius, 1f, joinType);
            }
            else if (col.shape() instanceof CollisionRect) {
                var rect = col.shape(CollisionRect.class);
                var rectangle = rect.rectangle(pos);
                shapes.rectangle(rectangle, Color.MAGENTA, 2f);
            }
            else if (col.shape() instanceof CollisionGrid) {
                var grid = col.shape(CollisionGrid.class);
                shapes.setColor(ColorType.DANGER.get());
                for (int y = 0; y < grid.rows(); y++) {
                    for (int x = 0; x < grid.cols(); x++) {
                        if (grid.get(x, y).solid) {
                            gridCell.set(
                                pos.x + x * grid.cellSize(),
                                pos.y + y * grid.cellSize(),
                                grid.cellSize(), grid.cellSize());
                            shapes.rectangle(gridCell, ColorType.DANGER.get(), 2f);
                        }
                    }
                }
            }
        }
        shapes.setColor(prevColor);
    }

    private void drawEntityGravities(ShapeDrawer shapes) {
        if (!drawEntityGravities) return;

        var gravityLength = 10f;
        var endpoint = FramePool.vec2();
        var triangleOffset = FramePool.vec2(4f, 8f);

        var prevColor = shapes.getPackedColor();
        for (var entity : entities) {
            var grav = Components.get(entity, Gravity.class);
            if (grav == null) continue;

            var pos = Components.get(entity, Position.class);
            if (pos == null) continue;

            endpoint.set(
                pos.x + gravityLength * GRAVITY_DIR.x,
                pos.y + gravityLength * GRAVITY_DIR.y);

            shapes.line(pos.x, pos.y, endpoint.x, endpoint.y);

            shapes.filledTriangle(
                endpoint.x, endpoint.y,
                endpoint.x - triangleOffset.x, endpoint.y - triangleOffset.y,
                endpoint.x + triangleOffset.x, endpoint.y - triangleOffset.y,
                ColorType.DARK.get());

            shapes.triangle(
                endpoint.x, endpoint.y,
                endpoint.x - triangleOffset.x, endpoint.y - triangleOffset.y,
                endpoint.x + triangleOffset.x, endpoint.y - triangleOffset.y,
                1f, JoinType.SMOOTH, ColorType.DARK_BORDER.get().toFloatBits());
        }
        shapes.setColor(prevColor);
    }

    private void drawEntityVelocities(ShapeDrawer shapes) {
        if (!drawEntityVelocities) return;

        var start = FramePool.vec2();
        var endpoint = FramePool.vec2();
        var lineWidth = 1f;
        var circleRadius = 2f;
        var maxLength = 32f;

        var prevColor = shapes.getPackedColor();
        for (var entity : entities) {
            var pos = Components.get(entity, Position.class);
            var vel = Components.get(entity, Velocity.class);
            if (pos == null) continue;
            if (vel == null) continue;

            var colliderOffset = FramePool.vec2();
            var col = Components.get(entity, Collider.class);
            if (col != null) {
                if (col.shape instanceof CollisionCirc) {
                    var circ = col.shape(CollisionCirc.class);
                    colliderOffset.set(circ.circle.x, circ.circle.y);
                }
                else if (col.shape instanceof CollisionRect) {
                    var rect = col.shape(CollisionRect.class);
                    rect.rectangle.getCenter(colliderOffset);
                }
            }

            start.set(
                pos.x + colliderOffset.x,
                pos.y + colliderOffset.y);

            var unitVel = FramePool.vec2().set(vel.value).nor();
            endpoint.set(
                pos.x + colliderOffset.x + maxLength * unitVel.x,
                pos.y + colliderOffset.y + maxLength * unitVel.y);

            shapes.line(start.x, start.y, endpoint.x, endpoint.y, ColorType.SUCCESS.get(), lineWidth);

            shapes.filledCircle(endpoint.x, endpoint.y, circleRadius, ColorType.LIGHT.get());
            shapes.filledCircle(endpoint.x, endpoint.y, circleRadius - lineWidth, ColorType.DARK_BORDER.get());
        }
        shapes.setColor(prevColor);
    }
}
