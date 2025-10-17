package lando.systems.ld58.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.tommyettinger.textra.TypingLabel;
import lando.systems.ld58.assets.FontType;
import lando.systems.ld58.assets.SkinType;
import lando.systems.ld58.utils.FramePool;

public class LaunchScreen extends BaseScreen {

    public LaunchScreen() {
        var skin = SkinType.ZENDO.get();
        var styleName = FontType.ATKINSON_HYPERLEGIBLE.textraLabelStyleName;
        var text = "{WAVE}{RAINBOW}Click to Begin{ENDRAINBOW}{ENDWAVE}";

        var label = new TypingLabel(text, skin, styleName);
        label.setAlignment(Align.center);
        label.setFillParent(true);

        uiStage.addActor(label);
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
        uiStage.draw();
    }
}
