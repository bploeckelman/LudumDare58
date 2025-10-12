package lando.systems.ld58.screens;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquations;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.tommyettinger.textra.Font;
import com.github.tommyettinger.textra.Layout;
import com.github.tommyettinger.textra.TypingLabel;
import lando.systems.ld58.Config;
import lando.systems.ld58.assets.*;
import lando.systems.ld58.flashback.FlashbackObject;
import lando.systems.ld58.flashback.FlashbackParticle;
import lando.systems.ld58.game.Signals;
import lando.systems.ld58.game.signals.AudioEvent;
import lando.systems.ld58.utils.FramePool;
import lando.systems.ld58.utils.accessors.RectangleAccessor;

public class EndingScreen extends BaseScreen {

    public enum PHASE {START, THROWING, DESTROY_LOU}
    private final Color backgroundColor = new Color(0x222255ff);
    private boolean completed = false;
    private final FrameBuffer fbo;
    private Texture screenTexture;
    float accum;
    public float deBounce;

    private Texture background;
    private Array<FlashbackObject> objects = new Array<>();
    private boolean drawParticles = false;
    private Array<String> messages =  new Array<>();

    public Layout layout = new Layout();
    public Font font;
    public TypingLabel dialog;
    Array<FlashbackParticle> particles = new Array<>();
    PHASE currentPhase = PHASE.START;


    public EndingScreen() {
        worldCamera.setToOrtho(false, 20, 15);
        font = FontType.ROUNDABOUT.font("large");
        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, Config.window_width, Config.window_height, false);
        screenTexture = fbo.getColorBufferTexture();
        backgroundColor.set(330/255f, 20/255f, 60/255f, 1.0f);
        worldCamera.position.x = 10;
        background = ImageType.CADRE_ROOM.get();
        dialog = new TypingLabel("", font);
        dialog.setWrap(true);
        dialog.setAlignment(Align.center);
        dialog.setBounds(200, Config.window_height/2f, Config.window_width - 400, Config.window_height/3f);

        dialog.restart("");
        currentPhase = PHASE.START;
        AudioEvent.stopAllMusic();
        AudioEvent.playMusic(MusicType.MAIN_THEME, 0.5f);
        setupPhase();
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        accum += delta;
        deBounce -= delta;

