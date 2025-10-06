package lando.systems.ld58.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Rectangle;
import lando.systems.ld58.game.Factory;
import lombok.AllArgsConstructor;

public interface TilemapObject {

    Tilemap tilemap();
    MapObject mapObject();

    static Entity createEntity(Tilemap tilemap, MapObject mapObject) {
        var component
            = mapObject.getName().equals("spawner") ? new TilemapObject.Spawner(tilemap, mapObject)
            : mapObject.getName().equals("trigger") ? new TilemapObject.Trigger(tilemap, mapObject)
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
        public final String type;
        public final int id;
        public final int x;
        public final int y;

        public Spawner(Tilemap tilemap, MapObject object) {
            this(tilemap, object,
                object.getProperties().get("type", "", String.class),
                object.getProperties().get("id", -1, Integer.class),
                object.getProperties().get("x", 0f, Float.class).intValue(),
                object.getProperties().get("y", 0f, Float.class).intValue());
        }

        public String type() { return type; }
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

    @AllArgsConstructor
    class Trigger implements TilemapObject, Component {

        public final Tilemap tilemap;
        public final MapObject mapObject;
        public final String type;
        public final int id;
        public final Rectangle bounds;

        public boolean activated;

        public Trigger(Tilemap tilemap, MapObject object) {
            this(tilemap, object,
                object.getProperties().get("type", "", String.class),
                object.getProperties().get("id", -1, Integer.class),
                object.getProperties().get("rectangle", new Rectangle(), Rectangle.class),
                false);
        }

        public String type() { return type; }
        public int id() { return id; }
        public Rectangle bounds() { return bounds; }

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
