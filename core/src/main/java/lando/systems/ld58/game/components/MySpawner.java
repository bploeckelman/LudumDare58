package lando.systems.ld58.game.components;

import com.badlogic.ashley.core.Component;

public class MySpawner implements Component {
    public final TilemapObject.Spawner spawner;
    public MySpawner(TilemapObject.Spawner spawner) {
        this.spawner = spawner;
    }
}
