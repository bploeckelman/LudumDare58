package lando.systems.ld58.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;
import lando.systems.ld58.assets.ImageType;
import lando.systems.ld58.utils.FramePool;
import lando.systems.ld58.utils.Util;

public class FlashbackScreen extends BaseScreen {

    private final Color backgroundColor = new Color(0x111111ff);
    private float skipTimer = 0;
    private enum FlashbackStage {WIFE_LEAVES, MUSHROOM, MARIO, SANCTUM}
    private FlashbackStage currentStage;

    public FlashbackScreen() {
        currentStage = FlashbackStage.WIFE_LEAVES;
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            skipTimer += delta;
        } else {
            skipTimer = 0;
        }

        if (skipTimer >= 1 && !transitioning) {
            transitioning = true;
            game.setScreen(new IntroScreen());
        }

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(backgroundColor);

        batch.enableBlending();
        batch.setProjectionMatrix(windowCamera.combined);
        batch.begin();
        var pos = FramePool.vec2(
            (windowCamera.viewportWidth - layout.getWidth()) / 2f,
            windowCamera.viewportHeight - layout.getHeight());
        font.drawGlyphs(batch, layout, pos.x, pos.y);
        batch.end();
    }
}
