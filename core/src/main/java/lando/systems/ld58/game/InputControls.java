package lando.systems.ld58.game;

import com.badlogic.gdx.Input;
import lando.systems.ld58.game.components.Player;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class InputControls {

    public final int left;
    public final int right;
    public final int jump;
    public final int debug;

    public int left() { return left; }
    public int right() { return right; }
    public int jump() { return jump; }
    public int debug() { return debug; }

    public static InputControls forPlayer(Player player) {
        return new InputControls(Input.Keys.A, Input.Keys.D, Input.Keys.SPACE, Input.Keys.E);
    }
}
