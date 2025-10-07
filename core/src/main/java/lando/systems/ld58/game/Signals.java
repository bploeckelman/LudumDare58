package lando.systems.ld58.game;

import com.badlogic.ashley.signals.Signal;
import lando.systems.ld58.game.signals.*;

public class Signals {

    public static final Signal<AnimationEvent> animFacing = new Signal<>();
    public static final Signal<AnimationEvent> animScale  = new Signal<>();
    public static final Signal<AnimationEvent> animStart  = new Signal<>();

    public static final Signal<AudioEvent> playSound = new Signal<>();
    public static final Signal<AudioEvent> playMusic = new Signal<>();
    public static final Signal<AudioEvent> stopMusic = new Signal<>();

    public static final Signal<CollisionEvent> collision = new Signal<>();

    public static final Signal<CooldownEvent> cooldownReset = new Signal<>();

    public static final Signal<StateEvent> changeState = new Signal<>();

    public static final Signal<EntityEvent> removeEntity = new Signal<>();

    public static final Signal<TriggerEvent> dialogTrigger = new Signal<>();
    public static final Signal<TriggerEvent> collectTrigger = new Signal<>();
}
