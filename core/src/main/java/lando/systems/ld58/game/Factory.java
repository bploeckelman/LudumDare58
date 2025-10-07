package lando.systems.ld58.game;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld58.Main;
import lando.systems.ld58.assets.EmitterType;
import lando.systems.ld58.assets.ImageType;
import lando.systems.ld58.game.components.*;
import lando.systems.ld58.game.components.collision.CollisionMask;
import lando.systems.ld58.game.components.renderable.Image;
import lando.systems.ld58.game.factories.CharFactory;
import lando.systems.ld58.game.factories.MapFactory;
import lando.systems.ld58.particles.ParticleEffectParams;

public class Factory {

    /**
     * Wrapper for {@link Engine#createEntity()} which ensures all
     * {@link Entity} instances has {@link Id} component attached.
     */
    public static Entity createEntity() {
        var engine = Main.game.engine;
        var entity = engine.createEntity();
        entity.add(new Id());
        return entity;
    }

    public static Entity fromSpawner(TilemapObject.Spawner spawner) {
        var objType = TilemapObjectType.Registry.get(spawner.type);

        if (objType instanceof TilemapObjectType.Enemies) {
            var enemyType = (TilemapObjectType.Enemies) objType;
            return CharFactory.enemy(enemyType, spawner);
        }
        else if (objType instanceof TilemapObjectType.Blocks) {
            var blockType = (TilemapObjectType.Blocks) objType;
            return MapFactory.block(blockType, spawner);
        }
        else if (objType instanceof TilemapObjectType.Pickups) {
            var pickupType = (TilemapObjectType.Pickups) objType;
            return MapFactory.pickup(pickupType, spawner);
        }
        // TODO: ...
//        else if (objType instanceof TilemapObjectType.Misc) {
//            var miscType = (TilemapObjectType.Misc) objType;
//            switch (miscType) {
//                case TRIGGER: return MapFactory.trigger(miscType, spawner);
//            }
//        }
        else {
            // Default is to spawn a player - TODO: make explicit
            return CharFactory.player(spawner);
        }
    }

    public static Entity view(OrthographicCamera worldCamera) {
        var entity = createEntity();

        var name = new Name("VIEW");
        var viewer = new Viewer(worldCamera);
        var interp = new Interp(1f);

        entity.add(name);
        entity.add(viewer);
        entity.add(interp);

        return entity;
    }

    public static Entity solid(String name, int x, int y, int w, int h) {
        var entity = createEntity();

        entity.add(new Name(name));
        entity.add(new Position(x, y));

        var image = new Image(Main.game.assets.pixelRegion);
        image.size.set(w, h);
        image.tint.set(Color.SALMON);
        entity.add(image);

        var colliderBounds = new Rectangle(0, 0, w, h);
        var collidesWith   = new CollisionMask[] {};
        entity.add(Collider.rect(CollisionMask.SOLID, colliderBounds, collidesWith));

        return entity;
    }

    public static Entity background(ImageType imageType, Vector2 pos, Vector2 size) {
        var entity = createEntity();

        var position = new Position(pos.x, pos.y);

        var region = new TextureRegion(imageType.get());
        var image = new Image(region, size);
        image.tint.a = 0.75f;
        image.depth = -1000;

        entity.add(position);
        entity.add(image);

        return entity;
    }

    public static Entity emitter(EmitterType type, ParticleEffectParams params) {
        var entity = createEntity();

        var name = new Name("emitter-"+type.name().toLowerCase());
        var emitter = new Emitter(type, params);

        entity.add(name);
        entity.add(emitter);

        return entity;
    }
}
