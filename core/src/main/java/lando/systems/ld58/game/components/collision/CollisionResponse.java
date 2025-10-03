package lando.systems.ld58.game.components.collision;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CollisionResponse {

    public final boolean stopMovement;
    public final boolean stopVelocity;

    public boolean stopMovement() { return stopMovement; }
    public boolean stopVelocity() { return stopVelocity; }

    public static final CollisionResponse STOP_BOTH = new CollisionResponse(true, true);
    public static final CollisionResponse KEEP_VELOCITY = new CollisionResponse(true, false);
    public static final CollisionResponse KEEP_MOVEMENT = new CollisionResponse(false, true);
    public static final CollisionResponse PASSTHROUGH = new CollisionResponse(false, false);
}
