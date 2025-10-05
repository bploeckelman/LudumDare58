package lando.systems.ld58.game.components.enemies;

import com.badlogic.ashley.core.Component;

public class EnemyMario extends Enemy implements Component {

    public static final float WALK_ACCEL = 300f; // pixels/sec^2

    public enum State { IDLE, PATROL }

    public State state;
    public float stateTime;
    public int direction;

    public EnemyMario() {
        this.state = State.IDLE;
        this.stateTime = 0f;
        this.direction = 0;
    }
}
