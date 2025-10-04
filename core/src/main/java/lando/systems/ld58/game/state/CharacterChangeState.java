package lando.systems.ld58.game.state;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.GdxRuntimeException;
import lando.systems.ld58.game.Components;
import lando.systems.ld58.game.Signals;
import lando.systems.ld58.game.components.Interp;
import lando.systems.ld58.game.components.TilemapObject;
import lando.systems.ld58.game.components.Viewer;
import lando.systems.ld58.game.signals.StateEvent;
import lando.systems.ld58.game.state.goomba.GoombaStartState;
import lando.systems.ld58.utils.Util;

public class CharacterChangeState extends PlayerState {

    private static final Family SPAWNERS = Family.one(TilemapObject.Spawner.class).get();
    private static final Family VIEW = Family.all(Viewer.class, Interp.class).get();

    private Color originalTint;

    public CharacterChangeState(Engine engine, Entity entity) {
        super(engine, entity);
    }

    @Override
    public void enter() {
        super.enter();

        // TODO: this will end up being more elaborate, playing a scripted sequence, sounds, effects, etc...

        var animator = animator();
        var spawner = findSpawnerForPlayer();

        // Make the player invisible for the duration of the change
        originalTint = animator().tint.cpy();
        animator.tint.a = 0;

//        if (player()) {
//            var viewer = Components.get(findViewEntity(), Viewer.class);
//            position().set(spawner.x(), (int) viewer.top());
//
//            climberGravity = gravity();
//            entity.remove(Gravity.class);
//
//            // Replace the climber collider with the squatch collider, creating if necessary
//            climberCollider = collider();
//            if (squatchCollider == null) {
//                squatchCollider = Collider.rect(
//                    CollisionMask.PLAYER, Constants.SQUATCH_COLLIDER_BOUNDS, CollisionMask.PLAYER, CollisionMask.SOLID);
//            }
//            entity.remove(Collider.class);
//            entity.add(squatchCollider);
//
//            var animBounds = Constants.SQUATCH_ANIMATOR_BOUNDS;
//            animator.origin.set(animBounds.x, animBounds.y);
//            animator.size.set(animBounds.width, animBounds.height);
//            Signals.animStart.dispatch(new AnimationEvent.Start(animator, AnimType.HERO_IDLE));
//        }
//        else if (player().isSquatch()) {
//            if (climberGravity == null) {
//                climberGravity = new Gravity(Constants.GRAVITY);
//            }
//            entity.add(climberGravity);
//
//            // Replace the squatch collider with the climber collider, creating if necessary
//            squatchCollider = collider();
//            if (climberCollider == null) {
//                climberCollider = Collider.rect(
//                    CollisionMask.PLAYER, Constants.CLIMBER_COLLIDER_BOUNDS, CollisionMask.PLAYER);
//            }
//            entity.remove(Collider.class);
//            entity.add(squatchCollider);
//
//            var animBounds = Constants.CLIMBER_ANIMATOR_BOUNDS;
//            animator.origin.set(animBounds.x, animBounds.y);
//            animator.size.set(animBounds.width, animBounds.height);
//            Signals.animStart.dispatch(new AnimationEvent.Start(animator, AnimType.HERO_FALL));
//
//            position().set(spawner.x(), spawner.y());
//        }
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        // Restore tint to make visible again
        animator().tint.set(originalTint);

        // NOTE: can't change state from the 'enter' method!
        // TODO: clean this up and remove squatch-out stuff
//        if (player().isClimber()) {
            Signals.changeState.dispatch(new StateEvent.Change(entity, this.getClass(), GoombaStartState.class));
//        } else if (player().isSquatch()) {
//            Signals.changeState.dispatch(new StateEvent.Change(entity, this.getClass(), GoombaStartState.class));
//        }
    }

    // TODO: this is in several classes now, probably worth extracting to a utility method
    private TilemapObject.Spawner findSpawnerForPlayer() {
        return Util.streamOf(engine.getEntitiesFor(SPAWNERS))
            .map(e -> Components.get(e, TilemapObject.Spawner.class))
            .findFirst()
            .orElseThrow(() -> new GdxRuntimeException("no spawner found in map"));
    }

    private Entity findViewEntity() {
        return Util.streamOf(engine.getEntitiesFor(VIEW)).findFirst()
            .orElseThrow(() -> new GdxRuntimeException("No View entity found in map"));
    }
}
