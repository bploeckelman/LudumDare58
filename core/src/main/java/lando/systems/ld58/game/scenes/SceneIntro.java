package lando.systems.ld58.game.scenes;

import lando.systems.ld58.game.systems.ViewSystem;
import lando.systems.ld58.screens.IntroScreen;

public class SceneIntro extends Scene<IntroScreen> {

    public SceneIntro(IntroScreen screen) {
        super(screen);

        setupView(640, 360);
        setupMap("maps/intro.tmx");

        spawnEntities();

        // Follow the player
        screen.engine.getSystem(ViewSystem.class).target(player);
    }
}
