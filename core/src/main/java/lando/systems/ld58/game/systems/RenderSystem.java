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
import lando.systems.ld58.game.components.renderable.Animator;
import lando.systems.ld58.game.components.renderable.Image;
import lando.systems.ld58.game.components.renderable.KirbyShaderRenderable;
import lando.systems.ld58.game.components.renderable.Outline;
import lando.systems.ld58.utils.FramePool;
import lando.systems.ld58.utils.Util;

import java.util.HashMap;
import java.util.Map;

public class RenderSystem extends EntitySystem {

    private static final Family TILE_LAYERS = Family.one(TileLayer.class).get();
    private static final Family RENDERABLES = Family.one(Image.class, Animator.class, Outline.class).get();

    private final Map<Entity, TiledMapRenderer> mapRenderers;

    private ImmutableArray<Entity> tileLayers;
    private ImmutableArray<Entity> renderables;

    public RenderSystem() {
        this.mapRenderers = new HashMap<>();
    }

    @Override
    public void addedToEngine(Engine engine) {
        tileLayers = engine.getEntitiesFor(TILE_LAYERS);
        renderables = engine.getEntitiesFor(RENDERABLES);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        for (var entity : renderables) {
            var kirby = Components.optional(entity, KirbyShaderRenderable.class).orElse(null);
            if (kirby != null) {
                kirby.accum += deltaTime;
                kirby.strength((float)Math.cos(kirby.accum));
            }
        }
    }

    public void draw(SpriteBatch batch) {
        Util.streamOf(tileLayers).filter(TileLayer::isBackground).findFirst().ifPresent(tileLayer -> renderTileLayer(batch, tileLayer));
        Util.streamOf(tileLayers).filter(TileLayer::isMiddle).findFirst().ifPresent(tileLayer -> renderTileLayer(batch, tileLayer));

        renderRenderables(batch);

        Util.streamOf(tileLayers).filter(TileLayer::isForeground).findFirst().ifPresent(tileLayer -> renderTileLayer(batch, tileLayer));
    }

    private void renderKirbyShader(SpriteBatch batch, Entity entity) {
        var pos = Components.optional(entity, Position.class).orElse(Position.ZERO);
        var kirby = Components.optional(entity, KirbyShaderRenderable.class).orElse(null);
        if (kirby == null) return;
        ShaderProgram shader = kirby.shaderProgram;
        batch.setShader(shader);
        var rect = kirby.rect(pos);


        shader.setUniformf("u_strength", kirby.strength());
        shader.setUniformf("u_time", kirby.accum);

        batch.draw(kirby.texture, rect.x, rect.y, rect.width, rect.height);

        batch.setShader(null);

    }

    private void renderTileLayer(SpriteBatch batch, Entity entity) {
        var pos = Components.optional(entity, Position.class).orElse(Position.ZERO);
        var layer = Components.get(entity, TileLayer.class);
        if (layer == null || layer.tilemap == null) return;

        // Create map renderer if one doesn't already exist for the given map
        var tilemap = layer.tilemap;
        var renderer = mapRenderers.computeIfAbsent(entity, e -> tilemap.newRenderer(batch));
        renderer.setView(tilemap.camera);

        // Set position and invert y so the layer renders right side up
        layer.tileLayer.setOffsetX(pos.x);
        layer.tileLayer.setOffsetY(-pos.y);
        renderer.renderTileLayer(layer.tileLayer);
    }

    private void renderRenderables(SpriteBatch batch) {
        for (var entity : renderables) {
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

            if (image == null && animator == null) {
                continue;
            }

            ShaderProgram outlineShader = Main.game.assets.outlineShader;
            batch.setShader(outlineShader);
            outlineShader.setUniformf("u_fill_color", outline.fillColor());
            outlineShader.setUniformf("u_outline_color", outline.outlineColor());

            var prevColor = FramePool.color().set(batch.getColor());
            batch.setColor(tintColor);
            if (texture != null) {
                outlineShader.setUniformf("u_thickness",
                    outline.outlineThickness() / (float) texture.getWidth(),
                    outline.outlineThickness() / (float) texture.getHeight());
                batch.draw(texture, rect.x, rect.y, rect.width, rect.height);
            }
            if (region != null) {
                outlineShader.setUniformf("u_thickness",
                    outline.outlineThickness() / (float) region.getTexture().getWidth(),
                    outline.outlineThickness() / (float) region.getTexture().getHeight());
                Util.draw(batch, region, rect, tintColor);
            }
            batch.setColor(prevColor);
            batch.setShader(null);

            renderKirbyShader(batch, entity);
        }
    }
}
