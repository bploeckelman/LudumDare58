package lando.systems.ld58.game;


import lando.systems.ld58.game.systems.*;
import lando.systems.ld58.screens.BaseScreen;

public class Systems {

    public static AnimationSystem        animation;
    public static AudioSystem            audio;
    public static CollisionCheckSystem   collisionCheck;
    public static CollisionHandlerSystem collisionHandler;
    public static CooldownSystem         cooldown;
    public static InputSystem            input;
    public static InterpSystem           interp;
    public static MovementSystem         movement;
    public static PlayerStateSystem      playerState;
    public static RenderDebugSystem      renderDebug;
    public static RenderSystem           render;
    public static ViewSystem             view;

    public static void init(BaseScreen screen) {
        Systems.animation        = new AnimationSystem();
        Systems.audio            = new AudioSystem();
        Systems.collisionCheck   = new CollisionCheckSystem();
        Systems.collisionHandler = new CollisionHandlerSystem();
        Systems.cooldown         = new CooldownSystem();
        Systems.playerState      = new PlayerStateSystem(screen);
        Systems.input            = new InputSystem();
        Systems.interp           = new InterpSystem();
        Systems.movement         = new MovementSystem();
        Systems.renderDebug      = new RenderDebugSystem();
        Systems.render           = new RenderSystem();
        Systems.view             = new ViewSystem();

        var engine = screen.engine;
        engine.addSystem(animation);
        engine.addSystem(audio);
        engine.addSystem(collisionCheck);
        engine.addSystem(collisionHandler);
        engine.addSystem(cooldown);
        engine.addSystem(input);
        engine.addSystem(interp);
        engine.addSystem(movement);
        engine.addSystem(playerState);
        engine.addSystem(renderDebug);
        engine.addSystem(render);
        engine.addSystem(view);
    }
}
