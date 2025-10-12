package lando.systems.ld58.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import lando.systems.ld58.utils.FramePool;

public class LaunchScreen extends BaseScreen {

    // TODO: use TypingLabel instead of direct font rendering

    public LaunchScreen() {
        layout.setTargetWidth(windowCamera.viewportWidth);
        font.markup("Click to Begin", layout);
        font.regenerateLayout(layout);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if (!transitioning && Gdx.input.justTouched()){
            transitioning = true;
            game.setScreen(new TitleScreen());
        }
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.DARK_GRAY);

        batch.setProjectionMatrix(windowCamera.combined);
        batch.begin();
        var center = FramePool.vec2(
            (windowCamera.viewportWidth - layout.getWidth()) / 2f,
            (windowCamera.viewportHeight - layout.getHeight()) / 2f);
        font.drawGlyphs(batch, layout, center.x, center.y);
        batch.end();
    }
}
