package lando.systems.ld58.game.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import lando.systems.ld58.game.Components;
import lando.systems.ld58.game.components.Animator;
import lando.systems.ld58.game.components.Image;
import lando.systems.ld58.game.components.Position;
import lando.systems.ld58.game.components.Tilemap;

import java.util.HashMap;
import java.util.Map;

public class RenderSystem extends EntitySystem {

    //
    // TODO: eventually everything should go through here, backgrounds, overlays, maybe ui?
    //  then it can be changed to an IteratingSystem and the rendering bit can be per-entity
    //

    private final Map<Entity, TiledMapRenderer> mapRenderers;

    private ImmutableArray<Entity> entities;

    public RenderSystem() {
        this.mapRenderers = new HashMap<>();
    }

    @Override
    public void addedToEngine(Engine engine) {
        var family = Family.one(Image.class, Animator.class, Tilemap.class).get();
        entities = engine.getEntitiesFor(family);
    }

    public void draw(SpriteBatch batch) {
        for (var entity : entities) {
            var pos = Components.optional(entity, Position.class).orElse(Position.ZERO);

            // Draw simple renderables
            Components.optional(entity, Image.class).ifPresent(img -> img.render(batch, pos));
            Components.optional(entity, Animator.class).ifPresent(animator -> animator.render(batch, pos));

            // Draw tilemaps, creating a renderer first if one doesn't already exist for a given map
            Components.optional(entity, Tilemap.class).ifPresent(tilemap -> {
                var renderer = mapRenderers.computeIfAbsent(entity, e -> tilemap.newRenderer(batch));
                if (tilemap.map != null) {
                    renderer.setView(tilemap.camera);
                    for (var layer : tilemap.layers) {
                        layer.setOffsetX(pos.x);
                        layer.setOffsetY(-pos.y);
                        renderer.renderTileLayer(layer);
                    }
                }
            });
        }
    }
}
