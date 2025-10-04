package lando.systems.ld58.game.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import lando.systems.ld58.game.Signals;
import lando.systems.ld58.game.signals.AudioEvent;

public class AudioSystem extends EntitySystem implements Listener<AudioEvent> {

    public AudioSystem() {
        Signals.playSound.add(this);
        Signals.playMusic.add(this);
    }

    @Override
    public void receive(Signal<AudioEvent> signal, AudioEvent event) {
        if (event instanceof AudioEvent.PlaySound) {
            var play = (AudioEvent.PlaySound) event;
            play.soundType.get().play(play.volume);
        }
        else if (event instanceof AudioEvent.PlayMusic) {
            var play = (AudioEvent.PlayMusic) event;
            var music = play.musicType.get();
            music.setVolume(play.volume);
            music.play();
        }
    }
}
