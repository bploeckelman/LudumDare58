package lando.systems.ld58.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.maps.MapObject;
import lando.systems.ld58.game.Factory;
import lombok.AllArgsConstructor;

public interface TilemapObject {

    Tilemap tilemap();
    MapObject mapObject();

    static Entity createEntity(Tilemap tilemap, MapObject mapObject) {
        var component = (mapObject.getName().equals("spawner"))
            ? new TilemapObject.Spawner(tilemap, mapObject)
            : new TilemapObject.Simple(tilemap, mapObject);

        var entity = Factory.createEntity();
        entity.add(component);
        return entity;
    }

    @AllArgsConstructor
    class Simple implements TilemapObject, Component {

        public final Tilemap tilemap;
        public final MapObject mapObject;

        @Override
        public Tilemap tilemap() {
            return tilemap;
        }

        @Override
        public MapObject mapObject() {
            return mapObject;
        }
    }

    @AllArgsConstructor
    class Spawner implements TilemapObject, Component {

        public final Tilemap tilemap;
        public final MapObject mapObject;
        public final int playerNumber;
        public final int id;
        public final int x;
        public final int y;

        public Spawner(Tilemap tilemap, MapObject object) {
            this(tilemap, object,
                object.getProperties().get("playerNumber", 0, Integer.class),
                object.getProperties().get("id", -1, Integer.class),
                object.getProperties().get("x", 0f, Float.class).intValue(),
                object.getProperties().get("y", 0f, Float.class).intValue());
        }

        public int playerNumber() { return playerNumber; }
        public int id() { return id; }
        public int x() { return x; }
        public int y() { return y; }

        @Override
        public Tilemap tilemap() {
            return tilemap;
        }

        @Override
        public MapObject mapObject() {
            return mapObject;
        }
    }
}
