package lando.systems.ld58.game.components;

import com.badlogic.ashley.core.Component;
import com.github.tommyettinger.digital.Stringf;

public class Player implements Component {

    private static final String TAG = Player.class.getSimpleName();

    public enum JumpState { FALLING, GROUNDED, JUMPED, GRABBED, DOUBLE_JUMPED }

    private JumpState jumpState;

    public Player() {
        this.jumpState = JumpState.FALLING;
    }

    public JumpState jumpState() { return jumpState; }

    public void jumpState(JumpState jumpState) { this.jumpState = jumpState; }

    @Override
    public String toString() {
        return Stringf.format("%s{jumpState=%s}", TAG, jumpState);
    }
}
