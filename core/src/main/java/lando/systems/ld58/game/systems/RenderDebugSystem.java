package lando.systems.ld58.game.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld58.game.components.SceneContainer;
import lando.systems.ld58.utils.Util;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.List;

public class RenderDebugSystem extends EntitySystem {

    private static final Vector2 GRAVITY_DIR = new Vector2(0, -1);
    private static final Family SCENE = Family.one(SceneContainer.class).get();
//    private static final Family PLATFORMS = Family.one(Platforms.class).get();

    private List<Entity> players;
    private ImmutableArray<Entity> entities;

    public boolean enabled = false;

//    public boolean drawPlatforms = true;
//    public boolean drawNavGrid = true;
//    public boolean drawNavGraph = true;
//    public boolean drawNavPath = true;
//    public boolean drawNavSystem = true;
//
//    public boolean drawEntityPositions = true;
//    public boolean drawEntityAnimators = true;
//    public boolean drawEntityColliders = true;
//    public boolean drawEntityGravities = true;
//    public boolean drawEntityVelocities = true;
//    public boolean drawEntityNavigation = true;

    public RenderDebugSystem() {
        this.players = List.of();
        this.entities = null;
    }

    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntities();
    }

    public void toggle() {
        enabled = !enabled;
    }

    public void draw(ShapeDrawer shapes) {
        if (!enabled) return;

        var scene = Util.streamOf(getEngine().getEntitiesFor(SCENE))
            .map(SceneContainer::get)
            .map(SceneContainer::scene)
            .findFirst().orElse(null);
        if (scene == null) return;

//        players = scene.playerEntities();
//        var navGrid = scene.navGrid();

//        drawNavGrid(shapes, navGrid);
//        drawPlatforms(shapes);
//        if (scene.navGraph() != null) {
//            drawNavGraph(shapes, scene.navGraph(), navGrid);
//        }
//        if (scene.screen() instanceof NavTestScreen screen) {
//            // TODO: get current path from NavigationSystem
//            drawNavPath(shapes, navGrid, currentPath);
//        }
//        drawEntityPositions(shapes);
//        drawEntityAnimators(shapes);
//        drawEntityColliders(shapes);
//        drawEntityGravities(shapes);
//        drawEntityVelocities(shapes);
//        drawEntityNavigation(shapes, scene);
//        if (drawNavSystem) {
//            getEngine().getSystem(NavigationSystem.class).renderDebug(shapes);
//        }
    }

