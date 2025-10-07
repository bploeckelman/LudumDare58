package lando.systems.ld58.game.scenes;

import lando.systems.ld58.game.systems.ViewSystem;
import lando.systems.ld58.screens.GameScreen;

public class SceneFinale extends Scene<GameScreen> {

    public SceneFinale(GameScreen screen) {
        super(screen);

        createView(640, 360);
        createMap("maps/finale.tmx");

        // Follow the player
        screen.engine.getSystem(ViewSystem.class).target(player);
    }
}
