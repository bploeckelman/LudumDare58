package lando.systems.ld58.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import lando.systems.ld58.game.Components;

public class TileLayer extends Renderable implements Component {

    public final Tilemap tilemap;
    public final TiledMapTileLayer tileLayer;

    public TileLayer(Tilemap tilemap, TiledMapTileLayer tileLayer) {
        this.tilemap = tilemap;
        this.tileLayer = tileLayer;
    }

    public boolean isBackground() { return tileLayer.getName().equals("background"); }
    public boolean isMiddle()     { return tileLayer.getName().equals("middle"); }
    public boolean isForeground() { return tileLayer.getName().equals("foreground"); }

    // ------------------------------------------------------------------------
    // Convenience methods for stream filtering
    // ------------------------------------------------------------------------

    public static boolean isBackground(Entity entity) {
        return Components.optional(entity, TileLayer.class).map(TileLayer::isBackground).orElse(false);
    }
    public static boolean isMiddle(Entity entity) {
        return Components.optional(entity, TileLayer.class).map(TileLayer::isMiddle).orElse(false);
    }
    public static boolean isForeground(Entity entity) {
        return Components.optional(entity, TileLayer.class).map(TileLayer::isForeground).orElse(false);
    }
}
