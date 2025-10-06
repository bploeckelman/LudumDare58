package lando.systems.ld58.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import lando.systems.ld58.Main;
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

    private static final Comparator<Entity> comparator = (o1, o2) -> {
        var r1 = Renderable.getRenderable(o1);
        var r2 = Renderable.getRenderable(o2);
        float o1Depth = r1 == null ? 0 : r1.depth;
        float o2Depth = r2 == null ? 0 : r2.depth;
        return (int)(o1Depth - o2Depth);
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

        for (Entity e : getEngine().getEntitiesFor(Family.one(RelicPickupRender.class).get())) {
            var pickup = e.getComponent(RelicPickupRender.class);
            pickup.accum += deltaTime;

//            if (pickup.accum > RelicPickupRender.DURATION) {
//                e.remove(RelicPickupRender.class);
//            }
        }
    }

    @Override
    public void processEntity(Entity entity, float deltaTime) {
        var kirby = Components.optional(entity, KirbyShaderRenderable.class).orElse(null);
        if (kirby != null) {
            kirby.accum += deltaTime;
            if (kirby.strength < kirby.targetStrength) {
                float amount = kirby.rampUpTime * deltaTime;
                if (amount  + kirby.strength > kirby.targetStrength) {
                    kirby.strength =  kirby.targetStrength;
                } else {
                    kirby.strength += amount;
                }
            } else if (kirby.strength > kirby.targetStrength) {
                float amount = kirby.rampDownTime * deltaTime;
                if (kirby.strength - amount < kirby.targetStrength) {
                    kirby.strength =  kirby.targetStrength;
                } else  {
                    kirby.strength -= amount;
                }
            }
        }

        var flames = Components.optional(entity, FlameShaderRenderable.class).orElse(null);
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
        var relicPickups = getEngine().getEntitiesFor(Family.one(RelicPickupRender.class).get());
        for (var entity : relicPickups) {
            if (!entity.getComponent(RelicPickupRender.class).isComplete()) {
                renderRelicShader(entity, batch, camera);
            }
        }

    }

    private void renderFlameShader(SpriteBatch batch, Entity entity) {
        var pos = Components.optional(entity, Position.class).orElse(Position.ZERO);
        var flame = Components.optional(entity, FlameShaderRenderable.class).orElse(null);
        if (flame == null) return;

        var shader = flame.shaderProgram;
        batch.setShader(shader);

        var rect = flame.rect(pos);
        shader.setUniformf("u_color1", flame.color1);
        shader.setUniformf("u_color2", flame.color2);
        shader.setUniformf("u_time", flame.accum);

        batch.draw(flame.texture, rect.x, rect.y, rect.width, rect.height);

        batch.setShader(null);
    }

    private void renderKirbyShader(SpriteBatch batch, Entity entity) {
        var pos = Components.optional(entity, Position.class).orElse(Position.ZERO);
        var kirby = Components.optional(entity, KirbyShaderRenderable.class).orElse(null);
        if (kirby == null) return;

        var shader = kirby.shaderProgram;
        batch.setShader(shader);

        var rect = kirby.rect(pos);
        shader.setUniformf("u_strength", kirby.strength);
        shader.setUniformf("u_time", kirby.accum);

        batch.draw(kirby.texture, rect.x, rect.y, rect.width, rect.height);

        batch.setShader(null);
    }

    public void renderRelicShader(Entity entity, SpriteBatch batch, OrthographicCamera camera) {
        var pickup = Components.optional(entity, RelicPickupRender.class).orElse(null);
        if (pickup == null) return;
        var shader = Main.game.assets.relicShader;
        batch.setShader(shader);

        float alpha = MathUtils.clamp(Math.min(pickup.accum * 2f, RelicPickupRender.DURATION - pickup.accum), 0f, 1f);

        shader.setUniformf("u_rotation", pickup.getRotation());
        batch.setColor(1,1,1,alpha);

        batch.draw(Main.game.assets.pixel, 0, 0, camera.viewportWidth, camera.viewportHeight);
        batch.setColor(Color.WHITE);

        var outlineShader = Main.game.assets.outlineShader;
        var thickness = 3f;
        var region = pickup.getRelicTexture();
        batch.setShader(outlineShader);
        batch.setColor(1, 1, 1, alpha);
        outlineShader.setUniformf("u_fill_color", Color.CLEAR_WHITE);
        outlineShader.setUniformf("u_outline_color", Util.hsvToRgb(accum * 1f, 1f, 1f, FramePool.color()));
        outlineShader.setUniformf("u_thickness",
            thickness / (float) region.getTexture().getWidth(),
            thickness / (float) region.getTexture().getHeight());
        batch.draw(region, camera.viewportWidth/2 - 100, camera.viewportHeight/2 - 100, 200, 200);
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
            return;
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
    }
}
