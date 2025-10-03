package lando.systems.ld58.game;

import com.badlogic.gdx.Input;
import lando.systems.ld58.game.components.Player;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class InputControls {

    public final int coin;
    public final int left;
    public final int right;
    public final int action;
    public final int debug;

    public int coin() { return coin; }
    public int left() { return left; }
    public int right() { return right; }
    public int action() { return action; }
    public int debug() { return debug; }

    public static InputControls forPlayer(Player player) {
        return forPlayer.get(player.number);
    }

    private static final Map<Integer, InputControls> forPlayer = Map.of(
        1, new InputControls(Input.Keys.C, Input.Keys.A, Input.Keys.S, Input.Keys.Z, Input.Keys.E),
        2, new InputControls(Input.Keys.V, Input.Keys.D, Input.Keys.F, Input.Keys.X, Input.Keys.R),
        3, new InputControls(Input.Keys.B, Input.Keys.G, Input.Keys.H, Input.Keys.O, Input.Keys.Y),
        4, new InputControls(Input.Keys.N, Input.Keys.J, Input.Keys.K, Input.Keys.P, Input.Keys.U)
    );
}
