package lando.systems.ld58.game.signals;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld58.game.components.collision.CollisionResponse;

public interface CollisionEvent {

    String TAG = CollisionEvent.class.getSimpleName();

    Entity entityA();
    Entity entityB();
    CollisionResponse response();

    static Move move(Entity a, Entity b, int xDir, int yDir) {
//        Util.log(TAG, "Move: mover=%s target=%s"
//            .formatted(Components.get(a, Id.class), Components.get(b, Id.class)));
        return new Move(a, b, xDir, yDir);
    }

    static Overlap overlap(Entity a, Entity b) {
//        Util.log(TAG, "Overlap between entities: a=%s b=%s"
//            .formatted(Components.get(a, Id.class), Components.get(b, Id.class)));
        return new Overlap(a, b);
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
