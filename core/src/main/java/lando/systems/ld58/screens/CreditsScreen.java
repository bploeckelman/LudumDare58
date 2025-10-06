package lando.systems.ld58.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;
import lando.systems.ld58.assets.ImageType;
import lando.systems.ld58.game.Signals;
import lando.systems.ld58.game.signals.AudioEvent;
import lando.systems.ld58.utils.FramePool;
import lando.systems.ld58.utils.Util;

public class CreditsScreen extends BaseScreen {

    private final Color backgroundColor = new Color(0x323232ff);
    private final TextureRegion gdx;

    public CreditsScreen() {
        this.gdx = new TextureRegion(ImageType.GDX.get());
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if (!transitioning && Gdx.input.justTouched()){
            transitioning = true;
            Signals.stopMusic.dispatch(new AudioEvent.StopMusic());
            game.setScreen(new TitleScreen());
        }
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(backgroundColor);

        batch.enableBlending();
        batch.setProjectionMatrix(windowCamera.combined);
        batch.begin();
        Util.draw(batch, gdx, FramePool.rect(
            (windowCamera.viewportWidth  - gdx.getRegionWidth())  / 2f,
            (windowCamera.viewportHeight - gdx.getRegionHeight()) / 2f,
            gdx.getRegionWidth(), gdx.getRegionHeight()));
        var pos = FramePool.vec2(
            (windowCamera.viewportWidth - layout.getWidth()) / 2f,
            windowCamera.viewportHeight - layout.getHeight());
        font.drawGlyphs(batch, layout, pos.x, pos.y);
        batch.end();
    }
}
