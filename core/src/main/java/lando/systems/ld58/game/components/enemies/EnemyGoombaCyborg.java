package lando.systems.ld58.game.components.enemies;

import com.badlogic.ashley.core.Component;

public class EnemyGoombaCyborg extends Enemy implements Component {

    public enum State { IDLE, PATROL }

    public State state;
    public float stateTime;
    public int direction;

    public EnemyGoombaCyborg() {
        this.state = EnemyGoombaCyborg.State.IDLE;
        this.stateTime = 0f;
        this.direction = 0;
    }
}
