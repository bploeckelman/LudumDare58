package lando.systems.ld58.screens;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import lando.systems.ld58.assets.AnimType;
import lando.systems.ld58.assets.EffectType;
import lando.systems.ld58.game.Signals;
import lando.systems.ld58.game.signals.AudioEvent;
import lando.systems.ld58.utils.accessors.Vector2Accessor;

public class TitleScreen extends BaseScreen {

    private final Color backgroundColor = new Color(0x121212ff);

    private final TextureRegion pixel;
    private final TextureRegion background;
    private final TextureRegion curtains;
    private final TextureRegion title2015;
    private final TextureRegion title2025;
    private final TextureRegion title2025Glow;
    private final TextureRegion billyOldStatic;
    private final TextureRegion billyNewStatic;

    private final MutableFloat morphAmount = new MutableFloat(0);
    private final MutableFloat pixelOverlayAlpha = new MutableFloat(1f);
    private final MutableFloat title2015Alpha = new MutableFloat(0);
    private final MutableFloat title2025Alpha = new MutableFloat(0);
    private final MutableFloat title2025GlowAlpha = new MutableFloat(0);

    private final Vector2 titlePosTarget = new Vector2();
    private final Vector2 billyPosTarget = new Vector2();
    private final Vector2 billyPos = new Vector2();

    private final AnimType billyOldWalkAnimType = AnimType.TITLE_BILLY_WALK;
    private final AnimType billyMorphAnimType = AnimType.TITLE_BILLY_MORPH;

    private float animTime = 0;

    private boolean showOldTitle = false;
    private boolean showNewTitle = false;
    private boolean showGlowTitle = false;
    private boolean walkComplete = false;
    private boolean morphStarted = false;
    private boolean morphComplete = false;
    private boolean drawUI = false;

    public TitleScreen() {
        var atlas = assets.atlas;
        this.pixel          = assets.pixelRegion;
        this.background     = atlas.findRegion("title/title-screen-background", 0);
        this.curtains       = atlas.findRegion("title/title-screen-curtains", 0);
        this.title2015      = atlas.findRegion("title/title-screen-text-2015", 0);
        this.title2025      = atlas.findRegion("title/title-screen-text-2025", 0);
        this.title2025Glow  = atlas.findRegion("title/title-screen-text-2025-glow-edge", 0);
        this.billyOldStatic = atlas.findRegion("title/title-big-billy-old-static", 0);
        this.billyNewStatic = atlas.findRegion("title/title-big-billy-new-static", 0);

        float morphDuration = AnimType.TITLE_BILLY_MORPH.get().getAnimationDuration();
        billyPosTarget.set(620, 50);
        billyPos.set(1100, 50); // start inside the curtain a bit
        titlePosTarget.set(310, 333);//587);

        initializeUI();

        Timeline.createSequence()
            .delay(0.1f)
            // black overlay fades off to reveal background and curtains
            .push(Tween.to(pixelOverlayAlpha, -1, 1f).target(0f))
            .pushPause(0.1f)
            // walk old billy onscreen, then set flag to show static 'old' frame and old title text
            .push(Tween.call((type, source) -> showOldTitle = true))
            .push(
                Timeline.createParallel()
                    .push(Tween.to(title2015Alpha, -1, 1.5f).target(1f))
                    .push(Tween.to(billyPos, Vector2Accessor.XY, 3f)
                        .target(billyPosTarget.x, billyPosTarget.y))
            )
            .push(Tween.call((type, source) -> walkComplete = true))
            .pushPause(0.25f)
            // morph old -> new billy, then set flag to show static 'new' frame and new title text
            .push(Tween.call((type, source) -> {
                morphStarted = true;
                showNewTitle = true;
                animTime = 0;
            }))
            .push(
                Timeline.createParallel()
                    .push(Tween.to(title2015Alpha, -1, 1.5f).target(0f))
                    .push(Tween.to(title2025Alpha, -1, 1.5f).target(1f))
                    .push(Tween.to(billyPos, Vector2Accessor.XY, 3f)
                        .target(billyPosTarget.x, billyPosTarget.y))
                    .push(Tween.to(morphAmount, -1, morphDuration).target(billyMorphAnimType.get().getAnimationDuration()))
            )
            .push(Tween.call((type, source) -> {
                morphComplete = true;
                showGlowTitle = true;
                showOldTitle = false;
            }))
            .push(Tween.to(title2025GlowAlpha, -1, 1f).target(0f))
            .pushPause(0.3f)
            .push(Tween.call((type, source) -> drawUI = true))
            .start(tween);
    }

    @Override
    public void initializeUI() {
        Gdx.input.setInputProcessor(uiStage);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if (!transitioning && Gdx.input.justTouched()){
            transitioning = true;
            AudioEvent.stopAllMusic();
            // TODO: flashback is broken until I get FontType2 sorted... come back to it
//            game.setScreen(new FlashbackScreen(), EffectType.DREAMY);
            game.setScreen(new IntroScreen(), EffectType.DREAMY);
        }

        animTime += delta;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(backgroundColor);

        var winWidth = windowCamera.viewportWidth;
        var winHeight = windowCamera.viewportHeight;

        batch.setProjectionMatrix(windowCamera.combined);
        batch.begin();
        {
            batch.draw(background, 0, 0, winWidth, winHeight);

            if (!walkComplete) {
                // Draw walk
                var anim = billyOldWalkAnimType.get();
                var frame = anim.getKeyFrame(animTime);
                batch.draw(frame, billyPos.x, billyPos.y);
            } else { // Walk complete...
//                if (!morphStarted) {
//                    // Waiting to start morph so show static
//                    batch.draw(billyOldStatic, billyPos.x, billyPos.y);
//                } else {
                    // Morph started..
                        var anim = billyMorphAnimType.get();
                        var frame = anim.getKeyFrame(morphAmount.floatValue());
                        batch.draw(frame, billyPos.x, billyPos.y);
//                }
            }

                batch.setColor(1, 1, 1, title2015Alpha.floatValue());
                batch.draw(title2015, titlePosTarget.x, titlePosTarget.y);
                batch.setColor(Color.WHITE);


            if (showGlowTitle) {
                batch.setColor(1, 1, 1, title2025GlowAlpha.floatValue());
                batch.draw(title2025Glow, titlePosTarget.x, titlePosTarget.y);
                batch.setColor(Color.WHITE);
            }
            if (showNewTitle) {
                batch.setColor(1, 1, 1, title2025Alpha.floatValue());
                batch.draw(title2025, titlePosTarget.x, titlePosTarget.y);
                batch.setColor(Color.WHITE);
            }

            batch.draw(curtains, 0, 0, winWidth, winHeight);

            // Draw black overlap that fades off
            batch.setColor(0, 0, 0, pixelOverlayAlpha.floatValue());
            batch.draw(pixel, 0, 0, winWidth, winHeight);
            batch.setColor(Color.WHITE);
        }
        batch.end();

        if (drawUI) {
            uiStage.draw();
        }
    }
}
