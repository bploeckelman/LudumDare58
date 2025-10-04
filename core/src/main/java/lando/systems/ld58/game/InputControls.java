package lando.systems.ld58.game;

import com.badlogic.gdx.Input;
import lando.systems.ld58.game.components.Player;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class InputControls {

    public final int left;
    public final int right;
    public final int action;
    public final int debug;

    public int left() { return left; }
    public int right() { return right; }
    public int action() { return action; }
    public int debug() { return debug; }

    public static InputControls forPlayer(Player player) {
        return new InputControls(Input.Keys.A, Input.Keys.S, Input.Keys.Z, Input.Keys.E);
    }
}
