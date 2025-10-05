package lando.systems.ld58.game;

import com.badlogic.ashley.core.Engine;
import lando.systems.ld58.game.systems.*;
import lando.systems.ld58.screens.BaseScreen;
import lando.systems.ld58.screens.GameScreen;

public class Systems {

    public static AnimationSystem        animation;
    public static AudioSystem            audio;
    public static CollisionCheckSystem   collisionCheck;
    public static CollisionHandlerSystem collisionHandler;
    public static CooldownSystem         cooldown;
    public static EnemySystem            enemies;
    public static InputSystem            input;
    public static InterpSystem           interp;
    public static MovementSystem         movement;
    public static ParticleSystem         particles;
    public static RenderDebugSystem      renderDebug;
    public static RenderSystem           render;
    public static ViewSystem             view;

    public static PlayerStateSystem<? extends BaseScreen> playerState;

    public static void init(Engine engine) {
        Systems.animation        = new AnimationSystem();
        Systems.audio            = new AudioSystem();
        Systems.collisionCheck   = new CollisionCheckSystem();
        Systems.collisionHandler = new CollisionHandlerSystem();
        Systems.cooldown         = new CooldownSystem();
        Systems.enemies          = new EnemySystem();
        Systems.input            = new InputSystem();
        Systems.interp           = new InterpSystem();
        Systems.movement         = new MovementSystem();
        Systems.particles        = new ParticleSystem();
        Systems.renderDebug      = new RenderDebugSystem();
        Systems.render           = new RenderSystem();
        Systems.view             = new ViewSystem();

        engine.addSystem(animation);
        engine.addSystem(audio);
        engine.addSystem(collisionCheck);
        engine.addSystem(collisionHandler);
        engine.addSystem(cooldown);
        engine.addSystem(enemies);
        engine.addSystem(input);
        engine.addSystem(interp);
        engine.addSystem(movement);
        engine.addSystem(particles);
        engine.addSystem(renderDebug);
        engine.addSystem(render);
        engine.addSystem(view);
    }
}
