package lando.systems.ld58.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;
import com.github.tommyettinger.digital.Stringf;
import lando.systems.ld58.Flag;
import lando.systems.ld58.screens.BaseScreen;
import lando.systems.ld58.utils.FramePool;
import lando.systems.ld58.utils.Util;

public abstract class InputHandler extends InputAdapter {

    protected final BaseScreen screen;
    protected final Vector2 pointer;
    protected final Vector2 pointerScreen;

    public InputHandler(BaseScreen screen) {
        this.screen = screen;
        this.pointer = new Vector2();
        this.pointerScreen = new Vector2();
    }

    @Override
    public boolean keyDown(int keycode) {
        log(Stringf.format("keyDown: %d", keycode));
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        log(Stringf.format("keyUp: %d", keycode));
        if (keycode == Input.Keys.ESCAPE && Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
            Gdx.app.exit();
        }
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointerIndex, int button) {
        log(Stringf.format("touchDown: b%d p%d=(%d, %d)", button, pointerIndex, screenX, screenY));
        updatePointer(screenX, screenY);
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointerIndex, int button) {
        log(Stringf.format("touchUp: b%d p%d=(%d, %d)", button, pointerIndex, screenX, screenY));
        updatePointer(screenX, screenY);
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointerIndex, int button) {
        log(Stringf.format("touchCancelled: b%d p%d=(%d, %d)", button, pointerIndex, screenX, screenY));
        updatePointer(screenX, screenY);
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointerIndex) {
        log(Stringf.format("touchDragged: p%d=(%d, %d)", pointerIndex, screenX, screenY));
        updatePointer(screenX, screenY);
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        // NOTE: log is too busy with this active
        //log("mouseMoved: (%d, %d)".formatted(screenX, screenY));
        updatePointer(screenX, screenY);
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        log(Stringf.format("scrolled: (%.1f, %.1f)", amountX, amountY));
        return false;
    }

    protected void log(String event) {
        if (Flag.LOG_INPUT.isDisabled()) return;

        var clazz = getClass().getSimpleName();
        Util.log(clazz, "event: " + event);
    }

    protected void updatePointer(int screenX, int screenY) {
        pointerScreen.set(screenX, screenY);

        var screenToWorld = FramePool.vec3(pointerScreen);
        screen.worldCamera.unproject(screenToWorld);

        pointer.set(screenToWorld.x, screenToWorld.y);
    }
}
