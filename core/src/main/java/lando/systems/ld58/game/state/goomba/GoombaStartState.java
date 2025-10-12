package lando.systems.ld58.game.state.goomba;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.utils.GdxRuntimeException;
import lando.systems.ld58.game.Components;
import lando.systems.ld58.game.actions.SequentialActions;
import lando.systems.ld58.game.actions.types.SetPositionAction;
import lando.systems.ld58.game.components.TilemapObject;
import lando.systems.ld58.game.scenes.Scene;
import lando.systems.ld58.game.signals.StateEvent;
import lando.systems.ld58.game.state.PlayerState;
import lando.systems.ld58.screens.BaseScreen;
import lando.systems.ld58.utils.FramePool;
import lando.systems.ld58.utils.Util;

public class GoombaStartState extends PlayerState {

    private static final Family SPAWNERS = Family.one(TilemapObject.Spawner.class).get();

    public GoombaStartState(Engine engine, Scene<? extends BaseScreen> scene, Entity entity) {
        super(engine, scene, entity);
    }

    @Override
    public void enter() {
        super.enter();

        var spawner = findSpawnerForPlayer();
        var startPos = FramePool.pos(spawner.x(), spawner.y());

        var introSequence = new SequentialActions(
            new SetPositionAction(startPos)
//            ,
//            new DelayAction(0.5f),
//            new ParallelActions(
//                new MoveRelativeAction(0.5f, 0, 50),
//                new PlayAnimAction(AnimType.BILLY_JUMP),
//                new PlaySoundAction(SoundType.JUMP)
//            ),
//            new DelayAction(0.3f),
//            new PlayAnimAction(AnimType.BILLY_YELL),
//            new DelayAction(0.2f),
//            new PlayAnimAction(AnimType.BILLY_IDLE),
//            new DelayAction(0.5f)
        );

        startActions(introSequence);
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        if (actionsCompleted()) {
            StateEvent.change(entity, this.getClass(), GoombaNormalState.class);
        }
    }

    private TilemapObject.Spawner findSpawnerForPlayer() {
        return Util.streamOf(engine.getEntitiesFor(SPAWNERS))
            .map(e -> Components.get(e, TilemapObject.Spawner.class))
            .findFirst()
            .orElseThrow(() -> new GdxRuntimeException("no spawner found in map"));
    }
}
