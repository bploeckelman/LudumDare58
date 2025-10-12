package lando.systems.ld58.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.github.tommyettinger.digital.Stringf;
import lando.systems.ld58.game.Components;
import lando.systems.ld58.game.components.Id;
import lando.systems.ld58.game.components.Player;
import lando.systems.ld58.game.scenes.Scene;
import lando.systems.ld58.game.signals.SignalEvent;
import lando.systems.ld58.game.signals.StateEvent;
import lando.systems.ld58.game.state.PlayerState;
import lando.systems.ld58.game.state.goomba.GoombaNormalState;
import lando.systems.ld58.game.state.goomba.GoombaStartState;
import lando.systems.ld58.screens.BaseScreen;

import java.util.HashMap;
import java.util.Map;

public class PlayerStateSystem<ScreenType extends BaseScreen> extends IteratingSystem implements Listener<SignalEvent> {

    public static final int PRIORITY = 10;

    private static final String TAG = PlayerStateSystem.class.getSimpleName();

    private final ScreenType screen;
    private final Scene<ScreenType> scene;
    private final Map<Entity, Map<Class<? extends PlayerState>, PlayerState>> allStates;
    private final Map<Entity, PlayerState> currentStates;

    @SuppressWarnings("unchecked")
    public PlayerStateSystem(ScreenType screen) {
        super(Family.all(Player.class).get(), PRIORITY);
        this.screen = screen;
        // NOTE: only IntroScreen and GameScreen have Scenes!
        //  but that's ok, because we only create a PlayerStateSystem for those screens
        this.scene = (Scene<ScreenType>) screen.scene();
        this.currentStates = new HashMap<>();
        this.allStates = new HashMap<>();
        SignalEvent.addListener(this);
    }

    public ScreenType screen() { return screen; }

    public PlayerState currentState(Entity entity) {
        return currentStates.get(entity);
    }

    public void setState(Entity entity, Class<? extends PlayerState> targetState) {
        var currentState = currentState(entity);
        if (targetState != currentState.getClass()) {
            currentState.abortActions();

            var states = allStates.get(entity);
            currentStates.put(entity, states.get(targetState));
        }
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        for (var entity : getEntities()) {
            var currentState = currentStates.get(entity);
            if (currentState != null) {
                currentState.update(deltaTime);
            }
        }
    }

    @Override
    public void receive(Signal<SignalEvent> signal, SignalEvent event) {
        if (event instanceof StateEvent.Change) {
            var change = (StateEvent.Change) event;
            var entity = change.entity();

            // Exit the current state
            var currentState = currentStates.get(entity);
            if (currentState != null) {
                currentState.exit();
            }

            // TODO: the null checks here are probably a bit excessive, but worth seeing how the timing works out
            //  might need to explicitly set priority for this system fairly high so it runs 'processEntity'
            //  earlier than any signals would be dispatched

            // Get the states map for this entity
            var states = allStates.get(entity);
            if (states == null) {
                var id = Components.get(entity, Id.class);
                throw new GdxRuntimeException(
                    Stringf.format(" Failed to get states by type map for entity with %s, "
                    + "this shouldn't happen unless %s received a change state signal "
                    + "before processEntity() was called for that entity.", id, TAG));
            }

            // Enter the next state
            var nextState = states.get(change.toState());
            if (nextState == null) {
                var id = Components.get(entity, Id.class);
                throw new GdxRuntimeException(
                    Stringf.format("Failed to find 'change to state' %s for entity with %s, "
                    + "make sure that state is included in populateIfEmpty() in %s.",
                    change.toState().getSimpleName(), id, TAG));
            }
            nextState.enter();

            // Set the next state as the current state
            currentStates.put(entity, nextState);
        }
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        var statesByType = allStates.computeIfAbsent(entity, k -> new HashMap<>());
        if (statesByType.isEmpty()) {
            populateStates(entity, scene, statesByType);
            StateEvent.change(entity, null, GoombaStartState.class);
        }
    }

    private void populateStates(Entity entity, Scene<ScreenType> scene, Map<Class<? extends PlayerState>, PlayerState> statesByPlayerEntity) {
        var engine = getEngine();
        statesByPlayerEntity.putIfAbsent(GoombaNormalState.class, new GoombaNormalState(engine, scene, entity));
        statesByPlayerEntity.putIfAbsent(GoombaStartState.class,  new GoombaStartState(engine, scene, entity));
    }
}
