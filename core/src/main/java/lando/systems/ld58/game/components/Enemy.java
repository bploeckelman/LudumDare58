package lando.systems.ld58.game.components;

import com.badlogic.ashley.core.Component;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Enemy implements Component {

    public enum Type { MARIO }
    public enum State { IDLE, PATROL }

    public final Type type;

    public State state;
    public float stateTime;
    public int direction;

    public Enemy(Type type) {
        this(type, State.IDLE, 0f, 0);
    }

    public void nextState() {
        switch (state) {
            case IDLE:   state = State.PATROL; break;
            case PATROL: state = State.IDLE;   break;
        }
    }
}
