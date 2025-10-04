package lando.systems.ld58.game.state.goomba;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.utils.GdxRuntimeException;
import lando.systems.ld58.assets.AnimType;
import lando.systems.ld58.assets.SoundType;
import lando.systems.ld58.game.Components;
import lando.systems.ld58.game.Signals;
import lando.systems.ld58.game.actions.ParallelActions;
import lando.systems.ld58.game.actions.SequentialActions;
import lando.systems.ld58.game.actions.types.*;
import lando.systems.ld58.game.components.TilemapObject;
import lando.systems.ld58.game.signals.StateEvent;
import lando.systems.ld58.game.state.PlayerState;
import lando.systems.ld58.utils.FramePool;
import lando.systems.ld58.utils.Util;

public class GoombaStartState extends PlayerState {

    private static final Family SPAWNERS = Family.one(TilemapObject.Spawner.class).get();

    public GoombaStartState(Engine engine, Entity entity) {
        super(engine, entity);
    }

    @Override
    public void enter() {
        super.enter();

        var spawner = findSpawnerForPlayer();
        var startPos = FramePool.pos(spawner.x(), spawner.y());

        var introSequence = new SequentialActions(
            new SetPositionAction(startPos),
            new DelayAction(0.5f),
            new ParallelActions(
                new MoveRelativeAction(1.5f, 0, 150),
                new PlayAnimAction(AnimType.GOOMBA_RAGE_IDLE),
                new PlaySoundAction(SoundType.CLIMBER_BOUNCE)
            ),
            new PlayAnimAction(AnimType.GOOMBA_NORMAL_IDLE),
            new DelayAction(0.5f)
        );

        startActions(introSequence);
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        if (actionsCompleted()) {
            Signals.changeState.dispatch(new StateEvent.Change(entity, this.getClass(), GoombaNormalState.class));
        }
    }

    private TilemapObject.Spawner findSpawnerForPlayer() {
        return Util.streamOf(engine.getEntitiesFor(SPAWNERS))
            .map(e -> Components.get(e, TilemapObject.Spawner.class))
            .findFirst()
            .orElseThrow(() -> new GdxRuntimeException("no spawner found in map"));
    }
}
