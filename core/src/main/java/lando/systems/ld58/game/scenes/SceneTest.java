package lando.systems.ld58.game.scenes;

import com.badlogic.gdx.graphics.Color;
import lando.systems.ld58.assets.EmitterType;
import lando.systems.ld58.game.Components;
import lando.systems.ld58.game.Factory;
import lando.systems.ld58.game.components.Position;
import lando.systems.ld58.game.systems.ViewSystem;
import lando.systems.ld58.particles.effects.TestEffect;
import lando.systems.ld58.screens.GameScreen;

public class SceneTest extends Scene<GameScreen> {

    public SceneTest(GameScreen screen) {
        super(screen);

        setupView(640, 360);
        setupMap("maps/test.tmx");

        spawnEntities();

        // Follow the player
        screen.engine.getSystem(ViewSystem.class).target(player);

        // TEST: Attach a test particle emitter to the player
//        var target = Components.get(player, Position.class);
//        var params = new TestEffect.Params(target, Color.RED, 0.2f);
//        var emitter = Factory.emitter(EmitterType.TEST, params);
//        screen.engine.addEntity(emitter);
    }
}
