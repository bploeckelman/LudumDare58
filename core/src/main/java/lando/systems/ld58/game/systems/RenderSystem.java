package lando.systems.ld58.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import lando.systems.ld58.Main;
import lando.systems.ld58.assets.ShaderType;
import lando.systems.ld58.game.Components;
import lando.systems.ld58.game.components.Position;
import lando.systems.ld58.game.components.TileLayer;
import lando.systems.ld58.game.components.renderable.*;
import lando.systems.ld58.utils.FramePool;
import lando.systems.ld58.utils.Util;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class RenderSystem extends SortedIteratingSystem {

    private static final Family RENDERABLES = Family
        .one(Image.class, Animator.class, Outline.class, TileLayer.class).get();

    private static final Comparator<Entity> comparator = (e1, e2) -> {
        var r1 = Renderable.getRenderable(e1);
        var r2 = Renderable.getRenderable(e2);
        float e1Depth = r1 == null ? 0 : r1.depth;
        float e2Depth = r2 == null ? 0 : r2.depth;
        return (int)(e1Depth - e2Depth);
    };

    private final Map<Entity, TiledMapRenderer> mapRenderers;
    private float accum = 0;

    public RenderSystem() {
        super(RENDERABLES, comparator);
        this.mapRenderers = new HashMap<>();
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        accum += deltaTime;

        var family = Family.one(RelicPickupRender.class).get();
        var relicRenderEntities = getEngine().getEntitiesFor(family);
        for (var e : relicRenderEntities) {
            var render = Components.get(e, RelicPickupRender.class);
            render.accum += deltaTime;
//            if (render.accum > RelicPickupRender.DURATION) {
//                e.remove(RelicPickupRender.class);
//            }
        }
    }

    @Override
    public void processEntity(Entity entity, float deltaTime) {
        var kirby = Components.get(entity, KirbyShaderRenderable.class);
        if (kirby != null) {
            kirby.accum += deltaTime;

            if (kirby.strength < kirby.targetStrength) {
                float amount = kirby.rampUpTime * deltaTime;
                if (amount + kirby.strength > kirby.targetStrength) {
                    kirby.strength = kirby.targetStrength;
                } else {
                    kirby.strength += amount;
                }
            }
            else if (kirby.strength > kirby.targetStrength) {
                float amount = kirby.rampDownTime * deltaTime;
                if (kirby.strength - amount < kirby.targetStrength) {
                    kirby.strength =  kirby.targetStrength;
                } else  {
                    kirby.strength -= amount;
                }
            }
        }

        var flames = Components.get(entity, FlameShaderRenderable.class);
        if (flames != null) {
            flames.accum += deltaTime;
        }
    }

    public void draw(SpriteBatch batch) {
        for (var entity : getEntities()) {
            renderTileLayer(batch, entity);
            renderRenderablesWithOutline(batch, entity);
            renderKirbyShader(batch, entity);
            renderFlameShader(batch, entity);
        }
    }

    public void drawInWindowSpace(SpriteBatch batch, OrthographicCamera camera) {
        var relicRenderFamily = Family.one(RelicPickupRender.class).get();
        var relicRenderEntities = getEngine().getEntitiesFor(relicRenderFamily);
        for (var entity : relicRenderEntities) {
            var render = Components.get(entity, RelicPickupRender.class);
            if (!render.isComplete()) {
                renderRelicShader(entity, batch, camera);
            }
        }
    }

    private void renderFlameShader(SpriteBatch batch, Entity entity) {
        var pos = Components.optional(entity, Position.class).orElse(Position.ZERO);
        var flame = Components.get(entity, FlameShaderRenderable.class);
        if (flame == null) return;

        var rect = flame.rect(pos);

        var shader = flame.shaderProgram;
        batch.setShader(shader);
        {
            shader.setUniformf("u_color1", flame.color1);
            shader.setUniformf("u_color2", flame.color2);
            shader.setUniformf("u_time", flame.accum);

            batch.draw(flame.texture, rect.x, rect.y, rect.width, rect.height);
        }
        batch.setShader(null);
    }

    private void renderKirbyShader(SpriteBatch batch, Entity entity) {
        var pos = Components.optional(entity, Position.class).orElse(Position.ZERO);
        var kirby = Components.get(entity, KirbyShaderRenderable.class);
        if (kirby == null) return;

        var rect = kirby.rect(pos);

        var shader = kirby.shaderProgram;
        batch.setShader(shader);
        {
            shader.setUniformf("u_strength", kirby.strength);
            shader.setUniformf("u_time", kirby.accum);

            batch.draw(kirby.texture, rect.x, rect.y, rect.width, rect.height);
        }
        batch.setShader(null);
    }

    public void renderRelicShader(Entity entity, SpriteBatch batch, OrthographicCamera camera) {
        var pickup = Components.get(entity, RelicPickupRender.class);
        if (pickup == null) return;

        var relicRegion = pickup.getRelicTexture();
        var pixel = Main.game.assets.pixel;
        var alpha = MathUtils.clamp(Math.min(pickup.accum * 2f, RelicPickupRender.DURATION - pickup.accum), 0f, 1f);
        var color = FramePool.color(1, 1, 1, alpha);
        var viewWidth = camera.viewportWidth;
        var viewHeight = camera.viewportHeight;

        var relic = ShaderType.RELIC.get();
        batch.setShader(relic);
        {
            relic.setUniformf("u_rotation", pickup.getRotation());

            batch.setColor(color);
            batch.draw(pixel, 0, 0, viewWidth, viewHeight);
            batch.setColor(Color.WHITE);
        }

        var outline = ShaderType.OUTLINE.get();
        batch.setShader(outline);
        {
            var fillColor = Color.CLEAR_WHITE;
            var outlineColor = Util.hsvToRgb(accum, 1f, 1f, FramePool.color());
            var thickness = 3f;
            var size = 200f;

            outline.setUniformf("u_fill_color", fillColor);
            outline.setUniformf("u_outline_color", outlineColor);
            outline.setUniformf("u_thickness",
                thickness / (float) relicRegion.getTexture().getWidth(),
                thickness / (float) relicRegion.getTexture().getHeight());

            batch.setColor(color);
            batch.draw(relicRegion, (viewWidth - size) / 2f, (viewHeight - size) / 2f, size, size);
        }

        batch.setColor(Color.WHITE);
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

    private void renderRenderablesWithOutline(SpriteBatch batch, Entity entity) {
        var pos      = Components.optional(entity, Position.class).orElse(Position.ZERO);
        var outline  = Components.optional(entity, Outline.class).orElse(Outline.CLEAR);
        var image    = Components.get(entity, Image.class);
        var animator = Components.get(entity, Animator.class);

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
            return;
        }

        var prevColor = FramePool.color().set(batch.getColor());
        var shader = ShaderType.OUTLINE.get();
        batch.setShader(shader);
        {
            shader.setUniformf("u_fill_color", outline.fillColor());
            shader.setUniformf("u_outline_color", outline.outlineColor());

            batch.setColor(tintColor);

            if (texture != null) {
                shader.setUniformf("u_thickness",
                    outline.outlineThickness() / (float) texture.getWidth(),
                    outline.outlineThickness() / (float) texture.getHeight());
                batch.draw(texture, rect.x, rect.y, rect.width, rect.height);
            }

            if (region != null) {
                shader.setUniformf("u_thickness",
                    outline.outlineThickness() / (float) region.getTexture().getWidth(),
                    outline.outlineThickness() / (float) region.getTexture().getHeight());
                Util.draw(batch, region, rect, tintColor);
            }
        }
        batch.setShader(null);
        batch.setColor(prevColor);
    }
}
