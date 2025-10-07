package lando.systems.ld58.game.factories;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import lando.systems.ld58.assets.AnimType;
import lando.systems.ld58.game.Constants;
import lando.systems.ld58.game.Factory;
import lando.systems.ld58.game.components.*;
import lando.systems.ld58.game.components.collision.CollisionMask;
import lando.systems.ld58.game.components.renderable.Animator;
import lando.systems.ld58.game.components.renderable.Outline;

public class MapFactory {

    // ------------------------------------------------------------------------
    // Map
    // ------------------------------------------------------------------------

    public static Entity map(String tmxFilePath, OrthographicCamera worldCamera) {
        var entity = Factory.createEntity();

        var name = new Name("map:" + tmxFilePath);
        var position = new Position(0, 0);
        var tilemap = new Tilemap(tmxFilePath, worldCamera);
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

    // ------------------------------------------------------------------------
    // Blocks
    // ------------------------------------------------------------------------

    public static Entity block(TilemapObjectType.Blocks type, TilemapObject.Spawner spawner) {
        var entity = Factory.createEntity();

        var mapObject = spawner.mapObject(TiledMapTileMapObject.class);
        var mapObjProps = mapObject.getProperties();

        // NOTE: 'tile' map object 'position' is a bit off of where we'd want it in game, manually adjusting it here
        var x = spawner.x + mapObjProps.get("width",  0f, Float.class) / 2f;
        var y = spawner.y + mapObjProps.get("height", 0f, Float.class) / 2f;

        var name = new Name(spawner.type);
        var position = new Position(x, y);
        var outline = new Outline(Color.ORANGE, Color.CLEAR_WHITE, 2f);

        entity.add(name);
        entity.add(position);
        entity.add(outline);

        switch (type) {
            case BREAK: {
                entity.add(new BlockBreakable());

                var anim = new Animator(AnimType.BLOCK_BREAK);
                anim.origin.set(8, 8);
                anim.depth = Constants.Z_DEPTH_DEFAULT + 1;
                entity.add(anim);

                var bounds = new Rectangle(-8, -8, 16, 16);
                var collidesWith  = new CollisionMask[] {};
                entity.add(Collider.rect(CollisionMask.DESTRUCTIBLE, bounds, collidesWith));
            } break;

            case COIN: {

            } break;

            case SPIKE: {
                entity.add(new BlockSpike());

                // TODO: read 'up/down' from tile map object
                var dir = mapObjProps.get("dir", "up", String.class);
                var animType = dir.equals("up") ? AnimType.BLOCK_SPIKE_UP : AnimType.BLOCK_SPIKE_DOWN;
                var anim = new Animator(animType);
                anim.origin.set(8, 8);
                anim.depth = Constants.Z_DEPTH_DEFAULT + 1;
                entity.add(anim);

                var bounds = new Rectangle(-8, -8, 16, 16);
                var collidesWith  = new CollisionMask[] {};
                entity.add(Collider.rect(CollisionMask.KILLER, bounds, collidesWith));
            } break;

            case LAVA: {
                entity.add(new BlockLava());

                var dir = mapObjProps.get("dir", "up", String.class);
                var animType = dir.equals("up") ? AnimType.BLOCK_LAVA_UP : AnimType.BLOCK_LAVA_DOWN;
                var anim = new Animator(animType);
                anim.origin.set(8, 8);
                anim.depth = Constants.Z_DEPTH_DEFAULT + 1;
                entity.add(anim);

                var bounds = new Rectangle(-8, -8, 16, 16);
                var collidesWith  = new CollisionMask[] {};
                entity.add(Collider.rect(CollisionMask.KILLER, bounds, collidesWith));
            } break;
        }

        return entity;
    }

    // ------------------------------------------------------------------------
    // Pickups
    // ------------------------------------------------------------------------

    public static Entity pickup(TilemapObjectType.Pickups type, TilemapObject.Spawner spawner) {
        var entity = Factory.createEntity();

        var mapObject = spawner.mapObject(TiledMapTileMapObject.class);
        var mapObjProps = mapObject.getProperties();

        // NOTE: 'tile' map object 'position' is a bit off of where we'd want it in game, manually adjusting it here
        var x = spawner.x + mapObjProps.get("width",  0f, Float.class) / 2f;
        var y = spawner.y + mapObjProps.get("height", 0f, Float.class) / 2f;

        var name = new Name(spawner.type);
        var position = new Position(x, y);
        var outline = new Outline(Color.YELLOW, Color.CLEAR_WHITE, 2f);
        var animDepth = Constants.Z_DEPTH_DEFAULT + 1;

        entity.add(name);
        entity.add(position);
        entity.add(outline);

        switch (type) {
            case COIN: {
                entity.add(Pickup.coin());

                var anim = new Animator(AnimType.COIN);
                anim.depth = animDepth;
                anim.origin.set(8, 8);
                entity.add(anim);

                var bounds = new Circle(0, 0, 6);
                var collidesWith  = new CollisionMask[] { CollisionMask.PLAYER };
                entity.add(Collider.circ(CollisionMask.PICKUP, bounds.x, bounds.y, bounds.radius, collidesWith));
            } break;

            case RELIC_PLUNGER: {
                entity.add(Pickup.plunger());

                var anim = new Animator(AnimType.RELIC_PLUNGER);
                anim.depth = animDepth;
                anim.origin.set(16, 16);
                anim.size.set(32, 32);
                entity.add(anim);

                var bounds = new Circle(0, 0, 10);
                var collidesWith  = new CollisionMask[] { CollisionMask.PLAYER };
                entity.add(Collider.circ(CollisionMask.PICKUP, bounds.x, bounds.y, bounds.radius, collidesWith));
            } break;

            case RELIC_TORCH: {
                entity.add(Pickup.torch());

                var anim = new Animator(AnimType.RELIC_TORCH);
                anim.depth = animDepth;
                anim.origin.set(16, 16);
                anim.size.set(32, 32);
                entity.add(anim);

                var bounds = new Circle(0, 0, 10);
                var collidesWith  = new CollisionMask[] { CollisionMask.PLAYER };
                entity.add(Collider.circ(CollisionMask.PICKUP, bounds.x, bounds.y, bounds.radius, collidesWith));
            } break;

            case RELIC_WRENCH: {
                entity.add(Pickup.wrench());

                var anim = new Animator(AnimType.RELIC_WRENCH);
                anim.depth = animDepth;
                anim.origin.set(16, 16);
                anim.size.set(32, 32);
                entity.add(anim);

                var bounds = new Circle(0, 0, 10);
                var collidesWith  = new CollisionMask[] { CollisionMask.PLAYER };
                entity.add(Collider.circ(CollisionMask.PICKUP, bounds.x, bounds.y, bounds.radius, collidesWith));
            } break;

            case SHROOM: {
                entity.add(Pickup.shroom());

                var anim = new Animator(AnimType.SHROOM);
                anim.depth = animDepth;
                anim.origin.set(8, 8);
                entity.add(anim);

                var bounds = new Circle(0, 0, 6);
                var collidesWith  = new CollisionMask[] { CollisionMask.PLAYER };
                entity.add(Collider.circ(CollisionMask.PICKUP, bounds.x, bounds.y, bounds.radius, collidesWith));
            } break;
        }

        return entity;
    }
}
