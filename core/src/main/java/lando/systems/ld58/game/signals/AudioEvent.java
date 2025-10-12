package lando.systems.ld58.game.signals;

import lando.systems.ld58.assets.MusicType;
import lando.systems.ld58.assets.SoundType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

public abstract class AudioEvent implements SignalEvent {

    public static void playSound(SoundType sound)               { signal.dispatch(new PlaySound(sound)); }
    public static void playSound(SoundType sound, float volume) { signal.dispatch(new PlaySound(sound, volume)); }
    public static void playMusic(MusicType music)               { signal.dispatch(new PlayMusic(music)); }
    public static void playMusic(MusicType music, float volume) { signal.dispatch(new PlayMusic(music, volume)); }
    public static void stopMusic(MusicType music)               { signal.dispatch(new StopMusic(music)); }
    public static void stopAllMusic()                           { signal.dispatch(new StopMusic()); }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class PlaySound extends AudioEvent {
        public final SoundType soundType;
        public final float volume;
        private PlaySound(SoundType soundType) { this(soundType, 1f); }
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class PlayMusic extends AudioEvent {
        public final MusicType musicType;
        public final float volume;
        private PlayMusic(MusicType musicType) { this(musicType, 1f); }
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class StopMusic extends AudioEvent {
        public final MusicType musicType;
        private StopMusic() { this(null); }
    }
}
