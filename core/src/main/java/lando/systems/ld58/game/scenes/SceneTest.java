package lando.systems.ld58.game.scenes;

import lando.systems.ld58.game.systems.ViewSystem;
import lando.systems.ld58.screens.GameScreen;

public class SceneTest extends Scene<GameScreen> {

    public SceneTest(GameScreen screen) {
        super(screen);

        createView(640, 360);
        createMap("maps/test.tmx");

        // Follow the player
        screen.engine.getSystem(ViewSystem.class).target(player);

        // TEST: Attach a test particle emitter to the player
//        var target = Components.get(player, Position.class);
//        var params = new TestEffect.Params(target, Color.RED, 0.2f);
//        var emitter = Factory.emitter(EmitterType.TEST, params);
//        screen.engine.addEntity(emitter);
    }
}
