package lando.systems.ld58.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import lando.systems.ld58.Flag;
import lando.systems.ld58.assets.EmitterType;
import lando.systems.ld58.game.Factory;
import lando.systems.ld58.game.systems.ViewSystem;
import lando.systems.ld58.particles.effects.TestEffect;
import lando.systems.ld58.screens.BaseScreen;
import lando.systems.ld58.utils.FramePool;
import lando.systems.ld58.utils.Util;

public class ScreenInputHandler extends InputHandler {

    private static final String TAG = ScreenInputHandler.class.getSimpleName();

    public ScreenInputHandler(BaseScreen screen) {
        super(screen);
    }

    @Override
    public boolean keyDown(int keycode) {
        super.keyDown(keycode);
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        super.keyUp(keycode);

        var shift = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);
        if (shift && keycode == Input.Keys.ESCAPE) {
            Util.log(TAG, "Application exit requested by shift+esc");
            Gdx.app.exit();
        }

        if (keycode == Input.Keys.TAB) {
            Util.log(TAG, "Toggled debug rendering by tab");
            Flag.DEBUG_RENDER.toggle();
            return true;
        }

        if (keycode == Input.Keys.Z) {
            Util.log(TAG, "Toggled view system 'zoomFit'");
            var viewSystem = screen.engine.getSystem(ViewSystem.class);
            viewSystem.zoomFit = !viewSystem.zoomFit;
        }

        if (keycode == Input.Keys.B) {
            Util.log(TAG, "Toggled view system 'stayWithinBounds'");
            var viewSystem = screen.engine.getSystem(ViewSystem.class);
            viewSystem.stayWithinBounds = !viewSystem.stayWithinBounds;
        }

        if (keycode == Input.Keys.NUM_1) {
            Flag.GLOBAL.toggle();
            return true;
        }

        if (keycode == Input.Keys.NUM_2) {
            Flag.DEBUG_RENDER.toggle();
            return true;
        }

        if (keycode == Input.Keys.NUM_0) {
            Flag.FRAME_STEP.toggle();
            return true;
        }

        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        super.touchDown(screenX, screenY, pointer, button);

        if (pointer == Input.Buttons.LEFT) {
            var pos = FramePool.vec3(screenX, screenY);
            screen.worldCamera.unproject(pos);

            var params = new TestEffect.Params(pos.x, pos.y, Color.MAGENTA);
            var emitter = Factory.emitter(EmitterType.TEST, params);
            screen.engine.addEntity(emitter);

            return true;
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        super.touchUp(screenX, screenY, pointer, button);
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        super.touchCancelled(screenX, screenY, pointer, button);
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        super.touchDragged(screenX, screenY, pointer);
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        super.mouseMoved(screenX, screenY);
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        super.scrolled(amountX, amountY);
        return false;
    }
}
