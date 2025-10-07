package lando.systems.ld58.game.scenes;

import lando.systems.ld58.game.systems.ViewSystem;
import lando.systems.ld58.screens.GameScreen;

public class SceneRelic3 extends Scene<GameScreen> {

    public SceneRelic3(GameScreen screen) {
        super(screen);

        createView(640, 360);
        createMap("maps/relic3.tmx");

        // Follow the player
        screen.engine.getSystem(ViewSystem.class).target(player);
    }
}
