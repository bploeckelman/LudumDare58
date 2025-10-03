package lando.systems.ld58.game.signals;

import lando.systems.ld58.game.components.Cooldowns;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class CooldownEvent {

    public Cooldowns cooldowns;
    public Cooldowns cooldowns() { return cooldowns; }

    public static final class Reset extends CooldownEvent {
        public String name;

        public Reset(Cooldowns cooldowns, String name) {
            super(cooldowns);
            this.name = name;
        }
    }
}
