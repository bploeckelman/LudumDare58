package lando.systems.ld58.game;

import com.badlogic.ashley.core.Engine;
import lando.systems.ld58.game.systems.*;
import lando.systems.ld58.screens.BaseScreen;

public class Systems {

    public static AnimationSystem        animation;
    public static AudioSystem            audio;
    public static CollisionCheckSystem   collisionCheck;
    public static CollisionHandlerSystem collisionHandler;
    public static CooldownSystem         cooldown;
    public static EnemySystem            enemies;
    public static InputSystem            input;
    public static InterpSystem           interp;
    public static MapTriggerSystem       mapTrigger;
    public static MovementSystem         movement;
    public static OffscreenRespawnSystem offscreenRespawn;
    public static ParticleSystem         particles;
    public static RenderDebugSystem      renderDebug;
    public static RenderSystem           render;
    public static StorySystem            story;
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
        Systems.mapTrigger       = new MapTriggerSystem();
        Systems.movement         = new MovementSystem();
        Systems.offscreenRespawn = new OffscreenRespawnSystem();
        Systems.particles        = new ParticleSystem();
        Systems.renderDebug      = new RenderDebugSystem();
        Systems.render           = new RenderSystem();
        Systems.story            = new StorySystem();
        Systems.view             = new ViewSystem();

        engine.addSystem(animation);
        engine.addSystem(audio);
        engine.addSystem(collisionCheck);
        engine.addSystem(collisionHandler);
        engine.addSystem(cooldown);
        engine.addSystem(enemies);
        engine.addSystem(input);
        engine.addSystem(interp);
        engine.addSystem(mapTrigger);
        engine.addSystem(movement);
        engine.addSystem(offscreenRespawn);
        engine.addSystem(particles);
        engine.addSystem(renderDebug);
        engine.addSystem(render);
        engine.addSystem(story);
        engine.addSystem(view);
    }
}
