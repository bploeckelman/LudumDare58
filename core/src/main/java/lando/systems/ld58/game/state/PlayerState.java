package lando.systems.ld58.game.state;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.github.tommyettinger.digital.Stringf;
import lando.systems.ld58.game.Components;
import lando.systems.ld58.game.actions.ActionGroup;
import lando.systems.ld58.game.components.*;
import lando.systems.ld58.game.systems.CollisionCheckSystem;
import lando.systems.ld58.game.systems.PlayerStateSystem;
import lando.systems.ld58.utils.Util;

public abstract class PlayerState {

    protected Engine engine;
    protected Entity entity;
    protected ActionGroup currentActions;
    protected CollisionCheckSystem collisionCheckSystem;
    protected PlayerStateSystem playerStateSystem;
    protected float elapsed = 0f;

    public PlayerState(Engine engine, Entity entity) {
        this.engine = engine;
        this.entity = entity;
        this.currentActions = null;
        this.collisionCheckSystem = engine.getSystem(CollisionCheckSystem.class);
        this.playerStateSystem = engine.getSystem(PlayerStateSystem.class);
    }

    public float elapsed() { return elapsed; }

    public void enter() {
        Util.log(getClass().getSimpleName(), "enter");
        elapsed = 0f;
    }

    public void exit() {
        Util.log(getClass().getSimpleName(), Stringf.format("exit (after %.1f sec)", elapsed));
    }

    public void update(float delta) {
        elapsed += delta;

        if (currentActions != null && !currentActions.isComplete()) {
            currentActions.tick(entity, engine, delta);
        }
    }

    public void abortActions() {
        if (currentActions == null || currentActions.isComplete()) {
            return;
        }
        currentActions.abort();
    }

    protected void startActions(ActionGroup actionGroup) {
        currentActions = actionGroup;
    }

    protected boolean actionsCompleted() {
        return currentActions == null || currentActions.isComplete();
    }

    public Player player()       { return Components.get(entity, Player.class); }
    public Animator animator()   { return Components.get(entity, Animator.class); }
    public Collider collider()   { return Components.get(entity, Collider.class); }
    public Cooldowns cooldowns() { return Components.get(entity, Cooldowns.class); }
    public Friction friction()   { return Components.get(entity, Friction.class); }
    public Gravity gravity()     { return Components.get(entity, Gravity.class); }
    public Id id()               { return Components.get(entity, Id.class); }
    public Input input()         { return Components.get(entity, Input.class); }
    public Name name()           { return Components.get(entity, Name.class); }
    public Position position()   { return Components.get(entity, Position.class); }
    public Velocity velocity()   { return Components.get(entity, Velocity.class); }
}
