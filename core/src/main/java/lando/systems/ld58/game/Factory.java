package lando.systems.ld58.game;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld58.assets.AnimType;
import lando.systems.ld58.assets.Assets;
import lando.systems.ld58.assets.ImageType;
import lando.systems.ld58.game.components.*;
import lando.systems.ld58.game.components.collision.CollisionMask;
import lando.systems.ld58.screens.BaseScreen;

public class Factory {

    private static BaseScreen screen;
    private static Engine engine;
    private static Assets assets;

    public static void init(BaseScreen screen) {
        Factory.screen = screen;
        Factory.engine = screen.engine;
        Factory.assets = screen.assets;
    }

    /**
     * Wrapper for {@link Engine#createEntity()} which ensures all
     * {@link Entity} instances has {@link Id} component attached.
     */
    public static Entity createEntity() {
        var entity = engine.createEntity();
        entity.add(new Id());
        return entity;
    }

    public static Entity player(TilemapObject.Spawner spawner) {
        return player(spawner.x(), spawner.y());
    }

    public static Entity player(Position position) {
        return player(position.x, position.y);
    }

    public static Entity player(int x, int y) {
        var entity = createEntity();

        entity.add(new Player());
        entity.add(new Name("Player"));

        entity.add(new Position(x, y));
        entity.add(new Velocity(0, 0));
        entity.add(new Friction(Constants.FRICTION_CLIMBER));
        entity.add(new Gravity(Constants.GRAVITY));
        entity.add(new Input());

        var animatorOrigin = new Vector2(48, 0);
        entity.add(new Animator(AnimType.GOOMBA_NORMAL_IDLE, animatorOrigin));

        entity.add(new Cooldowns()
            .add("jump", 0.2f)
            .add("taunt", 0.2f));

        var colliderBounds = new Rectangle(-17, 0, 34, 96);
        var collidesWith   = new CollisionMask[] { CollisionMask.SOLID, CollisionMask.PLAYER };
        entity.add(Collider.rect(CollisionMask.PLAYER, colliderBounds, collidesWith));

        return entity;
    }

    public static Entity solid(String name, int x, int y, int w, int h) {
        var entity = createEntity();

        entity.add(new Name(name));
        entity.add(new Position(x, y));

        var image = new Image(assets.pixelRegion);
        image.size.set(w, h);
        image.tint.set(Color.SALMON);
        entity.add(image);

        var colliderBounds = new Rectangle(0, 0, w, h);
        var collidesWith   = new CollisionMask[] {};
        entity.add(Collider.rect(CollisionMask.SOLID, colliderBounds, collidesWith));

        return entity;
    }

    public static Entity background(ImageType imageType) {
        var entity = createEntity();

        entity.add(new Image(imageType, new Vector2(1080, 12_800)));

        return entity;
    }

    public static Entity map(String tmxFilePath) {
        var entity = createEntity();

        var name = new Name("map:" + tmxFilePath);
        var position = new Position(0, 0);
        var tilemap = new Tilemap(tmxFilePath, screen.worldCamera);
        var collider = tilemap.newGridCollider();
        var bounds = tilemap.newBounds();

        entity.add(name);
        entity.add(position);
        entity.add(tilemap);
        entity.add(collider);
        entity.add(bounds);

        // TODO: Parse platforms for AI navigation, must be done after other components are added to entity
//        var platforms = NavPlatformParser.extractPlatforms(entity);
//        entity.add(new Platforms(entity, platforms));

        return entity;
    }

    public static Entity view(float scrollDurationSecs, boolean startPaused) {
        var entity = createEntity();

        var name = new Name("view");
        var viewer = new Viewer(screen.worldCamera);
        var interp = new Interp(scrollDurationSecs);
        if (startPaused) {
            interp.pause();
        }

        entity.add(name);
        entity.add(viewer);
        entity.add(interp);

        return entity;
    }
}
