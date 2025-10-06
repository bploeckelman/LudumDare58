package lando.systems.ld58.screens;

import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.tommyettinger.textra.TypingLabel;
import lando.systems.ld58.Config;
import lando.systems.ld58.Flag;
import lando.systems.ld58.assets.FontType;
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
import lando.systems.ld58.game.signals.TriggerEvent;
import lando.systems.ld58.game.systems.PlayerStateSystem;
import lando.systems.ld58.input.ScreenInputHandler;
import lando.systems.ld58.utils.FramePool;

import java.util.Map;

public class IntroScreen extends BaseScreen implements Listener<TriggerEvent> {

    private static final float DEBOUNCE_DURATION = 1f;

    private final Color backgroundColor = new Color(0xbbebf3ff);

    private final Map<String, String> dialogText = Map.of(
        "dialog-test-1", "This is a test of the map triggered dialog event system.\nThis is only a test!",
        "dialog-test-2", "He that is wounded in the stones,\nor hath his privy member cut off,\nshall not enter into the congregation of the Lord.\n\n- Deuteronomy 23:1"
    );

    public final Scene<IntroScreen> scene;
    public TypingLabel dialog;
    public float deBounce;

    public IntroScreen() {
        Signals.dialogTrigger.add(this);

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

        font = FontType.ROUNDABOUT.font("medium");
        this.dialog = new TypingLabel("", font);
        this.dialog.setWrap(true);
        this.dialog.setAlignment(Align.center);
        this.dialog.setBounds(
            200, Config.window_height/2f,
            Config.window_width - 400,
            Config.window_height/3f);
        this.dialog.restart("");
        this.deBounce = DEBOUNCE_DURATION;

        // Tick the engine for one frame first to get everything initialized
        engine.update(0f);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        // TODO: trigger when player reaches a checkpoint
        if (!transitioning && Gdx.input.isKeyJustPressed(Input.Keys.ENTER)){
            transitioning = true;

            // Cleanup ECS stuff from this screen before moving to the next screen/scene
            Signals.dialogTrigger.remove(this);
            Signals.removeEntity.remove(scene);
            Signals.changeState.remove(Systems.playerState);
            engine.removeSystem(Systems.playerState);
            engine.removeAllEntities();

            Signals.stopMusic.dispatch(new AudioEvent.StopMusic());
            game.setScreen(new GameScreen());
        }

        // Pause if dialog is showing so player can't progress through to next dialog until it's cleared
        var isDialogShowing = !dialog.getOriginalText().toString().isEmpty();
        if (isDialogShowing) {
            // Update the dialog and early out until the player clears it
            dialog.act(delta);

            // Handle clearing or fast forwarding dialog
            if ((Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY) || Gdx.input.justTouched()) && deBounce <= 0) {
                if (!dialog.hasEnded()) {
                    dialog.skipToTheEnd();
                } else {
                    dialog.restart("");
                    deBounce = DEBOUNCE_DURATION;
                }
            }
            return;
        } else {
            deBounce = MathUtils.clamp(deBounce - delta, 0, DEBOUNCE_DURATION);
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

        // Draw ui / dialog / story stuff
        batch.setProjectionMatrix(windowCamera.combined);
        batch.begin();
        drawDialog(batch, delta);
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

    @Override
    public void receive(Signal<TriggerEvent> signal, TriggerEvent event) {
        if (event instanceof TriggerEvent.Dialog) {
            var dialogEvent = (TriggerEvent.Dialog) event;
            var text = dialogText.get(dialogEvent.key);
            if (text != null) {
                dialog.setText(text);
            }
        }
    }

    private void drawDialog(SpriteBatch batch, float delta) {
       if (!dialog.getOriginalText().toString().isEmpty()) {
            batch.setColor(0, 0, 0, .4f);
            batch.draw(assets.pixel, dialog.getX()- 5, dialog.getY()-5, dialog.getWidth()+10, dialog.getHeight()+10);
            batch.setColor(Color.WHITE);
        }
        dialog.draw(batch, 1f);
    }
}
