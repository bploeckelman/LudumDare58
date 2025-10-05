package lando.systems.ld58.game.scenes;

import lando.systems.ld58.game.systems.ViewSystem;
import lando.systems.ld58.screens.GameScreen;

public class SceneRelic2 extends Scene<GameScreen> {

    public SceneRelic2(GameScreen screen) {
        super(screen);

        setupView(640, 360);
        setupMap("maps/relic2.tmx");

        spawnEntities();

        // Follow the player
        screen.engine.getSystem(ViewSystem.class).target(player);
    }
}
