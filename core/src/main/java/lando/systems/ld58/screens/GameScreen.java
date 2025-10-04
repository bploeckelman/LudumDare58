package lando.systems.ld58.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;
import lando.systems.ld58.Config;
import lando.systems.ld58.Flag;
import lando.systems.ld58.assets.ImageType;
import lando.systems.ld58.assets.MusicType;
import lando.systems.ld58.game.*;
import lando.systems.ld58.game.components.Bounds;
import lando.systems.ld58.game.components.SceneContainer;
import lando.systems.ld58.game.signals.AudioEvent;
import lando.systems.ld58.game.systems.MovementSystem;
import lando.systems.ld58.game.systems.PlayerStateSystem;
import lando.systems.ld58.input.ScreenInputHandler;
import lando.systems.ld58.utils.FramePool;
import lando.systems.ld58.utils.Util;

public class GameScreen extends BaseScreen {

    private final Color backgroundColor = new Color(0x225522ff);
    private final TextureRegion gdx;

    public GameScreen() {
        this.gdx = new TextureRegion(ImageType.GDX.get());

        var entity = Factory.createEntity();
        var scene = new SceneTest(this);
        entity.add(new SceneContainer(scene));
        engine.addEntity(entity);

        Systems.playerState = new PlayerStateSystem<>(this);
        engine.addSystem(Systems.playerState);

        var mapBounds = Components.get(scene.map(), Bounds.class);
        Systems.movement.mapBounds(mapBounds);

        Gdx.input.setInputProcessor(new ScreenInputHandler(this));

        Signals.playMusic.dispatch(new AudioEvent.PlayMusic(MusicType.CASTLEVANIA, 0.25f));
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if (!transitioning && Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && Gdx.input.justTouched()) {
            transitioning = true;
            game.setScreen(new EndingScreen());
        }

        if (Flag.FRAME_STEP.isEnabled()) {
            Config.stepped_frame = Gdx.input.isKeyJustPressed(Input.Keys.NUM_9);
            if (!Config.stepped_frame) {
                return;
            }
        }

        engine.update(delta);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(backgroundColor);

        // TEMP
        batch.setProjectionMatrix(windowCamera.combined);
        batch.begin();
        {
            Util.draw(batch, gdx, FramePool.rect(
                (windowCamera.viewportWidth - gdx.getRegionWidth()) / 2f,
                (windowCamera.viewportHeight - gdx.getRegionHeight()) / 2f,
                gdx.getRegionWidth(), gdx.getRegionHeight()));

            var pos = FramePool.vec2(
                (windowCamera.viewportWidth - layout.getWidth()) / 2f,
                windowCamera.viewportHeight - layout.getHeight());
            font.drawGlyphs(batch, layout, pos.x, pos.y);
        }
        batch.end();

        batch.setProjectionMatrix(worldCamera.combined);
        batch.begin();
        {
            Systems.render.draw(batch);
            Systems.renderDebug.draw(shapes);
        }
        batch.end();
    }
}
