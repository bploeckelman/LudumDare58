package lando.systems.ld58.game.actions;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;

public abstract class Action {

    protected float elapsed = 0f;
    protected boolean started = false;
    protected boolean completed = false;

    public abstract void start(Entity entity, Engine engine);
    public abstract void update(Entity entity, Engine engine, float delta);
    public abstract boolean isComplete();

    public final void tick(Entity entity, Engine engine, float delta) {
        if (!started) {
            start(entity, engine);
            started = true;
        }

        if (!completed) {
            elapsed += delta;
            update(entity, engine, delta);
            completed = isComplete();
        }
    }

    protected void abort() {
        started = true;
        completed = true;
    }
}
