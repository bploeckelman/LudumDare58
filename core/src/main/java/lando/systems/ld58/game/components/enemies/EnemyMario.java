package lando.systems.ld58.game.components.enemies;

import com.badlogic.ashley.core.Component;

public class EnemyMario implements Enemy, Component {

    public enum State { IDLE, PATROL }

    public State state;
    public float stateTime;
    public int direction;

    public EnemyMario() {
        this.state = State.IDLE;
    }
}
