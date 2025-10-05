package lando.systems.ld58.screens;

import aurelienribon.tweenengine.*;
import aurelienribon.tweenengine.equations.Linear;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.tommyettinger.textra.Font;
import com.github.tommyettinger.textra.Layout;
import com.github.tommyettinger.textra.TextraLabel;
import com.github.tommyettinger.textra.TypingLabel;
import lando.systems.ld58.Config;
import lando.systems.ld58.assets.AnimType;
import lando.systems.ld58.assets.FontType;
import lando.systems.ld58.assets.ImageType;
import lando.systems.ld58.flashback.FlashbackObject;
import lando.systems.ld58.utils.FramePool;
import lando.systems.ld58.utils.Util;
import lando.systems.ld58.utils.accessors.RectangleAccessor;

public class FlashbackScreen extends BaseScreen {

    private final Color backgroundColor = new Color(0x111111ff);
    private final FrameBuffer fbo;
    private Texture screenTexture;
    private float skipTimer = 0;
    private static final float SKIP_TIME = .8f;
    private float deBounce = .2f;
    private enum FlashbackStage {PRESENT_DAY, WIFE_LEAVES, MUSHROOM, MARIO, SANCTUM, EXIT}
    private FlashbackStage currentStage;

    private Texture background;
    private Array<FlashbackObject> objects = new Array<>();
    private Array<String> messages =  new Array<>();
    public MutableFloat saturation = new MutableFloat(0);

    public Layout layout = new Layout();
    public Font font;
    public TypingLabel dialog;

    public FlashbackScreen() {
        font = FontType.COUSINE.font("large");
        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, Config.window_width, Config.window_height, false);
        screenTexture = fbo.getColorBufferTexture();

        currentStage = FlashbackStage.PRESENT_DAY;
        worldCamera.setToOrtho(false, 20, 15);
        dialog = new TypingLabel("", font);
        dialog.setWrap(true);
        dialog.setAlignment(Align.center);
        dialog.setBounds(200, Config.window_height/2f, Config.window_width - 400, Config.window_height/3f);

        setUpPhase();
    }

    public void setUpPhase() {
        dialog.restart("");
        switch(currentStage) {
            case PRESENT_DAY:
                background = ImageType.BEDROOM.get();
                messages.add("It was 10 years ago");
                messages.add("I remember it all fondly");
                Rectangle billyBounds = new Rectangle(14, 2, 1, 1);
                var billy = new FlashbackObject(AnimType.YOUNG_BILLY_NORMAL.get(), billyBounds);
                Tween.to(billy.bounds, RectangleAccessor.X, 3f)
                        .target(17).repeatYoyo(100, 1f).start(tween);
                objects.add(billy);
                break;
            case WIFE_LEAVES:
                deBounce = .5f;
                Timeline.createSequence()
                        .pushPause(.5f)
                    .push(Tween.call((type, source) -> {
                        messages.add("Misty left with the kids");
                        messages.add("I didn't know how I would keep going.");

                        dialog.restart(messages.get(0));
                        messages.removeIndex(0);
                    }))
                        .push(Tween.to(saturation, 1, .5f).target(.8f).ease(Linear.INOUT))
                    .start(tween);
                break;
            case MUSHROOM:
                objects.clear();
                worldCamera.position.x = 25;
                worldCamera.update();
                background = ImageType.FLASHBACK_MUSHROOM.get();
                break;
            case MARIO:
                background = ImageType.FLASHBACK_MARIOS.get();
                break;
            case SANCTUM:
                background = ImageType.CADRE_ROOM.get();
                break;
            case EXIT:
                break;
        }
        if (messages.size > 0) {
            dialog.restart(messages.get(0));
            messages.removeIndex(0);
        }
    }

    public void nextStage() {
        switch (currentStage) {
            case PRESENT_DAY:
                currentStage = FlashbackStage.WIFE_LEAVES;
                break;
            case WIFE_LEAVES:
                currentStage = FlashbackStage.MUSHROOM;
                break;
            case MUSHROOM:
                currentStage = FlashbackStage.MARIO;
                break;
            case MARIO:
                currentStage = FlashbackStage.SANCTUM;
                break;
            case SANCTUM:
                currentStage = FlashbackStage.EXIT;
                break;
            case EXIT:
                transitioning = true;
                game.setScreen(new IntroScreen());
                break;
        }

        setUpPhase();
    }

    @Override
    public void update(float delta) {
        deBounce = MathUtils.clamp(deBounce-delta, 0, 1f);
        super.update(delta);
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            skipTimer += delta;
        } else {
            skipTimer = 0;
        }

        if (skipTimer >= SKIP_TIME && !transitioning) {
            transitioning = true;
            game.setScreen(new IntroScreen());
        }

        for (FlashbackObject object : objects) {
            object.update(delta);
        }

        dialog.act(delta);
        if (Gdx.input.justTouched() && deBounce <= 0) {
            if (!dialog.hasEnded()) {
                dialog.skipToTheEnd();
            } else {
                if (messages.size > 0) {
                    dialog.restart(messages.get(0));
                    messages.removeIndex(0);
                } else {
                    nextStage();
                }
            }
        }

    }

    @Override
    public void renderOffscreenBuffers(SpriteBatch batch) {
        fbo.begin();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(worldCamera.combined);
        batch.begin();
        batch.draw(background, 0, 0, background.getWidth()/16f, background.getHeight()/16f);
        for (FlashbackObject object : objects) {
            object.render(batch);
        }
        batch.end();
        fbo.end();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(backgroundColor);

        var shader = assets.flashbackShader;
        batch.setShader(shader);
        batch.setProjectionMatrix(windowCamera.combined);
        batch.begin();
        shader.setUniformf("u_time", 1f);
        shader.setUniformf("u_res", Config.window_width, Config.window_height);
        shader.setUniformf("u_fade", 1f);
        shader.setUniformf("u_saturation", saturation.floatValue());
        batch.draw(screenTexture, 0, screenTexture.getHeight(), screenTexture.getWidth(), -screenTexture.getHeight());
        batch.end();
        batch.setShader(null);


        // UI

        batch.enableBlending();
        batch.setProjectionMatrix(windowCamera.combined);
        batch.begin();
        dialog.draw(batch, 1f);
        var pos = FramePool.vec2(
            (windowCamera.viewportWidth - layout.getWidth()) / 2f,
            windowCamera.viewportHeight - layout.getHeight());
        font.drawGlyphs(batch, layout, pos.x, pos.y);

        batch.end();
        var progressShader = assets.progressShader;
        batch.setShader(progressShader);
        batch.begin();
        progressShader.setUniformf("u_percent", MathUtils.clamp(skipTimer / SKIP_TIME, 0f, 1f));
        batch.draw(ImageType.SKIP.get(), Config.window_width - 260, 10, 250, 60);
        batch.end();
        batch.setShader(null);
    }
}
