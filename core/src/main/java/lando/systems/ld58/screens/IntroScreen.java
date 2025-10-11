package lando.systems.ld58.screens;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.kotcrab.vis.ui.widget.VisTable;
import lando.systems.ld58.Config;
import lando.systems.ld58.Flag;
import lando.systems.ld58.assets.AnimType;
import lando.systems.ld58.assets.FontType2;
import lando.systems.ld58.assets.ImageType;
import lando.systems.ld58.assets.MusicType;
import lando.systems.ld58.game.Components;
import lando.systems.ld58.game.Factory;
import lando.systems.ld58.game.Signals;
import lando.systems.ld58.game.Systems;
import lando.systems.ld58.game.components.Bounds;
import lando.systems.ld58.game.components.Pickup;
import lando.systems.ld58.game.components.SceneContainer;
import lando.systems.ld58.game.components.Story;
import lando.systems.ld58.game.scenes.Scene;
import lando.systems.ld58.game.scenes.SceneIntro;
import lando.systems.ld58.game.signals.AudioEvent;
import lando.systems.ld58.game.signals.TriggerEvent;
import lando.systems.ld58.game.systems.PlayerStateSystem;
import lando.systems.ld58.game.systems.StorySystem;
import lando.systems.ld58.input.ScreenInputHandler;
import lando.systems.ld58.utils.FramePool;

import java.util.Map;

public class IntroScreen extends BaseScreen implements Listener<TriggerEvent> {

    private static final Color BACKGROUND_COLOR = new Color(.1f, .5f, 1f, 1f);

    private final StorySystem storySystem;
    private final FrameBuffer fbo;
    private final Texture screenTexture;
    private final MutableFloat trippyAmount;

    public final Scene<IntroScreen> scene;
    public final Entity storyEntity;

    private boolean changeToGameScreen;
    private boolean alreadyPickedUpShroom;
    private float accum;

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
        ,"dialog-test-10", "When you're done having your friends inside you,\nyou can simply hold Down and hit Enter.\n\nGoodbye, social obligations!"
    );

    public IntroScreen() {
        this.storySystem = engine.getSystem(StorySystem.class);
        this.fbo = new FrameBuffer(Pixmap.Format.RGBA8888, Config.window_width, Config.window_height, false);
        this.screenTexture = fbo.getColorBufferTexture();
        //this.screenTexture.setWrap(Texture.TextureWrap.MirroredRepeat, Texture.TextureWrap.MirroredRepeat);
        this.trippyAmount = new MutableFloat(0);
        this.accum = 0;
        this.changeToGameScreen = false;
        this.alreadyPickedUpShroom = false;
        this.scene = new SceneIntro(this);

        // Listen for signals
        Signals.dialogTrigger.add(this);
        Signals.collectTrigger.add(this);

        // Create entities and components for this screen
        engine.addEntity(Factory.createEntity().add(new SceneContainer(scene)));
        this.storyEntity = Factory.createEntity().add(new Story(
            new Story.Dialog(FontType2.ROUNDABOUT, AnimType.BILLY_YELL,
                "You see fuzzy images floating in your visual field...\n\n"
                    + "a plunger... a torch... a pipe wrench...\n"
                    + "They don't belong here, they're from... elsewhere\n\n"
                    + "You see a weird old man who looks kind of like... Mario?"),
            new Story.Dialog(FontType2.ROUNDABOUT, AnimType.BILLY_YELL,
                "So that's what's been happening in the Mushroom Kingdom!\n\n"
                    + "Weird old man Mario crossed over from another dimension\n"
                    + "and brought relics with him that are damaging our home.\n"),
            new Story.Dialog(FontType2.ROUNDABOUT, AnimType.BILLY_YELL,
                "The cabal is useless... ten years and they haven't figured this out.\n\n"
                    + "I guess it's up to me to set this right.\n\n"
                    + "I'll collect powers from others to search out those relics\n"
                    + "and destroy them in order to send Mario back!\n"),
            new Story.Dialog(FontType2.ROUNDABOUT, AnimType.BILLY_YELL, "Let's... uh... go!")));
        engine.addEntity(storyEntity);

        // Setup systems and initialize remaining bits
        Systems.playerState = new PlayerStateSystem<>(this);
        engine.addSystem(Systems.playerState);

        var mapBounds = Components.get(scene.map(), Bounds.class);
        Systems.movement.mapBounds(mapBounds);

        initializeUI();

        var inputMux = new InputMultiplexer(storySystem, new ScreenInputHandler(this));
        Gdx.input.setInputProcessor(inputMux);

        Signals.playMusic.dispatch(new AudioEvent.PlayMusic(MusicType.MARIO_DEGRADED, 0.2f));

        // Tick the engine for one frame first to get everything initialized
        engine.update(0f);
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

        // Pause for frame-stepping if enabled
        if (Flag.FRAME_STEP.isEnabled()) {
            Config.stepped_frame = Gdx.input.isKeyJustPressed(Input.Keys.NUM_9);
            if (!Config.stepped_frame) {
                return;
            }
        }

        // Pause for story if needed, otherwise update everything
        var shouldPauseForStory = storySystem.shouldPauseGame();
        if (shouldPauseForStory) {
            storySystem.update(delta);
            // TODO: might need manual update for RenderSystem here too
        } else {
            engine.update(delta);
        }
        uiStage.act(delta);
    }

    @Override
    public void renderOffscreenBuffers(SpriteBatch batch) {
        fbo.begin();

        ScreenUtils.clear(BACKGROUND_COLOR);
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
        ScreenUtils.clear(BACKGROUND_COLOR);

        var shader = assets.hippieShader;
        batch.setShader(shader);
        batch.setProjectionMatrix(windowCamera.combined);
        batch.begin();
        {
            ImageType.NOISE.get().bind(1);
            shader.setUniformi("u_texture2", 1);
            shader.setUniformf("u_time", accum);
            shader.setUniformf("u_res", windowCamera.viewportWidth, windowCamera.viewportHeight);
            shader.setUniformf("u_strength", trippyAmount.floatValue());

            // Draw scene with inverted y so it's right-side up
            screenTexture.bind(0);
            shader.setUniformi("u_texture", 0);
            batch.draw(screenTexture,
                0, screenTexture.getHeight(),
                screenTexture.getWidth(),
                -screenTexture.getHeight());
        }
        batch.end();
        batch.setShader(null);

        // Draw ui / dialog / story stuff
        uiStage.draw();

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

    // TODO: rework this once story system is fully refactored
    @Override
    public void receive(Signal<TriggerEvent> signal, TriggerEvent event) {
        if (event instanceof TriggerEvent.Dialog) {
            var dialogEvent = (TriggerEvent.Dialog) event;
//            var text = dialogText.get(dialogEvent.key);
//            if (text != null) {
//                dialog.setText(text);
//            }
        }
        else if (event instanceof TriggerEvent.Collect) {
            var collectEvent = (TriggerEvent.Collect) event;
            if (!alreadyPickedUpShroom && collectEvent.pickupType == Pickup.Type.SHROOM) {
                alreadyPickedUpShroom = true;
                Tween.to(trippyAmount, -1, 2f).target(1f).start(tween);
            }
        }
    }

    @Override
    public void initializeUI() {
        var root = new VisTable();
        root.setFillParent(true);
        uiStage.addActor(root);

        // Setup story dialog ui
        var margin = 200;
        storySystem.setup(new Rectangle(
            margin, Config.window_height / 2f,
            Config.window_width - 2*margin,
            Config.window_height / 3f));
        root.add(storySystem.uiRoot);
    }
}
