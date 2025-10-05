package lando.systems.ld58.game.scenes;

import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.Color;
import lando.systems.ld58.assets.EmitterType;
import lando.systems.ld58.game.Components;
import lando.systems.ld58.game.Factory;
import lando.systems.ld58.game.components.Position;
import lando.systems.ld58.game.components.TileLayer;
import lando.systems.ld58.game.components.Tilemap;
import lando.systems.ld58.game.components.TilemapObject;
import lando.systems.ld58.game.systems.ViewSystem;
import lando.systems.ld58.particles.effects.TestEffect;
import lando.systems.ld58.screens.GameScreen;
import lando.systems.ld58.utils.Util;

import java.util.stream.Collectors;

import static lando.systems.ld58.game.Constants.*;

public class SceneTest extends Scene<GameScreen> {

    public static final Family SPAWNERS = Family.one(TilemapObject.Spawner.class).get();

    public SceneTest(GameScreen screen) {
        super(screen);
        var engine = screen.engine;

        // configure the camera to emulate a low res display
        // TODO: continue playing with some options here,
        //  probably best to stick with integer multiples of window size (1280x720)
//        var width  = 360; // window size / 4  ;  // old: 240;
//        var height = 180; // window size / 4  ;  // old: 160;
        var width  = 640;
        var height = 360;
        var camera = screen.worldCamera;
        camera.setToOrtho(false, width, height);
        camera.update();

        // Set up the map view
        view = Factory.view();
        engine.addEntity(view);

        // Load the map
        var mapPath = "maps/test.tmx";
//        var mapPath = "maps/test2.tmx";
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
                case "background": depth = Z_DEPTH_BACKGROUND; break;
                case "middle":     depth = Z_DEPTH_DEFAULT;    break;
                case "foreground": depth = Z_DEPTH_FOREGROUND; break;
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
            //@formatter:off
            switch (spawner.type) {
                case "mario":  engine.addEntity(Factory.mario(spawner));        break;
                case "sun":    engine.addEntity(Factory.angrySun(spawner));     break;
                case "goomba": engine.addEntity(Factory.goombaCyborg(spawner)); break;
                case "lou":    engine.addEntity(Factory.captainLou(spawner));   break;
                case "misty":  engine.addEntity(Factory.misty(spawner));        break;
                case "bullet": engine.addEntity(Factory.bulletBill(spawner));   break;
                case "hammer": engine.addEntity(Factory.hammerBro(spawner));    break;
                case "koopa":  engine.addEntity(Factory.koopa(spawner));        break;
                case "lakitu": engine.addEntity(Factory.lakitu(spawner));       break;
                default: {
                    this.player = Factory.player(spawner);
                    engine.addEntity(this.player);
                } break;
            }
            //@formatter:on
        }

        // Init view system to follow the player
        var viewSystem = engine.getSystem(ViewSystem.class);
        viewSystem.target(player);

        // TEST: Attach a test particle emitter to the player
        var target = Components.get(player, Position.class);
        var params = new TestEffect.Params(target, Color.RED, 0.2f);
        var emitter = Factory.emitter(EmitterType.TEST, params);
        engine.addEntity(emitter);
    }
}
