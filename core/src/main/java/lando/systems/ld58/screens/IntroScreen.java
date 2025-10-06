package lando.systems.ld58.screens;

import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.tommyettinger.textra.TypingLabel;
import lando.systems.ld58.Config;
import lando.systems.ld58.Flag;
import lando.systems.ld58.assets.FontType;
import lando.systems.ld58.assets.ImageType;
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

    private static final float DEBOUNCE_DURATION = .31f;
    private FrameBuffer fbo;
    private Texture screenTexture;
    private float accum;
    private float trippyAmount = 0;
    private boolean changeToGameScreen = false;

//    private final Color backgroundColor = new Color(0xaaaaddff);
    private final Color backgroundColor = new Color(.1F, .5F, 1f, 1f);

    private final Map<String, String> dialogText = Map.of(
        "dialog-test-1", "Okay, something is definitely wrong.\nIt reeks of garlic and raw sewage in our otherwise bucolic valley.\n\n Usually that only happens after Big M swings through, but he hasn't been by since Super Mario Bros Wonder dropped.\nWhat gives?",
//        "dialog-test-2", "He that is wounded in the stones,\nor hath his privy member cut off,\nshall not enter into the congregation of the Lord.\n\n- Deuteronomy 23:1"
        "dialog-test-2", "At the last Cabal Strategic Alignment meeting, I vaguely remember Gannon complaining about getting stuck with Collector duty again...\n\nWe all know the 'dorf is a messy bitch who lives for drama, but would even HE be petty enough to leave the Ur-Artifacts scattered around after the last release?"
        ,"dialog-test-3", "He knows as well as we do that having the Ur-Artifacts lying around the Kingdom puts us at risk of another incursion situation like the Brooklyn Incident.\n\nWe can't be getting invaded by malevolent entities who wish us ill all the time! Where does he think he is, Hyrule?"
        ,"dialog-test-4", "I guess it's up to me now.\n\nI will collect these 3 Mario artifacts that are by their mere existence causing the Mushroom Kingdom to tumble into disrepair!"
        ,"dialog-test-5", "Coincidentally, this case happens to contain exact replicas of the very items I am looking for, which should make identifying them considerably easier.\n\nHow convenient!"
        ,"dialog-test-6", "Nice job holding the jump button down slightly longer than usual! \nThat might come in handy later.\n\nOr actually, it might not (I honestly can't remember where we landed with the level design...)"
        ,"dialog-test-7", "Thank you, Goomba!\n\nBut our exit is not in another castle!\n\n(Which is to say, you complete levels by collecting each respective relic, rather than by reaching any particular structure.)"
        ,"dialog-test-8", "One of the benefits of working in the Mushroom Kingdom is everybody wants to see you succeed.\n\nIf you hold the Down button, you can to suck in your comrades and use their powers. Cute!\n\nAlso uncomfortably intimate!"
        ,"dialog-test-9", "After you suck your comrade, you can hit Enter to use their powers!\n\nSharing is caring."
        ,"dialog-test-10", "When you're done having your friends inside you, you can simply hold Down and hit Enter.\n\nGoodbye, social obligations!"
    );

    public final Scene<IntroScreen> scene;
    public TypingLabel dialog;
    public float deBounce;

    public IntroScreen() {
        Signals.dialogTrigger.add(this);
        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, Config.window_width, Config.window_height, false);
        screenTexture = fbo.getColorBufferTexture();
//        screenTexture.setWrap(Texture.TextureWrap.MirroredRepeat, Texture.TextureWrap.MirroredRepeat);

        var entity = Factory.createEntity();
        this.scene = new SceneIntro(this);
        entity.add(new SceneContainer(scene));
        engine.addEntity(entity);

        Systems.playerState = new PlayerStateSystem<>(this);
        engine.addSystem(Systems.playerState);

        var mapBounds = Components.get(scene.map(), Bounds.class);
        Systems.movement.mapBounds(mapBounds);

        Gdx.input.setInputProcessor(new ScreenInputHandler(this));

        Signals.playMusic.dispatch(new AudioEvent.PlayMusic(MusicType.MARIO_DEGRADED, 0.55f));

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
        accum = 0;
    }

    public Scene<? extends BaseScreen> scene() {
        return scene;
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        accum += delta;
        if (!transitioning && changeToGameScreen) {
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
    public void renderOffscreenBuffers(SpriteBatch batch) {
        fbo.begin();

        ScreenUtils.clear(backgroundColor);
        // Draw scene
        batch.setProjectionMatrix(worldCamera.combined);
        batch.begin();
        Systems.render.draw(batch);
        Systems.renderDebug.draw(shapes);
        batch.end();

        fbo.end();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(backgroundColor);

        var shader = assets.hippieShader;
        batch.setShader(shader);
        batch.setProjectionMatrix(windowCamera.combined);
        batch.begin();
        ImageType.NOISE.get().bind(1);
        shader.setUniformi("u_texture2", 1);
        shader.setUniformf("u_time", accum);
        shader.setUniformf("u_res", windowCamera.viewportWidth, windowCamera.viewportHeight);
        shader.setUniformf("u_strength", trippyAmount);
        screenTexture.bind(0);
        shader.setUniformi("u_texture", 0);
        batch.draw(screenTexture, 0, screenTexture.getHeight(), screenTexture.getWidth(), -screenTexture.getHeight());
        batch.end();
        batch.setShader(null);

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
            batch.setColor(0, 0, 0, .65f);
            batch.draw(assets.pixel, dialog.getX()- 5, dialog.getY()-5, dialog.getWidth()+10, dialog.getHeight()+10);
            batch.setColor(Color.WHITE);
        }
        dialog.draw(batch, 1f);
    }
}
