package lando.systems.ld58.game.scenes;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import lando.systems.ld58.game.Components;
import lando.systems.ld58.game.Factory;
import lando.systems.ld58.game.components.*;
import lando.systems.ld58.game.factories.MapFactory;
import lando.systems.ld58.game.signals.EntityEvent;
import lando.systems.ld58.game.signals.SignalEvent;
import lando.systems.ld58.screens.BaseScreen;
import lando.systems.ld58.utils.Util;

import static lando.systems.ld58.game.Constants.*;

public abstract class Scene<ScreenType extends BaseScreen> implements Listener<SignalEvent> {

    public static final Family SPAWNERS = Family.one(TilemapObject.Spawner.class).get();

    public final ScreenType screen;

    public Entity player;
    public Entity map;
    public Entity view;

    public Scene(ScreenType screen) {
        this.screen = screen;
        SignalEvent.addListener(this);
    }

    public ScreenType screen() { return screen; }
    public Engine engine()     { return screen.engine; }
    public Entity player()     { return player; }
    public Entity map()        { return map; }
    public Entity view()       { return view; }

    @Override
    public void receive(Signal<SignalEvent> signal, SignalEvent event) {
        if (event instanceof EntityEvent.Remove) {
            var remove = (EntityEvent.Remove) event;
            engine().removeEntity(remove.entity);
        }
    }

    public Entity spawnEntity(TilemapObject.Spawner spawner) {
        var entity = Factory.fromSpawner(spawner);
        screen.engine.addEntity(entity);
        return entity;
    }

    protected void createView(int viewportWidth, int viewportHeight) {
        // configure the camera to emulate a low res display
        // TODO: continue playing with some options here,
        //  probably best to stick with integer multiples of window size (1280x720)
//        var width  = 360; // window size / 4  ;  // old: 240;
//        var height = 180; // window size / 4  ;  // old: 160;
        var camera = screen.worldCamera;
        camera.setToOrtho(false, viewportWidth, viewportHeight);
        camera.update();

        // Set up the map view
        view = Factory.view(screen.worldCamera);
        screen.engine.addEntity(view);
    }

    protected void createMap(String mapPath) {
        map = MapFactory.map(mapPath, screen.worldCamera);
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

        // Spawn entities
        Util.streamOf(screen.engine.getEntitiesFor(SPAWNERS))
            .map(TilemapObject.Spawner::get)
            .forEach(spawner -> {
                var entity = spawnEntity(spawner);
                if (Components.has(entity, Player.class)) {
                    player = entity;
                }
            });
    }
}
