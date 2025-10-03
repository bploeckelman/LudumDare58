package lando.systems.ld58.game;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import lando.systems.ld58.assets.Assets;
import lando.systems.ld58.game.components.Id;
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

//    public static Entity player(int playerNumber, TilemapObject.Spawner spawner) {
//        return player(playerNumber, spawner.x(), spawner.y());
//    }
//
//    public static Entity player(int playerNumber, Position position) {
//        return player(playerNumber, position.x, position.y);
//    }
//
//    public static Entity player(int playerNumber, int x, int y) {
//        var entity = createEntity();
//
//        entity.add(new Player(playerNumber));
//        entity.add(new Name("Player " + playerNumber));
//
//        entity.add(new Position(x, y));
//        entity.add(new Velocity(0, 0));
//        entity.add(new Friction(Constants.FRICTION_CLIMBER));
//        entity.add(new Gravity(Constants.GRAVITY));
//        entity.add(new Input());
//
//        var animatorOrigin = new Vector2(48, 0);
//        entity.add(new Animator(AnimType.PLAYER_CLIMBER_IDLE, animatorOrigin));
//
//        entity.add(new Cooldowns()
//            .add("jump", 0.2f)
//            .add("taunt", 0.2f));
//
//        var colliderBounds = new Rectangle(-17, 0, 34, 96);
//        var collidesWith   = new CollisionMask[] { CollisionMask.SOLID, CollisionMask.PLAYER };
//        entity.add(Collider.rect(CollisionMask.PLAYER, colliderBounds, collidesWith));
//
//        return entity;
//    }
//
//    public static Entity wall(String name, int x, int y, int w, int h) {
//        var entity = createEntity();
//
//        entity.add(new Name(name));
//        entity.add(new Position(x, y));
//
//        var image = new Image(assets.pixelRegion);
//        image.size.set(w, h);
//        image.tint.set(Color.SALMON);
//        entity.add(image);
//
//        var colliderBounds = new Rectangle(0, 0, w, h);
//        var collidesWith   = new CollisionMask[] {};
//        entity.add(Collider.rect(CollisionMask.SOLID, colliderBounds, collidesWith));
//
//        return entity;
//    }
//
//    public static Entity background(ImageType imageType) {
//        var entity = createEntity();
//
//        entity.add(new Image(imageType, new Vector2(1080, 12_800)));
//
//        return entity;
//    }
//
//    public static Entity map(String tmxFilePath) {
//        var entity = createEntity();
//
//        var name = new Name("map:" + tmxFilePath);
//        var position = new Position(0, 0);
//        var tilemap = new Tilemap(tmxFilePath, screen.worldCamera);
//        var collider = tilemap.newGridCollider();
//        var bounds = tilemap.newBounds();
//
//        entity.add(name);
//        entity.add(position);
//        entity.add(tilemap);
//        entity.add(collider);
//        entity.add(bounds);
//
//        // Parse platforms for AI navigation, must be done after other components are added to entity
//        var platforms = NavPlatformParser.extractPlatforms(entity);
//        entity.add(new Platforms(entity, platforms));
//
//        return entity;
//    }
//
//    public static Entity view(Duration scrollDuration, boolean startPaused) {
//        var entity = createEntity();
//
//        var name = new Name("view");
//        var viewer = new Viewer(screen.worldCamera);
//        var interp = new Interp(scrollDuration);
//        if (startPaused) {
//            interp.pause();
//        }
//
//        entity.add(name);
//        entity.add(viewer);
//        entity.add(interp);
//
//        return entity;
//    }
//
//    public static Entity snowball(Entity thrower) {
//        var entity = createEntity();
//
//        var snowball = new Snowball(thrower);
//
//        var throwerPos    = Components.get(thrower, Position.class);
//        var throwerAnim   = Components.get(thrower, Animator.class);
//        var throwerPlayer = Components.get(thrower, Player.class);
//
//        // NOTE(brian): override offsets for player 0 so throw comes from the center b/c of new squatch anim
//        var throwOffsetX = throwerAnim.facing * throwerAnim.size.x / 2f;
//        var throwOffsetY = 0f;
//        if (throwerPlayer.number == 1) {
//            throwOffsetX = 0;
//            throwOffsetY = throwerAnim.size.y;
//        }
//
//        var position = new Position(
//            throwerPos.x() + throwOffsetX,
//            throwerPos.y() + throwOffsetY);
//
//        // TODO(brian): add rotation support for Renderable component
//        // TODO(brian): add an interpolator component that increases the snowball size over time
//        var size = 64f;
//        var origin = FramePool.vec2(size / 2f, size / 2f);
//        var animator = new Animator(AnimType.SNOWBALL, origin);
//        animator.size.set(size, size);
//
//        var radius = (size - 10f) / 2f;
//        var collider = Collider.circ(CollisionMask.PROJECTILE, 0, 0, radius, CollisionMask.PLAYER);
//
//        var gravity = new Gravity(Constants.GRAVITY);
//        var velocity = new Velocity(0, -200);
//
//        // TODO: implement this in CollisionSystem
////        mover.setOnHit(onHitParams -> {
////            // what did it hit?
////            var hitCollider = onHitParams.hitCollider();
////            var hitEntity = hitCollider.entity;
////
////            // only climber players are 'hittable'
////            var hitPlayer = hitEntity.getIfActive(Player.class);
////            if (hitPlayer == null || hitPlayer.isSquatch()) {
////                return;
////            }
////
////            // spawn a particle effect
////            var particles = scene.screen.particles;
////            particles.snowballHit(position.x(), position.y());
////
////            // when a snowball hits a player, take the hit and disappear
////            hitPlayer.getHitBySnowball(entity);
////
////            // TODO(brian): we can probably do all the stuff from 'getHitBySnowball'
////            //   inline here using Timer and poking the hit player's animator, and collider and such
////            snowball.destroy();
////            scene.world.destroy(entity);
////        });
//
//        entity.add(snowball);
//        entity.add(position);
//        entity.add(animator);
//        entity.add(collider);
//        entity.add(gravity);
//        entity.add(velocity);
//
//        return entity;
//    }
//
//    @Deprecated(since = "for testing only, snowballs should normally always be created with a thrower")
//    public static Entity snowball(int x, int y) {
//        var entity = createEntity();
//
//        var snowball = new Snowball(null);
//        var position = new Position(x, y);
//
//        // TODO(brian): add rotation support for Renderable component
//        // TODO(brian): add an interpolator component that increases the snowball size over time
//        var size = 64f;
//        var origin = FramePool.vec2(size / 2f, size / 2f);
//        var animator = new Animator(AnimType.SNOWBALL, origin);
//        animator.size.set(size, size);
//
//        var radius = (size - 10f) / 2f;
//        var collider = Collider.circ(CollisionMask.PROJECTILE, 0, 0, radius, CollisionMask.PLAYER);
//
//        var gravity = new Gravity(Constants.GRAVITY);
//        var velocity = new Velocity(0, -200);
//
//        entity.add(snowball);
//        entity.add(position);
//        entity.add(animator);
//        entity.add(collider);
//        entity.add(gravity);
//        entity.add(velocity);
//
//        return entity;
//    }
}