//    private void drawPlatforms(ShapeDrawer shapes) {
//        if (!drawPlatforms) return;
//
//        var sceneEntity = getEngine().getEntitiesFor(SCENE).first();
//        var platformEntities = getEngine().getEntitiesFor(PLATFORMS);
//        var hLineWidth = 8f;
//        var vLineWidth = 4f;
//
//        var scene = Components.get(sceneEntity, SceneContainer.class).scene();
//        var currentPlatforms = Util.streamOf(players)
//            .map(player -> WorldQuery.currentPlatform(scene, player))
//            .flatMap(Optional::stream)
//            .toList();
//
//        var prevColor = shapes.getPackedColor();
//        for (var entity : platformEntities) {
//            var platforms = Components.get(entity, Platforms.class);
//            var mapPos    = Components.get(platforms.map(), Position.class);
//            var collider  = Components.get(platforms.map(), Collider.class);
//            var cellSize  = collider.shape(CollisionGrid.class).cellSize();
//
//            for (var platform : platforms.platforms()) {
//                var start = platform.worldStart();
//                var end   = platform.worldEnd();
//
//                // Draw platform as a thick horizontal line
//                shapes.setColor(ColorType.PLATFORM.get());
//                shapes.line(start.x, start.y, end.x, end.y, hLineWidth);
//                // Draw an extra thin line for visibility
//                shapes.setColor(ColorType.DARK_BORDER.get());
//                shapes.line(start.x, start.y, end.x, end.y, 1.5f);
//
//                // Draw small vertical markers at the ends
//                shapes.setColor(ColorType.PLATFORM.get());
//                shapes.line(start.x, start.y - 10, start.x, start.y + 10, vLineWidth);
//                shapes.line(end.x,   end.y   - 10, end.x,   end.y   + 10, vLineWidth);
//
//                // Draw highlight overlay for the tiles that make up the platform,
//                // with color based on whether or not a player is actively standing on it.
//                var gridStart = platform.gridFloorStart();
//                var gridWidth = platform.gridWidth();
//                var colorType = currentPlatforms.contains(platform) ? ColorType.PLATFORM_CURRENT : ColorType.PLATFORM_TILES;
//                shapes.filledRectangle(FramePool.rect(
//                    mapPos.x + cellSize * gridStart.x,
//                    mapPos.y + cellSize * gridStart.y,
//                    cellSize * gridWidth,
//                    cellSize
//                ), colorType.get());
//            }
//        }
//        shapes.setColor(prevColor);
//    }
//
//    private void drawNavGrid(ShapeDrawer shapes, NavGrid navGrid) {
//        if (!drawNavGrid) return;
//        if (navGrid == null) return;
//
//        var prevColor = shapes.getPackedColor();
//
//        var pos  = Components.get(navGrid.mapEntity(), Position.class);
//        int size = navGrid.cellSize();
//
//        // Draw the navGrid cells, colored according to their type
//        for (int y = 0; y < navGrid.rows(); y++) {
//            for (int x = 0; x < navGrid.cols(); x++) {
//                var type = navGrid.get(x, y);
//                var rect = FramePool.rect(pos.x + size * x, pos.y + size * y, size, size);
//
//                // TODO: consider adding text instead of just color
//
//                // Draw a filled colored rect for the cell
//                shapes.filledRectangle(rect, type.debugColor().get());
//
//                // Draw extra outline for visibility of open cells
//                if (type == NavGrid.CellType.OPEN) {
//                    shapes.rectangle(rect, Color.GRAY, 1.5f);
//                }
//            }
//        }
//
//        shapes.setColor(prevColor);
//    }
//
//    // TODO: refactor to use updated NavGraph type
//    private void drawNavGraph(ShapeDrawer shapes, NavGraph navGraph, NavGrid navGrid) {
//        if (!drawNavGraph) return;
//        if (navGraph == null) return;
//        if (navGrid  == null) return;
//
////        var pos = Components.get(navGrid.mapEntity(), Position.class);
////        int size = navGrid.cellSize();
////
////        var prevColor = shapes.getPackedColor();
////        for (var entry : navGraph.adjacency().entrySet()) {
////            var coord = entry.getKey();
////            var edges = entry.getValue();
////
////            //var rect = FramePool.rect(pos.x + size * coord.x, pos.y + size * coord.y, size, size);
////            //shapes.filledRectangle(rect, ColorType.NAV_GRAPH_ADJ_SRC_COORD.get());
////
////            // Draw lines between graph edges
////            for (var edge : edges) {
////                var color = switch (edge.type()) {
////                    case DROP -> Color.MAROON;
////                    case JUMP -> Color.FOREST;
////                    case WALK -> Color.ROYAL;
////                };
////                var lineWidth = switch (edge.type()) {
////                    case DROP -> 6f;
////                    case JUMP -> 1.5f;
////                    case WALK -> 8f;
////                };
////
////                var a = FramePool.pi2();
////                var b = FramePool.pi2();
////                switch (edge.meta()) {
////                    case NavEdgeMeta.Walk walk -> {
////                        a.set(walk.fromX(), walk.y());
////                        b.set(walk.toX(), walk.y());
////                    }
////                    case NavEdgeMeta.Drop drop -> {
////                        a.set(drop.fromX(), drop.fromY());
////                        b.set(drop.landX(), drop.landY());
////                    }
////                    case NavEdgeMeta.Jump jump -> {
////                        a.set(jump.takeoffX(), jump.takeoffY());
////                        b.set(jump.landingX(), jump.landingY());
////                    }
////                }
////
////                shapes.line(
////                    pos.x + size * a.x + size / 2f,
////                    pos.y + size * a.y + size / 2f,
////                    pos.x + size * b.x + size / 2f,
////                    pos.y + size * b.y + size / 2f,
////                    color, lineWidth
////                );
////            }
////
////            // Draw graph nodes for each edge
////            var nodes = new HashSet<Circle>();
////            for (var edge : edges) {
////                var src = edge.src();
////                var dst = edge.dst();
////
////                var srcNode = FramePool.circle(
////                    pos.x + size * src.x() + size / 2f,
////                    pos.y + size * src.y() + size / 2f,
////                    size / 4f
////                );
////                var dstNode = FramePool.circle(
////                    pos.x + size * dst.x() + size / 2f,
////                    pos.y + size * dst.y() + size / 2f,
////                    size / 4f
////                );
////
////                nodes.add(srcNode);
////                nodes.add(dstNode);
////            }
////            for (var circle : nodes) {
////                shapes.filledCircle(circle.x, circle.y, circle.radius,      Color.DARK_GRAY);
////                shapes.filledCircle(circle.x, circle.y, circle.radius - 4f, Color.LIGHT_GRAY);
////            }
////        }
////
////        shapes.setColor(prevColor);
//    }
//
//    // TODO: refactor to use updated NavPath type (or delegate to NavigationSystem debug rendering)
//    private void drawNavPath(ShapeDrawer shapes, NavGrid navGrid, NavPath path) {
//        if (!drawNavPath) return;
//
////        var path = navCtrl.currentPath();
////        if (path == null) return;
////
////        var pos = Components.get(navGrid.mapEntity(), Position.class);
////        var color = ColorType.WARNING.get();
////        int size = navGrid.cellSize();
////        var lineWidth = 10f;
////
////        var prevColor = shapes.getPackedColor();
////
////        // Draw lines for each path edge
////        for (var edge : path.edges()) {
////            var a = FramePool.pi2();
////            var b = FramePool.pi2();
////            switch (edge.meta()) {
////                case NavEdgeMeta.Walk walk -> {
////                    a.set(walk.fromX(), walk.y());
////                    b.set(walk.toX(), walk.y());
////                }
////                case NavEdgeMeta.Drop drop -> {
////                    a.set(drop.fromX(), drop.fromY());
////                    b.set(drop.landX(), drop.landY());
////                }
////                case NavEdgeMeta.Jump jump -> {
////                    a.set(jump.takeoffX(), jump.takeoffY());
////                    b.set(jump.landingX(), jump.landingY());
////                }
////            }
////
////            shapes.line(
////                pos.x + size * a.x + size / 2f,
////                pos.y + size * a.y + size / 2f,
////                pos.x + size * b.x + size / 2f,
////                pos.y + size * b.y + size / 2f,
////                color, lineWidth
////            );
////        }
////
////        // Draw big circles for waypoints in the path
////        var nodes = new HashSet<Circle>();
////        for (var edge : path.edges()) {
////            var src = edge.src();
////            var dst = edge.dst();
////
////            var srcNode = FramePool.circle(
////                pos.x + size * src.x() + size / 2f,
////                pos.y + size * src.y() + size / 2f,
////                size / 3f
////            );
////            var dstNode = FramePool.circle(
////                pos.x + size * dst.x() + size / 2f,
////                pos.y + size * dst.y() + size / 2f,
////                size / 3f
////            );
////
////            nodes.add(srcNode);
////            nodes.add(dstNode);
////        }
////        for (var circle : nodes) {
////            shapes.filledCircle(circle.x, circle.y, circle.radius,    Color.FOREST);
////            shapes.filledCircle(circle.x, circle.y, circle.radius-4f, Color.LIME);
////        }
////
////        shapes.setColor(prevColor);
//    }
//
//    private void drawEntityPositions(ShapeDrawer shapes) {
//        if (!drawEntityPositions) return;
//
//        var triangleOffset = FramePool.vec2(8f, 16f);
//
//        var prevColor = shapes.getPackedColor();
//        for (var entity : entities) {
//            var pos = Components.get(entity, Position.class);
//            if (pos == null) continue;
//
//            shapes.filledTriangle(
//                pos.x, pos.y,
//                pos.x - triangleOffset.x, pos.y - triangleOffset.y,
//                pos.x + triangleOffset.x, pos.y - triangleOffset.y,
//                ColorType.PRIMARY.get());
//
//            shapes.triangle(
//                pos.x, pos.y,
//                pos.x - triangleOffset.x, pos.y - triangleOffset.y,
//                pos.x + triangleOffset.x, pos.y - triangleOffset.y,
//                3f, JoinType.SMOOTH, ColorType.PRIMARY_BORDER.get().toFloatBits());
//        }
//        shapes.setColor(prevColor);
//    }
//
//    private void drawEntityAnimators(ShapeDrawer shapes) {
//        if (!drawEntityAnimators) return;
//
//        var prevColor = shapes.getPackedColor();
//        for (var entity : entities) {
//            var pos  = Components.get(entity, Position.class);
//            var anim = Components.get(entity, Animator.class);
//            if (pos  == null) continue;
//            if (anim == null) continue;
//
//            var rect = anim.rect(pos);
//            shapes.rectangle(rect, ColorType.INFO.get(), 2f);
//        }
//        shapes.setColor(prevColor);
//    }
//
//    private void drawEntityColliders(ShapeDrawer shapes) {
//        if (!drawEntityColliders) return;
//
//        var joinType = JoinType.SMOOTH;
//        var gridCell = FramePool.rect();
//
//        var prevColor = shapes.getPackedColor();
//        for (var entity : entities) {
//            var pos = Components.get(entity, Position.class);
//            var col = Components.get(entity, Collider.class);
//            if (pos == null) continue;
//            if (col == null) continue;
//
//            switch (col.shape()) {
//                case CollisionCirc circ -> {
//                    var circle = circ.circle(pos);
//                    shapes.setColor(Color.MAGENTA);
//                    shapes.circle(circle.x, circle.y, circle.radius, 4f, joinType);
//                }
//                case CollisionRect rect -> {
//                    var rectangle = rect.rectangle(pos);
//                    shapes.rectangle(rectangle, Color.MAGENTA, 4f);
//                }
//                case CollisionGrid grid -> {
//                    shapes.setColor(ColorType.DANGER.get());
//                    for (int y = 0; y < grid.rows(); y++) {
//                        for (int x = 0; x < grid.cols(); x++) {
//                            if (grid.get(x, y).solid) {
//                                gridCell.set(
//                                    pos.x + x * grid.cellSize(),
//                                    pos.y + y * grid.cellSize(),
//                                    grid.cellSize(), grid.cellSize());
//                                shapes.rectangle(gridCell, ColorType.DANGER.get(), 2f);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        shapes.setColor(prevColor);
//    }
//
//    private void drawEntityGravities(ShapeDrawer shapes) {
//        if (!drawEntityGravities) return;
//
//        var gravityLength = 10f;
//        var endpoint = FramePool.vec2();
//        var triangleOffset = FramePool.vec2(8f, 16f);
//
//        var prevColor = shapes.getPackedColor();
//        for (var entity : entities) {
//            var grav = Components.get(entity, Gravity.class);
//            if (grav == null) continue;
//
//            var pos = Components.get(entity, Position.class);
//            if (pos == null) continue;
//
//            endpoint.set(
//                pos.x + gravityLength * GRAVITY_DIR.x,
//                pos.y + gravityLength * GRAVITY_DIR.y);
//
//            shapes.line(pos.x, pos.y, endpoint.x, endpoint.y);
//
//            shapes.filledTriangle(
//                endpoint.x, endpoint.y,
//                endpoint.x - triangleOffset.x, endpoint.y - triangleOffset.y,
//                endpoint.x + triangleOffset.x, endpoint.y - triangleOffset.y,
//                ColorType.DARK.get());
//
//            shapes.triangle(
//                endpoint.x, endpoint.y,
//                endpoint.x - triangleOffset.x, endpoint.y - triangleOffset.y,
//                endpoint.x + triangleOffset.x, endpoint.y - triangleOffset.y,
//                2f, JoinType.SMOOTH, ColorType.DARK_BORDER.get().toFloatBits());
//        }
//        shapes.setColor(prevColor);
//    }
//
//    private void drawEntityVelocities(ShapeDrawer shapes) {
//        if (!drawEntityVelocities) return;
//
//        var endpoint = FramePool.vec2();
//        var lineWidth = 4f;
//        var circleRadius = 8f;
//        var maxLength = 200f;
//
//        var prevColor = shapes.getPackedColor();
//        for (var entity : entities) {
//            var pos = Components.get(entity, Position.class);
//            var vel = Components.get(entity, Velocity.class);
//            if (pos == null) continue;
//            if (vel == null) continue;
//
//            var unitVel = FramePool.vec2().set(vel.value).nor();
//            endpoint.set(
//                pos.x + maxLength * unitVel.x,
//                pos.y + maxLength * unitVel.y);
//
//            shapes.line(pos.x, pos.y, endpoint.x, endpoint.y, ColorType.SUCCESS.get(), lineWidth);
//
//            shapes.filledCircle(endpoint.x, endpoint.y, circleRadius, ColorType.LIGHT.get());
//            shapes.filledCircle(endpoint.x, endpoint.y, circleRadius - lineWidth, ColorType.DARK_BORDER.get());
//        }
//        shapes.setColor(prevColor);
//    }
//
//    private void drawEntityNavigation(ShapeDrawer shapes, Scene scene) {
//        if (!drawEntityNavigation) return;
//
//        var prevColor = shapes.getPackedColor();
//        for (var entity : players) {
//            var nav = Components.get(entity, Navigation.class);
//            if (nav == null) continue;
//
//            if (nav.behavior() != null) {
//                nav.behavior().renderDebug(entity, scene, shapes);
//            }
//        }
//        shapes.setColor(prevColor);
//    }
//
//    private void drawPips(ShapeDrawer shapes, Rectangle bounds, int count) {
//        if (count <= 0) return;
//        var margin = 4f;
//
//        // 1. Define the drawable inner area by applying the margin.
//        var innerRect = FramePool.rect(
//            bounds.x + margin,
//            bounds.y + margin,
//            bounds.width - margin * 2,
//            bounds.height - margin * 2);
//
//        // If the margin is too large, there's no space to draw.
//        if (innerRect.width <= 0 || innerRect.height <= 0) {
//            return;
//        }
//
//        // 2. Determine the most square-like grid dimensions.
//        int cols = (int) Math.ceil(Math.sqrt(count));
//        int rows = (int) Math.ceil((double) count / cols);
//
//        // 3. Calculate pip radius based on the inner area's cell size.
//        var cellWidth = innerRect.width / cols;
//        var cellHeight = innerRect.height / rows;
//        var radius = Math.min(cellWidth, cellHeight) * 0.4f;
//
//        var prevColor = shapes.getPackedColor();
//        shapes.setColor(Color.BLACK);
//
//        // 4. Iterate and draw each pip within the inner area.
//        int pipsDrawn = 0;
//        for (int r = 0; r < rows; r++) {
//            int pipsInThisRow = Math.min(cols, count - pipsDrawn);
//            var xOffset = (innerRect.width - (pipsInThisRow * cellWidth)) / 2f;
//
//            for (int c = 0; c < pipsInThisRow; c++) {
//                // Calculate position relative to the inner bounds.
//                var x = innerRect.x + xOffset + (c + 0.5f) * cellWidth;
//                var y = innerRect.y + (r + 0.5f) * cellHeight;
//                shapes.filledCircle(x, y, radius);
//            }
//            pipsDrawn += pipsInThisRow;
//        }
//
//        shapes.setColor(prevColor);
//    }
}
