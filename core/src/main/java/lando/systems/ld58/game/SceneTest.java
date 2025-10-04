package lando.systems.ld58.game;

import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.utils.GdxRuntimeException;
import lando.systems.ld58.assets.ImageType;
import lando.systems.ld58.game.components.*;
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
        var startPaused = true;
        var scrollDurationSecs = 5 * 60;
        view = Factory.view(scrollDurationSecs, startPaused);
        engine.addEntity(view);

        // Load the background
        var background = Factory.background(ImageType.BG_WARP_ROOM);
        engine.addEntity(background);

        // Load the map
        var mapPath = "maps/_test.tmx";
        map = Factory.map(mapPath);
        engine.addEntity(map);

        // Create entities from mapObjects
        var tilemap = Components.get(map, Tilemap.class);
        for (var mapObject : tilemap.objects) {
            engine.addEntity(TilemapObject.createEntity(tilemap, mapObject));
        }

        var spawner = Util.streamOf(engine.getEntitiesFor(SPAWNERS))
            .map(e -> Components.get(e, TilemapObject.Spawner.class))
            .findFirst()
            .orElseThrow(() -> new GdxRuntimeException("no spawner found in map:" + mapPath));

        this.player = Factory.player(spawner);
        engine.addEntity(this.player);

        // Init view system to scroll within the map bounds
        var viewSystem = screen.engine.getSystem(ViewSystem.class);
        var viewer = Components.optional(view, Viewer.class).orElseThrow();
        var interp = Components.optional(view, Interp.class).orElseThrow();
        var bounds = Components.optional(map, Bounds.class).orElseThrow();
        viewSystem.target(viewer, interp, bounds);
    }
}
