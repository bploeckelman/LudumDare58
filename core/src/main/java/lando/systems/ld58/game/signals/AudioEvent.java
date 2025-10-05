package lando.systems.ld58.game.signals;

import lando.systems.ld58.assets.MusicType;
import lando.systems.ld58.assets.SoundType;
import lombok.AllArgsConstructor;

public abstract class AudioEvent {

    @AllArgsConstructor
    public static final class PlaySound extends AudioEvent {

        public final SoundType soundType;
        public final float volume;

        public PlaySound(SoundType soundType) {
            this(soundType, 1f);
        }
    }

    @AllArgsConstructor
    public static final class PlayMusic extends AudioEvent {

        public final MusicType musicType;
        public final float volume;

        public PlayMusic(MusicType musicType) {
            this(musicType, 1f);
        }
    }

    @AllArgsConstructor
    public static final class StopMusic extends AudioEvent {

        public final MusicType musicType;

        /**
         * Stop all music that's been started as a {@link MusicType}
         */
        public StopMusic() {
            this(null);
        }
    }
}
