package lando.systems.ld58.game.components;

import com.badlogic.ashley.core.Component;
import lando.systems.ld58.assets.AnimType;
import lando.systems.ld58.assets.FontType2;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

public class Story implements Component {

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
    private boolean completedTyping;
    private boolean shouldClear;  // NEW: flag to indicate story should be removed

    public final boolean pauseGame;

    public Story(Dialog... dialogs) {
        this(true, dialogs);
    }

    public Story(boolean pauseGame, Dialog... dialogs) {
        this.pauseGame = pauseGame;
        this.dialogs = Arrays.asList(dialogs);
        this.index = -1;
        this.started = false;
        this.completed = this.dialogs.isEmpty();
        this.completedTyping = false;
    }

    public List<Dialog> dialogs() { return dialogs; }
    public int index() { return index; }
    public boolean isStarted() { return started; }
    public boolean isComplete() { return completed; }
    public boolean isTyping() { return !completedTyping; }
    public boolean shouldClear() { return shouldClear; }

    public void finishTyping() { completedTyping = true; }

    public void clear() {
        if (!completed) return;
        shouldClear = true;
    }

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
            completedTyping = true;
        } else {
            completedTyping = false;
        }
    }

    public boolean next() {
        if (!started) return false;

        index++;
        if (index >= dialogs.size()) {
            index = -1;
            completed = true;
        } else {
            completedTyping = false;
        }

        return completed;
    }
}
