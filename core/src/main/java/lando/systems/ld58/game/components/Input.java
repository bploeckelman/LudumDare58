package lando.systems.ld58.game.components;

import com.badlogic.ashley.core.Component;
import lombok.ToString;

@ToString
public class Input implements Component {

    public int moveDirX;
    public boolean isActionHeld;
    public boolean isMoveLeftHeld;
    public boolean isMoveRightHeld;
    public boolean wasActionPressed;
    public boolean wasControllerActionButtonDown;

    public Input() {}

    public Input(int moveDirX, boolean isMoveLeftHeld, boolean isMoveRightHeld, boolean isActionHeld, boolean wasActionPressed) {
        this.moveDirX = moveDirX;
        this.isMoveLeftHeld = isMoveLeftHeld;
        this.isMoveRightHeld = isMoveRightHeld;
        this.isActionHeld = isActionHeld;
        this.wasActionPressed = wasActionPressed;
        this.wasControllerActionButtonDown = false;
    }

    public static Input empty() {
        return new Input(0, false, false, false, false);
    }

    public void reset() {
        isMoveLeftHeld = false;
        isMoveRightHeld = false;
        isActionHeld = false;
        wasActionPressed = false;
        wasControllerActionButtonDown = false;
        moveDirX = 0;
    }
}
