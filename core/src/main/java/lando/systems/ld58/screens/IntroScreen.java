package lando.systems.ld58.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import lando.systems.ld58.Config;
import lando.systems.ld58.Flag;
import lando.systems.ld58.assets.MusicType;
import lando.systems.ld58.game.Components;
import lando.systems.ld58.game.Factory;
import lando.systems.ld58.game.Signals;
import lando.systems.ld58.game.Systems;
import lando.systems.ld58.game.components.Bounds;
import lando.systems.ld58.game.components.SceneContainer;
import lando.systems.ld58.game.scenes.Scene;
import lando.systems.ld58.game.scenes.SceneIntro;
import lando.systems.ld58.game.signals.AudioEvent;
import lando.systems.ld58.game.systems.PlayerStateSystem;
import lando.systems.ld58.input.ScreenInputHandler;
import lando.systems.ld58.utils.FramePool;

public class IntroScreen extends BaseScreen {

    private final Color backgroundColor = new Color(0xbbebf3ff);

    public final Scene<IntroScreen> scene;

    public IntroScreen() {
        var entity = Factory.createEntity();
        this.scene = new SceneIntro(this);
        entity.add(new SceneContainer(scene));
        engine.addEntity(entity);

        Systems.playerState = new PlayerStateSystem<>(this);
        engine.addSystem(Systems.playerState);

        var mapBounds = Components.get(scene.map(), Bounds.class);
        Systems.movement.mapBounds(mapBounds);

        Gdx.input.setInputProcessor(new ScreenInputHandler(this));

        Signals.playMusic.dispatch(new AudioEvent.PlayMusic(MusicType.DIRGE, 0.25f));

        // Tick the engine for one frame first to get everything initialized
        engine.update(0f);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        // TODO: trigger when player reaches a checkpoint
        if (!transitioning && Gdx.input.justTouched()){
            transitioning = true;

            // Cleanup ECS stuff from this screen before moving to the next screen/scene
            Signals.removeEntity.remove(scene);
            Signals.changeState.remove(Systems.playerState);
            engine.removeSystem(Systems.playerState);
            engine.removeAllEntities();

            Signals.stopMusic.dispatch(new AudioEvent.StopMusic());
            game.setScreen(new GameScreen());
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

        // Draw scene
        batch.setProjectionMatrix(worldCamera.combined);
        batch.begin();
        Systems.render.draw(batch);
        Systems.renderDebug.draw(shapes);
        batch.end();

        // Screen name overlay
        if (Flag.DEBUG_RENDER.isEnabled()) {
            batch.setProjectionMatrix(windowCamera.combined);
            batch.begin();
            var pos = FramePool.vec2(
                (windowCamera.viewportWidth - layout.getWidth()) / 2f,
                windowCamera.viewportHeight - layout.getHeight());
            font.drawGlyphs(batch, layout, pos.x, pos.y);
            batch.end();
        }
    }
}
