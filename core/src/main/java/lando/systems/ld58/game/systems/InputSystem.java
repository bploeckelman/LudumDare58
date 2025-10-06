package lando.systems.ld58.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.github.tommyettinger.digital.Stringf;
import lando.systems.ld58.Flag;
import lando.systems.ld58.game.Components;
import lando.systems.ld58.game.InputControls;
import lando.systems.ld58.game.components.Input;
import lando.systems.ld58.game.components.Player;
import lando.systems.ld58.utils.Util;

public class InputSystem extends IteratingSystem {

    private static final String TAG = InputSystem.class.getSimpleName();

    private Controller controller;

    public InputSystem() {
        super(Family.one(Player.class, Input.class).get());
        this.controller = null;
    }

    @Override
    public void update(float delta) {
        keepControllerCurrent();
        super.update(delta);
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        var player = Components.optional(entity, Player.class).orElseThrow();
        var input  = Components.optional(entity, Input.class).orElseThrow();
        var controls = InputControls.forPlayer(player);

        // Collect key input --------------------------------------------------
        input.wasJumpJustPressed = Gdx.input.isKeyJustPressed(controls.jump());
        input.isJumpHeld     = Gdx.input.isKeyPressed(controls.jump());
        input.isMoveLeftHeld   = Gdx.input.isKeyPressed(controls.left());
        input.isMoveRightHeld  = Gdx.input.isKeyPressed(controls.right());
        input.isDownHeld = Gdx.input.isKeyPressed(controls.down());

        // Collect controller input -------------------------------------------
        if (controller != null) {
            var deadzone = 0.2f;
            var mapping = controller.getMapping();

            var jumpButtonDown = controller.getButton(mapping.buttonA);
            var jump = !input.wasControllerJumpButtonDown && jumpButtonDown;

            input.wasControllerJumpButtonDown = jumpButtonDown;
            input.wasJumpJustPressed              = input.wasJumpJustPressed || jump;

            var moveLeft  = controller.getButton(mapping.buttonDpadLeft)  || controller.getAxis(mapping.axisLeftX) < -deadzone;
            var moveRight = controller.getButton(mapping.buttonDpadRight) || controller.getAxis(mapping.axisLeftX) >  deadzone;
            var moveDown = controller.getButton(mapping.buttonDpadDown)   || controller.getAxis(mapping.axisLeftY) < -deadzone;

            input.isJumpHeld      = input.isJumpHeld      || jump;
            input.isMoveLeftHeld  = input.isMoveLeftHeld  || moveLeft;
            input.isMoveRightHeld = input.isMoveRightHeld || moveRight;
            input.isDownHeld      = input.isDownHeld      || moveDown;
        }

        input.moveDirX = input.isMoveLeftHeld ? -1 : input.isMoveRightHeld ? 1 : 0;

        if (Flag.LOG_INPUT.isEnabled()) {
            Util.log(InputSystem.TAG, toString());
        }
    }

    private void keepControllerCurrent() {
        var current = Controllers.getCurrent();
        if (current != null) {
            if (Flag.LOG_INPUT.isEnabled() && controller != current) {
                Util.log(TAG, Stringf.format("controller connected'%s' (%s)", current.getName(), current.getUniqueId()));
            }
            controller = current;
        } else {
            // detach controller, if there's no 'current' controller then the existing reference is invalid
            if (Flag.LOG_INPUT.isEnabled() && controller != null) {
                Util.log(TAG, Stringf.format("controller disconnected '%s' (%s)", controller.getName(), controller.getUniqueId()));
            }
            controller = null;
        }
    }
}
