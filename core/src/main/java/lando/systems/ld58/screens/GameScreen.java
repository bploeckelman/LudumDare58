package lando.systems.ld58.screens;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
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
import lando.systems.ld58.game.components.renderable.RelicPickupRender;
import lando.systems.ld58.game.scenes.*;
import lando.systems.ld58.game.signals.AudioEvent;
import lando.systems.ld58.game.systems.PlayerStateSystem;
import lando.systems.ld58.input.ScreenInputHandler;
import lando.systems.ld58.utils.FramePool;

public class GameScreen extends BaseScreen {

    private enum SceneType { TEST, RELIC_1, RELIC_2, RELIC_3, FINALE }

//    private final Color backgroundColor = new Color(0x225522ff);
    private final Color backgroundColor = new Color(0x333333ff);

    public Scene<GameScreen> scene;

    public GameScreen() {
        Signals.playMusic.dispatch(new AudioEvent.PlayMusic(MusicType.MAIN_THEME, 0.5f));
        switchScene(SceneType.RELIC_1);
    }

    public Scene<? extends BaseScreen> scene() {
        return scene;
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        var relicCollectedEntities = engine.getEntitiesFor(Family.one(RelicPickupRender.class).get());

        for (Entity entity : relicCollectedEntities) {
            if (!transitioning && entity.getComponent(RelicPickupRender.class).isComplete()) {
                transitioning = true;
                switchScene(nextScene());
            }
        }

//        if (!transitioning && Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && Gdx.input.justTouched()) {
//            transitioning = true;
//            game.setScreen(new EndingScreen());
//        }
//        // TODO: temporary for testing scene changes -------------
//        if (!transitioning && Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) {
//            if      (scene instanceof SceneTest)   switchScene(SceneType.RELIC_1);
//            else if (scene instanceof SceneRelic1) switchScene(SceneType.RELIC_2);
//            else if (scene instanceof SceneRelic2) switchScene(SceneType.RELIC_3);
//            else if (scene instanceof SceneRelic3) switchScene(SceneType.FINALE);
//            else if (scene instanceof SceneFinale && !transitioning) {
//                transitioning = true;
//
//                // Cleanup ECS stuff from this screen before moving to the next screen/scene
//                Signals.changeState.remove(Systems.playerState);
//                engine.removeSystem(Systems.playerState);
//                engine.removeAllEntities();
//
//                Signals.stopMusic.dispatch(new AudioEvent.StopMusic());
//                game.setScreen(new EndingScreen());
//            }
//        }
//        // TODO: temporary for testing scene changes -------------

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

        batch.setProjectionMatrix(windowCamera.combined);
        batch.begin();
        Systems.render.drawInWindowSpace(batch, windowCamera);
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

    private SceneType nextScene () {
        if (scene instanceof SceneTest) { return SceneType.RELIC_1; }
        if (scene instanceof SceneRelic1) { return SceneType.RELIC_2; }
        if (scene instanceof SceneRelic2) { return SceneType.RELIC_3; }
        if (scene instanceof SceneRelic3) { return SceneType.FINALE; }
        return SceneType.FINALE;
    }

    private void switchScene(SceneType sceneType) {
        // Cleanup ECS stuff from this screen before moving to the next screen/scene
        Signals.removeEntity.remove(scene);
        Signals.changeState.remove(Systems.playerState);
        engine.removeSystem(Systems.playerState);
        engine.removeAllEntities();

        // Instantiate the new scene
        switch (sceneType) {
            case TEST:    scene = new SceneTest(this); break;
            case RELIC_1: scene = new SceneRelic1(this); break;
            case RELIC_2: scene = new SceneRelic2(this); break;
            case RELIC_3: scene = new SceneRelic3(this); break;
            case FINALE:
                game.setScreen(new EndingScreen());
                Signals.stopMusic.dispatch(new AudioEvent.StopMusic());
                return;
        }

        var entity = Factory.createEntity();
        entity.add(new SceneContainer(scene));
        engine.addEntity(entity);

        Systems.playerState = new PlayerStateSystem<>(this);
        engine.addSystem(Systems.playerState);

        var mapBounds = Components.get(scene.map(), Bounds.class);
        Systems.movement.mapBounds(mapBounds);

        Gdx.input.setInputProcessor(new ScreenInputHandler(this));

//        Signals.playMusic.dispatch(new AudioEvent.PlayMusic(MusicType.MAIN, 0.25f));
//        Signals.playMusic.dispatch(new AudioEvent.PlayMusic(MusicType.DIRGE, 0.25f));

        // Tick the engine for one frame first to get everything initialized
        engine.update(0f);
        transitioning = false;
    }
}
