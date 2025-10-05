package lando.systems.ld58.screens;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Bounce;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import lando.systems.ld58.assets.EffectType;
import lando.systems.ld58.assets.ImageType;
import lando.systems.ld58.utils.FramePool;
import lando.systems.ld58.utils.Util;
import lando.systems.ld58.utils.accessors.RectangleAccessor;

public class TitleScreen extends BaseScreen {

    private final Color backgroundColor = new Color(0x121212ff);
    private final TextureRegion logo;
    private final Rectangle logoBounds;

    private boolean drawUI = false;

    public TitleScreen() {
        this.logo = new TextureRegion(ImageType.GDX.get());
        this.logoBounds = new Rectangle(
            windowCamera.viewportWidth,
            (windowCamera.viewportHeight - logo.getRegionHeight()) / 2f,
            logo.getRegionWidth(), logo.getRegionHeight());

        initializeUI();

        var logoTarget = new Rectangle(
            (windowCamera.viewportWidth - logo.getRegionWidth()) / 2f,
            (windowCamera.viewportHeight - logo.getRegionHeight()) / 2f,
            logo.getRegionWidth(), logo.getRegionHeight());

        Timeline.createSequence()
            .delay(.1f)
            .push(
                Tween.to(logoBounds, RectangleAccessor.XYWH, 1f).ease(Bounce.OUT)
                    .target(logoTarget.x, logoTarget.y, logoTarget.width, logoTarget.height)
            )
            .pushPause(.1f)
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
            game.setScreen(new FlashbackScreen(), EffectType.DREAMY);
        }
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(backgroundColor);

        batch.setProjectionMatrix(windowCamera.combined);
        batch.begin();
        Util.draw(batch, logo, logoBounds);
        var pos = FramePool.vec2(
            (windowCamera.viewportWidth - layout.getWidth()) / 2f,
            windowCamera.viewportHeight - layout.getHeight());
        font.drawGlyphs(batch, layout, pos.x, pos.y);
        batch.end();

        if (drawUI) {
            uiStage.draw();
        }
    }
}
