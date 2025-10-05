package lando.systems.ld58.game.scenes;

import lando.systems.ld58.game.systems.ViewSystem;
import lando.systems.ld58.screens.GameScreen;

public class SceneFinale extends Scene<GameScreen> {

    public SceneFinale(GameScreen screen) {
        super(screen);

        setupView(640, 360);
        setupMap("maps/finale.tmx");

        spawnEntities();

        // Follow the player
        screen.engine.getSystem(ViewSystem.class).target(player);
    }
}
