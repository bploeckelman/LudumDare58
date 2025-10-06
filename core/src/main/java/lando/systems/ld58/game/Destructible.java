package lando.systems.ld58.game;

import com.badlogic.ashley.core.Component;

public class Destructible implements Component {
    public static final float RESPAWN_INTERVAL = 5f;

    public float respawnTimer = RESPAWN_INTERVAL;
    public boolean destroyed = false;
}
