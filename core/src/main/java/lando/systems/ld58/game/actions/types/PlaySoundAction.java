package lando.systems.ld58.game.actions.types;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import lando.systems.ld58.assets.SoundType;
import lando.systems.ld58.game.Signals;
import lando.systems.ld58.game.actions.Action;
import lando.systems.ld58.game.signals.AudioEvent;

public class PlaySoundAction extends Action {

    private static final String TAG = PlaySoundAction.class.getSimpleName();

    private final SoundType soundType;
    private boolean dispatched;

    public PlaySoundAction(SoundType soundType) {
        this.soundType = soundType;
        this.dispatched = false;
    }

    @Override
    public void start(Entity entity, Engine engine) {
        Signals.playSound.dispatch(new AudioEvent.PlaySound(soundType));
        dispatched = true;
    }

    @Override
    public void update(Entity entity, Engine engine, float delta) {/* nothing to do */}

    @Override
    public boolean isComplete() {
        return dispatched;
    }
}
