package lando.systems.ld58;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.tommyettinger.textra.Layout;
import com.kotcrab.vis.ui.VisUI;
import lando.systems.ld58.assets.Assets;
import lando.systems.ld58.assets.EffectType;
import lando.systems.ld58.game.Systems;
import lando.systems.ld58.screens.*;
import lando.systems.ld58.utils.FramePool;
import lando.systems.ld58.utils.Time;
import lando.systems.ld58.utils.accessors.*;

import static com.badlogic.gdx.Application.ApplicationType;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {

    public static Main game;

    public Assets assets;
    public TweenManager tween;
    public FrameBuffer frameBuffer;
    public TextureRegion frameBufferRegion;
    public OrthographicCamera windowCamera;
    public Engine engine;

    public BaseScreen currentScreen;
    Layout layout;

    public Main() {
        Main.game = this;
    }

    @Override
    public void create() {
        Time.init();
        layout = new Layout();

        assets = new Assets();
        Transition.init(assets);

        tween = new TweenManager();
        Tween.setWaypointsLimit(4);
        Tween.setCombinedAttributesLimit(4);
        Tween.registerAccessor(OrthographicCamera.class, new CameraAccessor());
        Tween.registerAccessor(Circle.class, new CircleAccessor());
        Tween.registerAccessor(Color.class, new ColorAccessor());
        Tween.registerAccessor(PerspectiveCamera.class, new PerspectiveCameraAccessor());
        Tween.registerAccessor(Rectangle.class, new RectangleAccessor());
        Tween.registerAccessor(Vector2.class, new Vector2Accessor());
        Tween.registerAccessor(Vector3.class, new Vector3Accessor());

        var format = Pixmap.Format.RGBA8888;
        int width = Config.framebuffer_width;
        int height = Config.framebuffer_height;
        var hasDepth = true;

        frameBuffer = new FrameBuffer(format, width, height, hasDepth);
        var frameBufferTexture = frameBuffer.getColorBufferTexture();
        frameBufferTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        frameBufferRegion = new TextureRegion(frameBufferTexture);
        frameBufferRegion.flip(false, true);

        windowCamera = new OrthographicCamera();
        windowCamera.setToOrtho(false, Config.window_width, Config.window_height);
        windowCamera.update();

        VisUI.setSkipGdxVersionCheck(true);
//        VisUI.load(SkinType.ZENDO.get());
        // TODO: restore zendo skin after fixing up skin loading with textra fonts
        VisUI.load();

        engine = new Engine();
        Systems.init(engine);

        var showLaunchScreen = (Gdx.app.getType() == ApplicationType.WebGL || Flag.LAUNCH_SCREEN.isEnabled());
        var startScreen = showLaunchScreen ? new LaunchScreen()
            : Flag.GAME_SCREEN.isEnabled() ? new GameScreen()
            : new TitleScreen();

        setScreen(startScreen);
    }

    @Override
    public void dispose() {
        VisUI.dispose();
        assets.dispose();
    }

    public void update(float delta) {
        // update things that must update every tick
        Time.update();
        tween.update(Time.delta);
        currentScreen.alwaysUpdate(Time.delta);
        Transition.update(Time.delta);

        // handle a pause
        if (Time.pause_timer > 0) {
            Time.pause_timer -= Time.delta;
            if (Time.pause_timer <= -0.0001f) {
                Time.delta = -Time.pause_timer;
            } else {
                // skip updates if we're paused
                return;
            }
        }
        Time.millis += Time.delta;
        Time.previous_elapsed = Time.elapsed_millis();

        currentScreen.update(delta);
    }

    @Override
    public void render() {
        FramePool.get().clear();
        update(Time.delta);

        ScreenUtils.clear(Color.DARK_GRAY);
        if (Transition.inProgress()) {
            Transition.render(assets.batch);
        } else {
            currentScreen.renderOffscreenBuffers(assets.batch);
            currentScreen.render(Time.delta);
        }

    }

    @Override
    public void setScreen(Screen screen) {
        if (screen instanceof BaseScreen) {
            setScreen((BaseScreen) screen);
        }
    }

    @Override
    public Screen getScreen() {
        return currentScreen;
    }

    public void setScreen(BaseScreen newScreen) {
        setScreen(newScreen, null, false);
    }

    public void setScreen(BaseScreen newScreen, EffectType effectType) {
        setScreen(newScreen, effectType, false);
    }

    public void setScreen(BaseScreen newScreen, EffectType effectType, boolean instant) {
        if (currentScreen != null) {
            currentScreen.hide();
        }

        // nothing to transition from, just set the current screen
        if (currentScreen == null) {
            currentScreen = newScreen;
            currentScreen.show();
            currentScreen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            return;
        }

        // only one transition allowed at a time
        if (Transition.inProgress()) {
            return;
        }

        Transition.to(newScreen, effectType, instant);
    }
}
