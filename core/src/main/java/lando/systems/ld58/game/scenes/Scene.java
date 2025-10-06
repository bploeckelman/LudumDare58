package lando.systems.ld58.game.scenes;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import lando.systems.ld58.game.Components;
import lando.systems.ld58.game.Factory;
import lando.systems.ld58.game.Signals;
import lando.systems.ld58.game.components.Position;
import lando.systems.ld58.game.components.TileLayer;
import lando.systems.ld58.game.components.Tilemap;
import lando.systems.ld58.game.components.TilemapObject;
import lando.systems.ld58.game.signals.EntityEvent;
import lando.systems.ld58.screens.BaseScreen;
import lando.systems.ld58.utils.Util;

import java.util.stream.Collectors;

import static lando.systems.ld58.game.Constants.*;

public abstract class Scene<ScreenType extends BaseScreen> implements Listener<EntityEvent> {

    public static final Family SPAWNERS = Family.one(TilemapObject.Spawner.class).get();

    public final ScreenType screen;

    public Entity player;
    public Entity map;
    public Entity view;

    public Scene(ScreenType screen) {
        this.screen = screen;
        Signals.removeEntity.add(this);
    }

    public ScreenType screen() { return screen; }
    public Engine engine()     { return screen.engine; }
    public Entity player()     { return player; }
    public Entity map()        { return map; }
    public Entity view()       { return view; }

    public void receive(Signal<EntityEvent> signal, EntityEvent event) {
        if (event instanceof EntityEvent.Remove) {
            var remove = (EntityEvent.Remove) event;
            engine().removeEntity(remove.entity);
        }
    }

    public void spawnEntity(TilemapObject.Spawner spawner) {
        //@formatter:off
        switch (spawner.type) {
            case "mario":   screen.engine.addEntity(Factory.mario(spawner));        break;
            case "sun":     screen.engine.addEntity(Factory.angrySun(spawner));     break;
            case "goomba":  screen.engine.addEntity(Factory.goombaCyborg(spawner)); break;
            case "lou":     screen.engine.addEntity(Factory.captainLou(spawner));   break;
            case "misty":   screen.engine.addEntity(Factory.misty(spawner));        break;
            case "bullet":  screen.engine.addEntity(Factory.bulletBill(spawner));   break;
            case "hammer":  screen.engine.addEntity(Factory.hammerBro(spawner));    break;
            case "koopa":   screen.engine.addEntity(Factory.koopa(spawner));        break;
            case "lakitu":  screen.engine.addEntity(Factory.lakitu(spawner));       break;
            case "coin":    screen.engine.addEntity(Factory.coin(spawner));         break;
            case "plunger": screen.engine.addEntity(Factory.relic(spawner));        break;
            case "torch":   screen.engine.addEntity(Factory.relic(spawner));        break;
            case "wrench":  screen.engine.addEntity(Factory.relic(spawner));        break;
            default: {
                this.player = Factory.player(spawner);
                screen.engine.addEntity(this.player);
            } break;
        }
        //@formatter:on
    }

    protected void setupView(int viewportWidth, int viewportHeight) {
        // configure the camera to emulate a low res display
        // TODO: continue playing with some options here,
        //  probably best to stick with integer multiples of window size (1280x720)
//        var width  = 360; // window size / 4  ;  // old: 240;
//        var height = 180; // window size / 4  ;  // old: 160;
        var camera = screen.worldCamera;
        camera.setToOrtho(false, viewportWidth, viewportHeight);
        camera.update();

        // Set up the map view
        view = Factory.view();
        screen.engine.addEntity(view);
    }

    protected void setupMap(String mapPath) {
        map = Factory.map(mapPath);
        screen.engine.addEntity(map);

        var tilemap = Components.get(map, Tilemap.class);
        var mapPosition = Components.get(map, Position.class);

        // Create entities for tile layers
        for (var tileLayer : tilemap.layers) {
            var entity = Factory.createEntity();
            float depth = Z_DEPTH_DEFAULT;
            switch (tileLayer.getName()) {
                case "background": depth = Z_DEPTH_BACKGROUND; break;
                case "middle":     depth = Z_DEPTH_DEFAULT;    break;
                case "foreground": depth = Z_DEPTH_FOREGROUND; break;
            }
            entity.add(new Position(mapPosition.x,  mapPosition.y));
            entity.add(new TileLayer(tilemap, tileLayer, depth));
            screen.engine.addEntity(entity);
        }

        // Create entities for mapObjects
        for (var mapObject : tilemap.objects) {
            var entity = TilemapObject.createEntity(tilemap, mapObject);
            screen.engine.addEntity(entity);
        }
    }

    protected void spawnEntities() {
        var spawners = Util.streamOf(screen.engine.getEntitiesFor(SPAWNERS))
            .map(e -> Components.get(e, TilemapObject.Spawner.class))
            .collect(Collectors.toList());

        for (var spawner : spawners) {
            spawnEntity(spawner);
        }
    }
}
