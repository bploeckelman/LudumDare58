package lando.systems.ld58.screens;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Linear;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.MathUtils;
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
import lando.systems.ld58.utils.accessors.ColorAccessor;
import lando.systems.ld58.utils.accessors.RectangleAccessor;
import lando.systems.ld58.utils.accessors.Vector3Accessor;

public class FlashbackScreen extends BaseScreen {

    private final Color backgroundColor = new Color(0x111111ff);
    private final FrameBuffer fbo;
    private Texture screenTexture;
    float accum;
    private float skipTimer = 0;
    private static final float SKIP_TIME = .8f;
    private float deBounce = .2f;
    private enum FlashbackStage {PRESENT_DAY, WIFE_LEAVES, MUSHROOM, MARIO, SANCTUM, EXIT}
    private FlashbackStage currentStage;

    private Texture background;
    private Array<FlashbackObject> objects = new Array<>();
    private Array<String> messages =  new Array<>();
    public MutableFloat flashback = new MutableFloat(0);
    private boolean drawParticles = false;

    public Layout layout = new Layout();
    public Font font;
    public TypingLabel dialog;

    public FlashbackScreen() {
        drawParticles = false;
        font = FontType.ROUNDABOUT.font("large");
        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, Config.window_width, Config.window_height, false);
        screenTexture = fbo.getColorBufferTexture();

