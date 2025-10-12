package lando.systems.ld58.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import lando.systems.ld58.game.Components;
import lando.systems.ld58.game.components.Collider;
import lando.systems.ld58.game.components.Position;
import lando.systems.ld58.game.components.TilemapObject;
import lando.systems.ld58.game.components.collision.CollisionRect;
import lando.systems.ld58.game.scenes.Scene;
import lando.systems.ld58.game.signals.TriggerEvent;
import lando.systems.ld58.screens.BaseScreen;
import lando.systems.ld58.utils.Util;

public class MapTriggerSystem extends IteratingSystem {

    private Scene<? extends BaseScreen> scene;

    public MapTriggerSystem() {
        super(Family.one(TilemapObject.Trigger.class).get());
    }

    @Override
    public void update(float delta) {
        scene = Util.findScene(getEngine());
        super.update(delta);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        // Bail if this trigger has already been activated
        var trigger = Components.get(entity, TilemapObject.Trigger.class);
        if (trigger.activated) return;

        // Need a player collider in world space to trigger triggers
        if (scene == null || scene.player == null) return;
        var position = Components.get(scene.player, Position.class);
        var collider = Components.get(scene.player, Collider.class);
        var playerRect = collider.shape(CollisionRect.class).rectangle(position);

        if (playerRect.overlaps(trigger.bounds)) {
            TriggerEvent.dialog(trigger);
        }
    }
}
