package lando.systems.ld58.game.signals;

public interface StoryEvent {
    class Advance implements StoryEvent {}

    static StoryEvent advance() { return new Advance(); }
}
