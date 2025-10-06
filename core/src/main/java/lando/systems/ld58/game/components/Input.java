package lando.systems.ld58.game.components;

import com.badlogic.ashley.core.Component;
import lombok.ToString;

@ToString
public class Input implements Component {

    public int moveDirX;
    public boolean isJumpHeld;
    public boolean isMoveLeftHeld;
    public boolean isMoveRightHeld;
    public boolean wasJumpJustPressed;
    public boolean wasControllerJumpButtonDown;
    public boolean isDownHeld;
    public boolean isActionHeld;
    public boolean isActionJustPressed;

    public Input() {}

    public Input(int moveDirX, boolean isMoveLeftHeld, boolean isMoveRightHeld, boolean isActionHeld, boolean wasActionPressed, boolean isDownHeld) {
        this.moveDirX = moveDirX;
        this.isMoveLeftHeld = isMoveLeftHeld;
        this.isMoveRightHeld = isMoveRightHeld;
        this.isJumpHeld = isActionHeld;
        this.wasJumpJustPressed = wasActionPressed;
        this.wasControllerJumpButtonDown = false;
        this.isDownHeld = isDownHeld;
        this.isActionHeld = isActionHeld;
        this.isActionJustPressed = false;
    }

    public static Input empty() {
        return new Input(0, false, false, false, false, false);
    }

    public void reset() {
        isMoveLeftHeld = false;
        isMoveRightHeld = false;
        isJumpHeld = false;
        wasJumpJustPressed = false;
        wasControllerJumpButtonDown = false;
        moveDirX = 0;
        isDownHeld = false;
        isActionHeld = false;
        isActionJustPressed = false;
    }
}
