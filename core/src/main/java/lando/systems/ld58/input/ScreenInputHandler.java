package lando.systems.ld58.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import lando.systems.ld58.Flag;
import lando.systems.ld58.screens.BaseScreen;
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

        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        super.touchDown(screenX, screenY, pointer, button);
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
