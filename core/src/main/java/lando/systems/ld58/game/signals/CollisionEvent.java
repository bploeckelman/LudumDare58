package lando.systems.ld58.game.signals;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.github.tommyettinger.digital.Stringf;
import lando.systems.ld58.Flag;
import lando.systems.ld58.game.Components;
import lando.systems.ld58.game.components.Name;
import lando.systems.ld58.game.components.collision.CollisionResponse;
import lando.systems.ld58.utils.Util;

public interface CollisionEvent extends SignalEvent {

    String TAG = CollisionEvent.class.getSimpleName();

    Entity entityA();
    Entity entityB();
    CollisionResponse response();

    static Move move(Entity a, Entity b, int xDir, int yDir) {
        if (Flag.LOG_EVENT.isEnabled()) {
            Util.log(TAG, Stringf.format("Move: mover=%s target=%s",
                Components.get(a, Name.class), Components.get(b, Name.class)));
        }
        var event = new Move(a, b, xDir, yDir);
        signal.dispatch(event);
        return event;
    }

    static Overlap overlap(Entity a, Entity b) {
        if (Flag.LOG_EVENT.isEnabled()) {
            Util.log(TAG, Stringf.format("Overlap between entities: a=%s b=%s",
                Components.get(a, Name.class), Components.get(b, Name.class)));
        }
        var event = new Overlap(a, b);
        signal.dispatch(event);
        return event;
    }

    final class Move implements CollisionEvent {

        private final Entity mover;
        private final Entity target;
        private final Vector2 dir;

        public CollisionResponse response;

        private Move(Entity mover, Entity target, int xDir, int yDir) {
            this.mover = mover;
            this.target = target;
            this.dir = new Vector2(xDir, yDir);
            this.response = CollisionResponse.STOP_BOTH;
        }

        public Entity entityA()  { return mover; }
        public Entity entityB()  { return target; }
        public CollisionResponse response() { return response; }

        public Entity mover()  { return mover; }
        public Entity target() { return target; }
        public Vector2 dir()   { return dir; }
    }

    final class Overlap implements CollisionEvent {

        private final Entity entityA;
        private final Entity entityB;

        public CollisionResponse response;

        private Overlap(Entity a, Entity b) {
            this.entityA = a;
            this.entityB = b;
            this.response = CollisionResponse.STOP_BOTH;
        }

        public Entity entityA()  { return entityA; }
        public Entity entityB()  { return entityB; }
        public CollisionResponse response() { return response; }
    }
}
