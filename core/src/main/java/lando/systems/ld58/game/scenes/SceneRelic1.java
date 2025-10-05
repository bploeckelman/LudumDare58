package lando.systems.ld58.game.scenes;

import lando.systems.ld58.game.systems.ViewSystem;
import lando.systems.ld58.screens.GameScreen;

public class SceneRelic1 extends Scene<GameScreen> {

    public SceneRelic1(GameScreen screen) {
        super(screen);

        setupView(640, 360);
        setupMap("maps/relic1.tmx");

        spawnEntities();

        // Follow the player
        screen.engine.getSystem(ViewSystem.class).target(player);
    }
}
