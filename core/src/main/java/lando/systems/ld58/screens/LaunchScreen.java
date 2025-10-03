package lando.systems.ld58.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.tommyettinger.textra.Font;
import com.github.tommyettinger.textra.Layout;
import lando.systems.ld58.Main;
import lando.systems.ld58.assets.FontType;
import lando.systems.ld58.utils.FramePool;

public class LaunchScreen extends BaseScreen {

    private final Font font;
    private final Layout layout;

    public LaunchScreen() {
        this.font = FontType.ROUNDABOUT.font();
        this.layout = new Layout(font);

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
