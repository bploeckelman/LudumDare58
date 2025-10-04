package lando.systems.ld58.game;

import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxRuntimeException;
import lando.systems.ld58.assets.ImageType;
import lando.systems.ld58.game.components.Tilemap;
import lando.systems.ld58.game.components.TilemapObject;
import lando.systems.ld58.game.systems.ViewSystem;
import lando.systems.ld58.screens.GameScreen;
import lando.systems.ld58.utils.Util;

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
        var mapPath = "maps/_test.tmx";
        map = Factory.map(mapPath);
        var tilemap = Components.get(map, Tilemap.class);

        // Load the background
        var bgPosition = new Vector2();
        // TODO: the size handling is wrong here, probably a bug in Image or Renderable
        var bgSize = new Vector2(tilemap.cols * tilemap.tileSize, tilemap.rows * tilemap.tileSize);
        var background = Factory.background(ImageType.BG_WARP_ROOM, bgPosition, bgSize);

        // *** Order matters when adding renderables
        engine.addEntity(background);
        engine.addEntity(map);

        // Create entities from mapObjects
        for (var mapObject : tilemap.objects) {
            engine.addEntity(TilemapObject.createEntity(tilemap, mapObject));
        }

        var spawner = Util.streamOf(engine.getEntitiesFor(SPAWNERS))
            .map(e -> Components.get(e, TilemapObject.Spawner.class))
            .findFirst()
            .orElseThrow(() -> new GdxRuntimeException("no spawner found in map:" + mapPath));

        this.player = Factory.player(spawner);
        engine.addEntity(this.player);

        // Init view system to follow the player
        var viewSystem = screen.engine.getSystem(ViewSystem.class);
        viewSystem.target(player);
    }
}
