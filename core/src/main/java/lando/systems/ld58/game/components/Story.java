package lando.systems.ld58.game.components;

import com.badlogic.ashley.core.Component;
import lando.systems.ld58.assets.AnimType;
import lando.systems.ld58.assets.FontType2;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

public class Story implements Component {

    // TODO: need to keep track of a set of dialog elements each of which include:
    //  - String for text to be displayed in a single dialog
    //  - AnimType or Image ref for a 'talking head' to display alongside the string in a single dialog
    //  - possibly an id to instantiate an entity with a story component from a map trigger
    //  - timing / 'isDone' fields
    //  - possibly a flag to indicate that the game should be paused while showing the dialogs, but that could also be a flag in StorySystem instead

    @RequiredArgsConstructor
    public static class Dialog {
        public final FontType2 fontType;
        public final AnimType animType;
        public final String text;
    }

    private final List<Dialog> dialogs;
    private int index;
    private boolean started;
    private boolean completed;

    public Story(Dialog... dialogs) {
        this.dialogs = Arrays.asList(dialogs);
        this.index = -1;
        this.started = false;
        this.completed = this.dialogs.isEmpty();
    }

    public List<Dialog> dialogs() { return dialogs; }
    public int index() { return index; }
    public boolean isStarted() { return started; }
    public boolean isComplete() { return completed; }

    public Dialog currentDialog() {
        if (index < 0 || index >= dialogs.size()) return null;
        return dialogs.get(index);
    }

    public void start() {
        if (started) return;
        started = true;

        index = dialogs.isEmpty() ? -1 : 0;
        if (index == -1) {
            completed = true;
        }
    }

    public boolean next() {
        if (!started) return false;

        index++;
        if (index >= dialogs.size()) {
            index = -1;
            completed = true;
        }

        return completed;
    }
}
