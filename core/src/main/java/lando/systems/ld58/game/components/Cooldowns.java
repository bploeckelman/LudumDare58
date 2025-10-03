package lando.systems.ld58.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Cooldowns implements Component {

    public static final ComponentMapper<Cooldowns> mapper = ComponentMapper.getFor(Cooldowns.class);

    public static Optional<Cooldowns> get(Entity entity) {
        return Optional.ofNullable(mapper.get(entity));
    }

    private final Map<String, Entry> entryByName;

    public Cooldowns() {
        this.entryByName = new HashMap<>();
    }

    public Collection<Entry> entries() {
        return entryByName.values();
    }

    public Cooldowns add(String name, float max) {
        if (entryByName.containsKey(name)) {
            throw new GdxRuntimeException("Entry with name " + name + " already exists");
        }
        entryByName.put(name, new Entry(name, max));
        return this;
    }

    public Cooldowns remove(String name) {
        entryByName.remove(name);
        return this;
    }

    public Optional<Entry> get(String name) {
        return Optional.ofNullable(entryByName.get(name));
    }

    public boolean isReady(String name) {
        return get(name).map(Entry::isReady).orElse(false);
    }

    public void reset(String name) {
        get(name).ifPresent(Entry::reset);
    }

    public void pauseAll() { entryByName.values().forEach(Entry::pause); }
    public void resumeAll() { entryByName.values().forEach(Entry::resume); }
    public void resetAll() { entryByName.values().forEach(Entry::reset); }

    public static class Entry {
        public final String name;
        public final float max;

        public float current;
        public boolean active;

        public Entry(String name, float max) {
            this.name = name;
            this.max = max;
            this.current = max;
            this.active = true;
        }

        public boolean isReady() {
            return active && current == 0;
        }

        public void update(float delta) {
            if (!active) return;
            current = Math.max(0, current - delta);
        }

        public String name() { return name; }
        public float max() { return max; }
        public float current() { return current; }
        public boolean active() { return active; }

        public void reset() { current = max; }
        public void zero() { current = 0; }
        public float percent() { return 1f - (current / max); }

        public void pause() { active = false; }
        public void resume() { active = true; }
    }
}
