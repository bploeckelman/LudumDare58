package lando.systems.ld58.game.actions.types;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import lando.systems.ld58.assets.MusicType;
import lando.systems.ld58.game.Signals;
import lando.systems.ld58.game.actions.Action;
import lando.systems.ld58.game.signals.AudioEvent;

public class PlayMusicAction extends Action {

    private static final String TAG = PlayMusicAction.class.getSimpleName();

    private final MusicType musicType;
    private boolean dispatched;

    public PlayMusicAction(MusicType musicType) {
        this.musicType = musicType;
        this.dispatched = false;
    }

    @Override
    public void start(Entity entity, Engine engine) {
        Signals.playMusic.dispatch(new AudioEvent.PlayMusic(musicType));
        dispatched = true;
    }

    @Override
    public void update(Entity entity, Engine engine, float delta) {/* nothing to do */}

    @Override
    public boolean isComplete() {
        return dispatched;
    }
}
