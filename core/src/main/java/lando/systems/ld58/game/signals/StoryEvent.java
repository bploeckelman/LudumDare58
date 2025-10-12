package lando.systems.ld58.game.signals;

import lando.systems.ld58.game.components.Story;

public interface StoryEvent extends SignalEvent {

    static void advance()              { signal.dispatch(new Advance()); }
    static void completed(Story story) { signal.dispatch(new Completed(story)); }

    class Advance implements StoryEvent {}

    class Completed implements StoryEvent {
        public final String storyId;
        private Completed(Story story) {
            this.storyId = story.id;
            story.completionDispatched = true;
        }
    }
}
