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

        // Skip input processing for computer-controlled players
        if (player.controlledBy() == Player.ControlledBy.COMPUTER) {
            return;
        }

        var controls = InputControls.forPlayer(player);

        // Collect key input --------------------------------------------------
        input.wasActionPressed = Gdx.input.isKeyJustPressed(controls.action());
        input.isActionHeld     = Gdx.input.isKeyPressed(controls.action());
        input.isMoveLeftHeld   = Gdx.input.isKeyPressed(controls.left());
        input.isMoveRightHeld  = Gdx.input.isKeyPressed(controls.right());

        // Collect controller input -------------------------------------------
        var onlyPlayer1 = (player.number == 1);
        if (controller != null && onlyPlayer1) {
            var deadzone = 0.2f;
            var mapping = controller.getMapping();

            var actionButtonDown = controller.getButton(mapping.buttonA);
            var action = !input.wasControllerActionButtonDown && actionButtonDown;

            input.wasControllerActionButtonDown = actionButtonDown;
            input.wasActionPressed              = input.wasActionPressed || action;

            var moveLeft  = controller.getButton(mapping.buttonDpadLeft)  || controller.getAxis(mapping.axisLeftX) < -deadzone;
            var moveRight = controller.getButton(mapping.buttonDpadRight) || controller.getAxis(mapping.axisLeftX) >  deadzone;

            input.isActionHeld    = input.isActionHeld    || action;
            input.isMoveLeftHeld  = input.isMoveLeftHeld  || moveLeft;
            input.isMoveRightHeld = input.isMoveRightHeld || moveRight;
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
