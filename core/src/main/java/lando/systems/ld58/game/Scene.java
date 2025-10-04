package lando.systems.ld58.game;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.utils.GdxRuntimeException;
import lando.systems.ld58.assets.ImageType;
import lando.systems.ld58.game.components.*;
import lando.systems.ld58.game.systems.ViewSystem;
import lando.systems.ld58.screens.BaseScreen;
import lando.systems.ld58.utils.Util;

public abstract class Scene<ScreenType extends BaseScreen> {

    public final ScreenType screen;

    public Entity player;
    public Entity map;
    public Entity view;

    public Scene(ScreenType screen) {
        this.screen = screen;
    }

    public ScreenType screen() { return screen; }
    public Engine engine()     { return screen.engine; }
    public Entity player()     { return player; }
    public Entity map()        { return map; }
    public Entity view()       { return view; }
}
