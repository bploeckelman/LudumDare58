package lando.systems.ld58.game;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxRuntimeException;
import lando.systems.ld58.assets.AnimType;
import lando.systems.ld58.assets.Assets;
import lando.systems.ld58.assets.EmitterType;
import lando.systems.ld58.assets.ImageType;
import lando.systems.ld58.game.components.*;
import lando.systems.ld58.game.components.collision.CollisionMask;
import lando.systems.ld58.game.components.enemies.EnemyAngrySun;
import lando.systems.ld58.game.components.enemies.EnemyMario;
import lando.systems.ld58.game.components.renderable.*;
import lando.systems.ld58.particles.ParticleEffectParams;
import lando.systems.ld58.screens.BaseScreen;

public class Factory {

    private static final String TAG = Factory.class.getSimpleName();

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

        var animBounds = Constants.GOOMBA_ANIMATOR_BOUNDS;
        var animOrigin = animBounds.getPosition(new Vector2());
        var animator = new Animator(AnimType.GOOMBA_NORMAL_IDLE, animOrigin);
        animator.depth = 1;
        entity.add(animator);
        entity.add(new Outline(Color.YELLOW, Color.CLEAR, 1f));
        entity.add(new KirbyShaderRenderable());
        entity.add(new FlameShaderRenderable(animator));
        entity.add(new Cooldowns()
            .add("jump", 0.2f)
            .add("taunt", 0.2f));

        var collidesWith   = new CollisionMask[] { CollisionMask.SOLID, CollisionMask.ENEMY };
        entity.add(Collider.rect(CollisionMask.PLAYER, Constants.GOOMBA_COLLIDER_BOUNDS, collidesWith));

        return entity;
    }

    public static Entity mario(TilemapObject.Spawner spawner) {
        if (!"mario".equals(spawner.type)) {
            throw new GdxRuntimeException(TAG + ": tried to create mario from spawner without mario type");
        }

        var entity = createEntity();

        entity.add(new Name("Mario"));
        entity.add(new EnemyMario());

        entity.add(new Position(spawner.x, spawner.y));
        entity.add(new Velocity(0, 0));
        entity.add(new Friction(Constants.FRICTION_CLIMBER));
        entity.add(new Gravity(Constants.GRAVITY));

        var animBounds = Constants.MARIO_ANIMATOR_BOUNDS;
        var animOrigin = animBounds.getPosition(new Vector2());
        entity.add(new Animator(AnimType.MARIO_IDLE, animOrigin));

        var collidesWith  = new CollisionMask[] { CollisionMask.SOLID, CollisionMask.PLAYER };
        entity.add(Collider.rect(CollisionMask.ENEMY, Constants.MARIO_COLLIDER_BOUNDS, collidesWith));

        return entity;
    }

    public static Entity angrySun(TilemapObject.Spawner spawner) {
        if (!"sun".equals(spawner.type)) {
            throw new GdxRuntimeException(TAG + ": tried to create angry sun from spawner without angry sun type");
        }

        var entity = createEntity();

        entity.add(new Name("Sun"));
        entity.add(new EnemyAngrySun());

        entity.add(new Position(spawner.x, spawner.y));
        entity.add(new Velocity(0, 0));

        entity.add(new Outline(Color.RED, Color.CLEAR, 1f));
        var animOrigin = new Vector2(16, 12);
        var anim = new Animator(AnimType.ANGRY_SUN, animOrigin);
        anim.depth = 1000;
        entity.add(anim);

        var radius = 8f;
        var collidesWith  = new CollisionMask[] { CollisionMask.PLAYER };
        entity.add(Collider.circ(CollisionMask.ENEMY, 0, 0, radius, collidesWith));

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

    public static Entity view() {
        var entity = createEntity();

        var name = new Name("view");
        var viewer = new Viewer(screen.worldCamera);
        var interp = new Interp(1f);

        entity.add(name);
        entity.add(viewer);
        entity.add(interp);

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