        currentStage = FlashbackStage.PRESENT_DAY;
        worldCamera.setToOrtho(false, 20, 15);
        dialog = new TypingLabel("", font);
        dialog.setWrap(true);
        dialog.setAlignment(Align.center);
        dialog.setBounds(200, Config.window_height/2f, Config.window_width - 400, Config.window_height/3f);
        Signals.playMusic.dispatch(new AudioEvent.PlayMusic(MusicType.DIRGE, 0.25f));
        setUpPhase();
    }

    public void setUpPhase() {
        messages.clear();
        dialog.restart("");
        switch(currentStage) {
            case PRESENT_DAY:
                background = ImageType.BEDROOM.get();
                messages.add("Wow... 10 years already?");
                messages.add("Feels like it was only yesterday...");
                var billy = new FlashbackObject(AnimType.YOUNG_BILLY_NORMAL.get(),  new Rectangle(14, 2, 1, 1));
                Tween.to(billy.bounds, RectangleAccessor.X, 3f)
                        .target(17).repeatYoyo(100, 1f).start(tween);
                objects.add(billy);
                break;
            case WIFE_LEAVES:
                deBounce = .5f;
                var misty = new FlashbackObject(AnimType.MISTY.get(),   new Rectangle(6, 2, 1, 1));
                misty.tintColor.a = 0;
                objects.add(misty);
                var kids = new FlashbackObject(AnimType.KIDS.get(),    new Rectangle(8, 2, 1, 1));
                kids.tintColor.a = 0;
                Tween.to(kids.bounds, RectangleAccessor.Y, .3f).target(3).repeatYoyo(1000, .1f).start(tween);
                objects.add(kids);
                Timeline.createSequence()
                        .pushPause(.5f)
                    .push(Tween.call((type, source) -> {
                        messages.add("When Misty left with the kids, I thought my life was over");
//                        messages.add("I thought my life was over.");
                        messages.add("I didn't realize it was only the beginning!");

                        dialog.restart(messages.get(0));
                        messages.removeIndex(0);
                    }))
                    .beginParallel()
                        .push(Tween.to(flashback, 1, .5f).target(1f).ease(Linear.INOUT))
                        .push(Tween.to(misty.tintColor, ColorAccessor.A, .5f).target(1f).ease(Linear.INOUT))
                        .push(Tween.to(kids.tintColor, ColorAccessor.A, .5f).target(1f).ease(Linear.INOUT))
                    .end()
                    .pushPause(.5f)
                    .beginParallel()
                        .push(Tween.to(misty.bounds, RectangleAccessor.X, 5f).target(-3f).ease(Linear.INOUT))
                        .push(Tween.to(kids.bounds, RectangleAccessor.X, 5f).target(-1f).ease(Linear.INOUT))

                    .end()
                    .start(tween);
                break;
            case MUSHROOM:
                backgroundColor.set(107/255f, 140/255f, 255/255f, 1.0f);
                placeObjectsInMushroomWorld();
                worldCamera.position.x = 25;
                background = ImageType.FLASHBACK_MUSHROOM.get();
                messages.add("That mushroom changed my whole perspective");
                messages.add("I got Super powers...");
                messages.add("I could JUMP...");
                messages.add("Saw things I could never have imagined.");
                messages.add("Places...");
                messages.add("Secrets...");
                break;
            case MARIO:
                drawParticles = false;
                placeObjectsInMarioWorld();
                backgroundColor.set(.25f, .25f, .25f, 1.0f);
                worldCamera.position.x = 84;
                billy = new FlashbackObject(AnimType.YOUNG_BILLY_NORMAL.get(),  new Rectangle(86, 2, 1, 1));

                objects.add(billy);
                background = ImageType.FLASHBACK_MARIOS.get();

                Timeline.createSequence().pushPause(.5f)
                    .push(Tween.call((type, source) -> {
                        messages.add("Later, I found a strange factory");
                        messages.add("They were creating clones of this person");
                        messages.add("I had to go deeper to learn what was happening");

                        dialog.restart(messages.get(0));
                        messages.removeIndex(0);
                    }))
                    .beginParallel()
                       .push(Tween.to(worldCamera.position, Vector3Accessor.X, 15f).target(20))
                       .push(Tween.to(billy.bounds, RectangleAccessor.X, 15f).target(22))

                    .end()
                    .pushPause(5f)
                    .push(Tween.call((type, source) -> {nextStage();}))
                    .start(tween);
                break;
            case SANCTUM:
                objects.clear();
                tween.killAll();
                objects.add(new FlashbackObject(AnimType.GANNON.get(), new Rectangle(.5f, 2, 2, 4)));
                objects.add(new FlashbackObject(AnimType.HIPPO.get(), new Rectangle(2.5f, 2, 2, 4)));
                objects.add(new FlashbackObject(AnimType.GOOMBA_CAPE.get(),  new Rectangle(5f, 2, 2, 2)));
                objects.add(new FlashbackObject(AnimType.MOTHER_BRAIN.get(), new Rectangle(6, 2, 8, 4)));
                objects.add(new FlashbackObject(AnimType.DRACULA.get(), new Rectangle(13, 2, 2, 4)));
                objects.add(new FlashbackObject(AnimType.LUIGI.get(), new Rectangle(16, 2, 2, 4)));
                objects.add(new FlashbackObject(AnimType.WILLY.get(), new Rectangle(18, 2, 2, 4)));

                backgroundColor.set(330/255f, 20/255f, 60/255f, 1.0f);
                worldCamera.position.x = 10;
                background = ImageType.CADRE_ROOM.get();
                messages.add(
                    "A cabal of boss enemies from across a wide range of game franchises");
                messages.add(
                    "{Size=90%}Keeping the Mushroom Kingdom in balance by growing and then releasing a near-endless army of Marios into our ecosystem...");
                messages.add(
                    "Which is a premise so absurd it could only have come out of Ludum Dare 33");
                messages.add(
                    "{Size=70%}Where the theme \"you are the monster\" inspired a plucky team of developers to create a game where you are a goomba whose wife left him, inspiring him toward adventure");
                messages.add("Anyway, I was that goomba.");
                messages.add("I ate a mushroom, hijinks ensued, and I ended up " +
                    "getting inducted into the cabal.");
                messages.add("But that was a long time ago. \n\nMust have been, what...");
                messages.add("Wow... 10 years already?");
                break;
            case EXIT:
                deBounce = 1.5f;
                Timeline.createSequence().pushPause(.5f)
                    .push(Tween.to(flashback,1, .5f).target(0))
                    .push(Tween.call((type, source) -> {
                        messages.add("Now that we are back in the present.");
                        messages.add("Things aren't as good as I thought they should be.");

                        dialog.restart(messages.get(0));
                        messages.removeIndex(0);
                    }))
                    .start(tween);
                break;
        }
        worldCamera.update();
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
                Signals.stopMusic.dispatch(new AudioEvent.StopMusic());
                game.setScreen(new IntroScreen(), EffectType.DREAMY);
                break;
        }

        setUpPhase();
    }

    @Override
    public void update(float delta) {
        accum += delta;
        deBounce = MathUtils.clamp(deBounce-delta, 0, 1f);
        super.update(delta);
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            skipTimer += delta;
        } else {
            skipTimer = 0;
        }

        if (skipTimer >= SKIP_TIME && !transitioning) {
            transitioning = true;
            Signals.stopMusic.dispatch(new AudioEvent.StopMusic());
            game.setScreen(new IntroScreen());
        }

        for (FlashbackObject object : objects) {
            object.update(delta);
        }
        if (drawParticles) {
            updateParticleFountain(delta);
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
        for (FlashbackObject object : objects) {
            batch.setColor(object.tintColor);
            object.render(batch);
        }
        if (drawParticles) {
            for (FlashbackParticle particle : particles) {
                particle.render(batch);
            }
        }
        batch.setColor(Color.WHITE);
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
        shader.setUniformf("u_time", accum);
        shader.setUniformf("u_res", Config.window_width, Config.window_height);
        shader.setUniformf("u_fade", 1f);
        shader.setUniformf("u_flashback", flashback.floatValue());
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

        batch.setColor(0,0,0,.5f);
        batch.draw(assets.pixel, Config.window_width - 260, 10, 250, 60);
        batch.setColor(Color.WHITE);
        batch.end();
        var progressShader = assets.progressShader;
        batch.setShader(progressShader);
        batch.begin();
        progressShader.setUniformf("u_percent", MathUtils.clamp(skipTimer / SKIP_TIME, 0f, 1f));
        batch.draw(ImageType.SKIP.get(), Config.window_width - 260, 10, 250, 60);
        batch.end();
        batch.setShader(null);
    }

    private void drawDialog(SpriteBatch batch, float delta) {
        if (!dialog.getOriginalText().toString().isEmpty()) {
            batch.setColor(0, 0, 0, .4f);
            batch.draw(assets.pixel, dialog.getX()- 5, dialog.getY()-5, dialog.getWidth()+10, dialog.getHeight()+10);
            batch.setColor(Color.WHITE);
        }
        dialog.draw(batch, 1f);
    }

    Array<FlashbackParticle> particles = new Array<>();
    private void updateParticleFountain(float delta) {
        for (int i = 0; i < 30; i++) {
            particles.add(new FlashbackParticle());
        }

        for (int i = particles.size-1; i >= 0; i--) {
            var particle = particles.get(i);
            particle.update(delta);

            if (particle.ttl < 0) particles.removeIndex(i);
        }
    }

    private void placeObjectsInMushroomWorld() {
        objects.clear();
        objects.add(new FlashbackObject(AnimType.COIN_BLOCK.get(), new Rectangle(27, 9, 1, 1)));
        objects.add(new FlashbackObject(AnimType.COIN_BLOCK.get(), new Rectangle(28, 5, 1, 1)));
        objects.add(new FlashbackObject(AnimType.COIN_BLOCK.get(), new Rectangle(26, 5, 1, 1)));
        objects.add(new FlashbackObject(AnimType.COIN_BLOCK.get(), new Rectangle(21, 5, 1, 1)));
        var billy = new FlashbackObject(AnimType.YOUNG_BILLY_NORMAL.get(),  new Rectangle(27, 2, 1, 1));
        objects.add(billy);
        var mushroom = new FlashbackObject(AnimType.MUSHROOM.get(),  new Rectangle(32, 2, 1, 1));
        objects.add(mushroom);
        Timeline.createSequence()
            .beginParallel()
                .push(Tween.to(billy.bounds, RectangleAccessor.X, 3f).target(25).ease(Linear.INOUT))
                .push(Tween.to(mushroom.bounds, RectangleAccessor.X, 3f).target(25.8f).ease(Linear.INOUT))
            .end()
            .push(Tween.to(mushroom.tintColor, ColorAccessor.A, .05f).target(0))
            .push(Tween.call((type, source) -> {
                billy.animation = AnimType.YOUNG_BILLY_RAGE.get();
                drawParticles = true;
            }))
            .start(tween);

    }

    private void placeObjectsInMarioWorld() {
        objects.clear();
        objects.add(new FlashbackObject(AnimType.MARIO_TUBE_SMALL.get(), new Rectangle(64, 5, 1, 2)));
        objects.add(new FlashbackObject(AnimType.MARIO_TUBE_SMALL.get(), new Rectangle(66, 5, 1, 2)));
        objects.add(new FlashbackObject(AnimType.MARIO_TUBE_SMALL.get(), new Rectangle(68, 5, 1, 2)));
        objects.add(new FlashbackObject(AnimType.MARIO_TUBE_LARGE.get(), new Rectangle(70, 5, 1, 2)));
        objects.add(new FlashbackObject(AnimType.MARIO_TUBE_LARGE.get(), new Rectangle(72, 5, 1, 2)));
        objects.add(new FlashbackObject(AnimType.MARIO_TUBE_LARGE.get(), new Rectangle(74, 5, 1, 2)));

        objects.add(new FlashbackObject(AnimType.MARIO_EMBRYO.get(), new Rectangle(50, 5, 1, 2)));
        objects.add(new FlashbackObject(AnimType.MARIO_EMBRYO.get(), new Rectangle(52, 5, 1, 2)));
        objects.add(new FlashbackObject(AnimType.MARIO_EMBRYO.get(), new Rectangle(54, 5, 1, 2)));
        objects.add(new FlashbackObject(AnimType.MARIO_EMBRYO.get(), new Rectangle(56, 5, 1, 2)));
        objects.add(new FlashbackObject(AnimType.MARIO_EMBRYO.get(), new Rectangle(58, 5, 1, 2)));
        objects.add(new FlashbackObject(AnimType.MARIO_EMBRYO.get(), new Rectangle(60, 5, 1, 2)));

        objects.add(new FlashbackObject(AnimType.MARIO_SPINE.get(), new Rectangle(36, 5, 1, 2)));
        objects.add(new FlashbackObject(AnimType.MARIO_SPINE.get(), new Rectangle(38, 5, 1, 2)));
        objects.add(new FlashbackObject(AnimType.MARIO_SPINE.get(), new Rectangle(40, 5, 1, 2)));
        objects.add(new FlashbackObject(AnimType.MARIO_SPINE.get(), new Rectangle(42, 5, 1, 2)));
        objects.add(new FlashbackObject(AnimType.MARIO_SPINE.get(), new Rectangle(44, 5, 1, 2)));
        objects.add(new FlashbackObject(AnimType.MARIO_SPINE.get(), new Rectangle(46, 5, 1, 2)));

        objects.add(new FlashbackObject(AnimType.MARIO_SCREEN.get(),  new Rectangle(78, 5, 2, 2)));
        objects.add(new FlashbackObject(AnimType.MARIO_SCREEN.get(),  new Rectangle(81, 5, 2, 2)));
        objects.add(new FlashbackObject(AnimType.MARIO_SCREEN.get(),  new Rectangle(84, 5, 2, 2)));
        objects.add(new FlashbackObject(AnimType.MARIO_SCREEN.get(),  new Rectangle(87, 5, 2, 2)));
    }
}
