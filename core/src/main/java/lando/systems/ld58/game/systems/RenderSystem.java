package lando.systems.ld58.game.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import lando.systems.ld58.Main;
import lando.systems.ld58.game.Components;
import lando.systems.ld58.game.components.*;
import lando.systems.ld58.utils.FramePool;
import lando.systems.ld58.utils.Util;

import java.util.HashMap;
import java.util.Map;

public class RenderSystem extends EntitySystem {

    private final Map<Entity, TiledMapRenderer> mapRenderers;

    private ImmutableArray<Entity> entities;

    public RenderSystem() {
        this.mapRenderers = new HashMap<>();
    }

    @Override
    public void addedToEngine(Engine engine) {
        var family = Family.one(Image.class, Animator.class, Tilemap.class, Outline.class).get();
        entities = engine.getEntitiesFor(family);
    }

    public void draw(SpriteBatch batch) {
        for (var entity : entities) {

            renderImageWithOutline(batch, entity);

            var pos = Components.optional(entity, Position.class).orElse(Position.ZERO);
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

    private void renderImageWithOutline(SpriteBatch batch, Entity entity) {
        var pos = Components.optional(entity, Position.class).orElse(Position.ZERO);
        var outline = Components.optional(entity, Outline.class).orElse(Outline.CLEAR);

        // Draw simple renderables
        var image = Components.optional(entity, Image.class).orElse(null);
        var animator = Components.optional(entity, Animator.class).orElse(null);

        TextureRegion region = null;
        Texture texture = null;
        Rectangle rect = null;
        Color tintColor = FramePool.color().set(Color.WHITE);
        if (image != null) {
            region = image.getTextureRegion();
            texture = image.getTexture();
            rect = image.rect(pos);
            tintColor = image.tint;
        }
        if (animator != null) {
            region = animator.keyframe();
            rect = animator.rect(pos);
            tintColor = animator.tint;
        }

        ShaderProgram outlineShader = Main.game.assets.outlineShader;
        batch.setShader(outlineShader);
        outlineShader.setUniformf("u_fill_color", outline.fillColor());
        outlineShader.setUniformf("u_outline_color", outline.outlineColor());

        var prevColor = FramePool.color().set(batch.getColor());
        batch.setColor(tintColor);
        if (texture != null) {
            outlineShader.setUniformf("u_thickness", outline.outlineThickness() / (float) texture.getWidth(),
                outline.outlineThickness() / (float) texture.getHeight());
            batch.draw(texture, rect.x, rect.y, rect.width, rect.height);
        }
        if (region != null) {
            outlineShader.setUniformf("u_thickness", outline.outlineThickness() / (float) region.getTexture().getWidth(),
                outline.outlineThickness() / (float) region.getTexture().getHeight());
            Util.draw(batch, region, rect, tintColor);
        }
        batch.setColor(prevColor);
        batch.setShader(null);
    }
}
