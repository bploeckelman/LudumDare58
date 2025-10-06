package lando.systems.ld58.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Intersector;
import com.github.tommyettinger.digital.Stringf;
import lando.systems.ld58.Flag;
import lando.systems.ld58.game.Components;
import lando.systems.ld58.game.components.*;
import lando.systems.ld58.game.components.collision.CollisionCirc;
import lando.systems.ld58.game.components.collision.CollisionRect;
import lando.systems.ld58.game.components.enemies.*;
import lando.systems.ld58.utils.Time;
import lando.systems.ld58.utils.Util;

import java.util.ArrayList;
import java.util.List;

public class OffscreenRespawnSystem extends IteratingSystem {

    private static final String TAG = OffscreenRespawnSystem.class.getSimpleName();
    private final List<Entity> entitiesBeingReset = new ArrayList<>();

    public OffscreenRespawnSystem() {
        // TODO: might make more sense to use animator vs collider bounds since animator can be bigger
        super(Family
            .all(Position.class, Collider.class, MySpawner.class)
            // Boo @ Java for making it a pain to do generic arrays and array appending
            .one(Player.class
                , EnemyAngrySun.class
                , EnemyBulletBill.class
                , EnemyCaptainLou.class
                , EnemyGoombaCyborg.class
                , EnemyHammerBro.class
                , EnemyKoopa.class
                , EnemyLakitu.class
                , EnemyMario.class
                , EnemyMisty.class
            ).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        // Skip entities that are actively being repositioned back at their spawn
        // Needed so we don't kick off multiple callbacks to move to spawn pos
        if (entitiesBeingReset.contains(entity)) return;

        // Need scene to get map to check map bounds
        var scene = Util.findScene(getEngine());
        if (scene == null) return;

        var position = Components.get(entity, Position.class);
        var collider = Components.get(entity, Collider.class);
        var spawner  = Components.get(entity, MySpawner.class).spawner;
        var mapBounds = Components.get(scene.map, Bounds.class);

        var overlaps = true;
        if (collider.shape instanceof CollisionCirc) {
            var circle = collider.shape(CollisionCirc.class).circle(position);
            overlaps = Intersector.overlaps(circle, mapBounds.rect);
        } else if (collider.shape instanceof CollisionRect) {
            var rectangle = collider.shape(CollisionRect.class).rectangle(position);
            overlaps = Intersector.overlaps(rectangle, mapBounds.rect);
        }

        if (!overlaps) {
            entitiesBeingReset.add(entity);

            // TODO: show dialog here at least once, but only if player, enemies don't get notified
            Time.do_after_delay(1f, (args) -> {
                position.set(spawner.x, spawner.y);
                entitiesBeingReset.remove(entity);

                if (Flag.LOG_DEBUG.isEnabled()) {
                    Util.log(TAG, Stringf.format("entity fell offscreen, moving back to spawn: id=%s, name=%s",
                        Components.optional(entity, Id.class).orElse(Id.UNKNOWN),
                        Components.optional(entity, Name.class).orElse(Name.UNKNOWN)));
                }
            });
        }
    }
}
