package lando.systems.ld58.game.components;

import com.badlogic.ashley.core.Component;
import com.github.tommyettinger.digital.Stringf;

public class Player implements Component {

    private static final String TAG = Player.class.getSimpleName();

    public enum ControlledBy { PLAYER, COMPUTER }

    public enum CharacterType { CLIMBER, SQUATCH }

    public enum JumpState { FALLING, GROUNDED, JUMPED, GRABBED, DOUBLE_JUMPED }

    public final int number;

    private ControlledBy controlledBy;
    private CharacterType characterType;
    private JumpState jumpState;

    public Player(int number) {
        this.number = number;
        this.controlledBy = ControlledBy.PLAYER;
        this.characterType = CharacterType.CLIMBER;
        this.jumpState = JumpState.FALLING;
    }

    public int number() { return number; }
    public ControlledBy controlledBy() { return controlledBy; }
    public CharacterType characterType() { return characterType; }
    public JumpState jumpState() { return jumpState; }

    public boolean isClimber() { return characterType == CharacterType.CLIMBER; }
    public boolean isSquatch() { return characterType == CharacterType.SQUATCH; }

    public void controlledBy(ControlledBy controlledBy) { this.controlledBy = controlledBy; }
    public void characterType(CharacterType characterType) { this.characterType = characterType; }
    public void jumpState(JumpState jumpState) { this.jumpState = jumpState; }

    @Override
    public String toString() {
        return Stringf.format("%s{number=%d, controlledBy=%s, characterType=%s, jumpState=%s}",
            TAG, number, controlledBy, characterType, jumpState);
    }
}