        for (FlashbackObject object : objects) {
            object.update(delta);
        }
        dialog.act(delta);
        if ((Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY) || Gdx.input.justTouched()) && deBounce <= 0) {
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
        ScreenUtils.clear(backgroundColor);
        batch.setProjectionMatrix(worldCamera.combined);
        batch.begin();
        batch.draw(background, 0, 0, background.getWidth()/16f, background.getHeight()/16f);

        if (drawParticles) {
            for (FlashbackParticle particle : particles) {
                particle.render(batch);
            }
        }
        for (FlashbackObject object : objects) {
            batch.setColor(object.tintColor);
            object.render(batch);
        }
        batch.setColor(Color.WHITE);
        batch.draw(ImageType.CADRE_FLOOR.get(), 0, 0, background.getWidth()/16f, background.getHeight()/16f );
        batch.end();
        fbo.end();
    }

     @Override
    public void render(float delta) {
        ScreenUtils.clear(backgroundColor);

        batch.setProjectionMatrix(windowCamera.combined);
        batch.begin();
        batch.draw(screenTexture, 0, screenTexture.getHeight(), screenTexture.getWidth(), -screenTexture.getHeight());
        batch.end();
        batch.setShader(null);


        // UI

        batch.enableBlending();
        batch.setProjectionMatrix(windowCamera.combined);
        batch.begin();
        drawDialog(batch, delta);
        var pos = FramePool.vec2(
            (windowCamera.viewportWidth - layout.getWidth()) / 2f,
            windowCamera.viewportHeight - layout.getHeight());
        font.drawGlyphs(batch, layout, pos.x, pos.y);

//        batch.setColor(0,0,0,.5f);
//        batch.draw(assets.pixel, Config.window_width - 260, 10, 250, 60);
//        batch.setColor(Color.WHITE);
//        batch.end();
//        var progressShader = assets.progressShader;
//        batch.setShader(progressShader);
//        batch.begin();
//        progressShader.setUniformf("u_percent", MathUtils.clamp(skipTimer / SKIP_TIME, 0f, 1f));
//        batch.draw(ImageType.SKIP.get(), Config.window_width - 260, 10, 250, 60);
        batch.end();
        batch.setShader(null);
        batch.setColor(Color.WHITE);
    }


    private void drawDialog(SpriteBatch batch, float delta) {
        if (!dialog.getOriginalText().toString().isEmpty()) {
            batch.setColor(0, 0, 0, .75f);
            batch.draw(assets.pixel, dialog.getX()- 5, dialog.getY()-5, dialog.getWidth()+10, dialog.getHeight()+10);
            batch.setColor(Color.WHITE);
        }
        dialog.draw(batch, 1f);
    }

    public void nextStage() {
        switch (currentPhase) {
            case START:
                currentPhase = PHASE.THROWING;
                break;
            case THROWING:
                currentPhase = PHASE.DESTROY_LOU;
                break;
            case DESTROY_LOU:
                transitioning = true;
                AudioEvent.stopAllMusic();
                game.setScreen(new CreditsScreen(), EffectType.HEART);
                break;

        }
        setupPhase();
    }

    FlashbackObject billy = new FlashbackObject(AnimType.YOUNG_BILLY_NORMAL.get(), new Rectangle(-2, 2, 1, 1));
    FlashbackObject captinLou = new FlashbackObject(AnimType.ALBANO_BOT_IDLE.get(), new Rectangle(10, -4, 9, 9));
    public void setupPhase() {
        switch (currentPhase) {
            case START:
                deBounce = 4;
                completed = false;

                objects.add(captinLou);
                objects.add(billy);
                Timeline.createSequence()
                    .push(Tween.to(billy.bounds, RectangleAccessor.X, 3f).target(5))
                    .push(Tween.call((type, source) -> {
                        dialog.restart("I collected all the relics");
                        messages.add("Time to throw them into Pluto! \n{SIZE=70%}(the secret theme of LD 33)");
                    }))

                    .push(Tween.call((type, source) -> completed = true))
                    .start(tween);
                break;
            case THROWING:
                deBounce = 6;
                dialog.restart("");
                var wrench = new FlashbackObject(AnimType.RELIC_WRENCH.get(),  new Rectangle(5, 4, 1, 1));
                var torch = new FlashbackObject(AnimType.RELIC_TORCH.get(),  new Rectangle(4, 3, 1, 1));
                var plunger = new FlashbackObject(AnimType.RELIC_PLUNGER.get(),  new Rectangle(6, 3, 1, 1));
                objects.add(wrench);
                objects.add(torch);
                objects.add(plunger);

                Timeline.createSequence()
                    .beginParallel()
                        .push(Tween.to(wrench.bounds, RectangleAccessor.XY, 2f).target(9, 9))
                        .push(Tween.to(torch.bounds, RectangleAccessor.XY, 2f).target(9, 9))
                        .push(Tween.to(plunger.bounds, RectangleAccessor.XY, 2f).target(9, 9))
                    .end()
                    .beginParallel()
                        .push(Tween.to(wrench.bounds, RectangleAccessor.WH, 1f).target(0, 0))
                        .push(Tween.to(torch.bounds, RectangleAccessor.WH, 1f).target(0, 0))
                        .push(Tween.to(plunger.bounds, RectangleAccessor.WH, 1f).target(0, 0))
                    .end()
                    .push(Tween.call((type, source) -> {
                        dialog.restart("{SIZE=70%}With those destroyed, the Mushroom Kingdom should once again be safe");
                        messages.add("Wait...");
                        messages.add("What is going on?");
                    }))
                    .start(tween);

                break;

            case DESTROY_LOU:
                dialog.restart("");
                deBounce = 6;
                Timeline.createSequence()
                    .push(Tween.to(captinLou.bounds, RectangleAccessor.Y, 1f).target(2).ease(TweenEquations.easeInOutBounce))
                    .pushPause(2f)
                    .push(Tween.to(captinLou.bounds, RectangleAccessor.XY, 2f).target(20, 20))
                    .push(Tween.call((type, source) -> {
                        dialog.restart("That boss battle was EPIC.");
                        messages.add("{SIZE=50%}Or at least, it would have been if we didn't run out of time.");
                        messages.add("Mario has been banished from our world.  Things should return to normal now.");
                        messages.add("Or...  \nAnother threat will emerge 10 years from now, in..");
                        messages.add("Goomba Simulator 2035!");
                        messages.add("Thank you for playing our game!");
                    }))
                .start(tween);
                break;

        }
    }
}
