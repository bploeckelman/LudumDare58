package lando.systems.ld58.game.scenes;

import com.badlogic.ashley.core.Family;
import lando.systems.ld58.game.Components;
import lando.systems.ld58.game.Factory;
import lando.systems.ld58.game.components.Position;
import lando.systems.ld58.game.components.TileLayer;
import lando.systems.ld58.game.components.Tilemap;
import lando.systems.ld58.game.components.TilemapObject;
import lando.systems.ld58.game.systems.ViewSystem;
import lando.systems.ld58.screens.GameScreen;
import lando.systems.ld58.utils.Util;

import java.util.stream.Collectors;

import static lando.systems.ld58.game.Constants.BACKROUND_Z_LEVEL;
import static lando.systems.ld58.game.Constants.FOREGROUND_Z_LEVEL;

public class SceneTest extends Scene<GameScreen> {

    public static final Family SPAWNERS = Family.one(TilemapObject.Spawner.class).get();

    public SceneTest(GameScreen screen) {
        super(screen);

        // configure the camera to emulate a low res display
        var width = 240;
        var height = 160;
        var camera = screen.worldCamera;
        camera.setToOrtho(false, width, height);
        camera.update();

        var engine = screen.engine;

        // Set up the map view
        view = Factory.view();
        engine.addEntity(view);

        // Load the map
        var mapPath = "maps/test.tmx";
        map = Factory.map(mapPath);
        var tilemap = Components.get(map, Tilemap.class);
        var mapPosition = Components.get(map, Position.class);

        // Load the background
        // TODO: the size handling is wrong here, probably a bug in Image or Renderable
        // TODO: map backgrounds should just be a map image layer and have their own renderable
        //  otherwise it's hard to handle ordering of map tile layers and other renderables
//        var bgPosition = new Vector2(0f, -1.65f * height);
//        var bgSize = new Vector2((tilemap.cols+2) * tilemap.tileSize, (tilemap.rows+16) * tilemap.tileSize);
//        var background = Factory.background(ImageType.BG_WARP_ROOM, bgPosition, bgSize);

        // *** Order matters when adding renderables
//        engine.addEntity(background);
        engine.addEntity(map);

        // Create entities from tile layers
        for (var tileLayer : tilemap.layers) {
            var entity = Factory.createEntity();
            float depth = 0;
            switch (tileLayer.getName()) {
                case "background": depth = BACKROUND_Z_LEVEL; break;
                case "middle": depth = 0; break;
                case "foreground": depth = FOREGROUND_Z_LEVEL; break;
            }
            entity.add(new Position(mapPosition.x,  mapPosition.y));

            entity.add(new TileLayer(tilemap, tileLayer, depth));
            engine.addEntity(entity);
        }

        // Create entities from mapObjects
        for (var mapObject : tilemap.objects) {
            engine.addEntity(TilemapObject.createEntity(tilemap, mapObject));
        }

        var spawners = Util.streamOf(engine.getEntitiesFor(SPAWNERS))
            .map(e -> Components.get(e, TilemapObject.Spawner.class))
            .collect(Collectors.toList());

        for (var spawner : spawners) {
            if ("mario".equals(spawner.type)) {
                var mario = Factory.mario(spawner);
                engine.addEntity(mario);
            } else if ("sun".equals(spawner.type)) {
                var angrySun = Factory.angrySun(spawner);
                engine.addEntity(angrySun);
            } else {
                this.player = Factory.player(spawner);
                engine.addEntity(this.player);
            }
        }

        // Init view system to follow the player
        var viewSystem = screen.engine.getSystem(ViewSystem.class);
        viewSystem.target(player);
    }
}
