package lando.systems.ld58.game.scenes;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import lando.systems.ld58.game.Signals;
import lando.systems.ld58.game.signals.EntityEvent;
import lando.systems.ld58.screens.BaseScreen;

public abstract class Scene<ScreenType extends BaseScreen> implements Listener<EntityEvent> {

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
}
