package lando.systems.ld58.game;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.GdxRuntimeException;
import lando.systems.ld58.game.components.*;
import lando.systems.ld58.game.components.enemies.EnemyAngrySun;
import lando.systems.ld58.game.components.enemies.EnemyMario;
import lando.systems.ld58.game.components.renderable.Animator;
import lando.systems.ld58.game.components.renderable.Image;
import lando.systems.ld58.game.components.renderable.KirbyShaderRenderable;
import lando.systems.ld58.game.components.renderable.Outline;

import java.util.Map;
import java.util.Optional;

import static java.util.Map.entry;

public class Components {

    private static final Map<Class<? extends Component>, ComponentMapper<?>> mappers = Map.ofEntries(
        entry(Bounds.class,         ComponentMapper.getFor(Bounds.class)),
        entry(Collider.class,       ComponentMapper.getFor(Collider.class)),
        entry(Cooldowns.class,      ComponentMapper.getFor(Cooldowns.class)),
        entry(Friction.class,       ComponentMapper.getFor(Friction.class)),
        entry(Gravity.class,        ComponentMapper.getFor(Gravity.class)),
        entry(Id.class,             ComponentMapper.getFor(Id.class)),
        entry(Input.class,          ComponentMapper.getFor(Input.class)),
        entry(Interp.class,         ComponentMapper.getFor(Interp.class)),
        entry(Name.class,           ComponentMapper.getFor(Name.class)),
        entry(Player.class,         ComponentMapper.getFor(Player.class)),
        entry(Position.class,       ComponentMapper.getFor(Position.class)),
        entry(SceneContainer.class, ComponentMapper.getFor(SceneContainer.class)),
        entry(Sensors.class,        ComponentMapper.getFor(Sensors.class)),
        entry(TileLayer.class,      ComponentMapper.getFor(TileLayer.class)),
        entry(Tilemap.class,        ComponentMapper.getFor(Tilemap.class)),
        entry(Velocity.class,       ComponentMapper.getFor(Velocity.class)),
        entry(Viewer.class,         ComponentMapper.getFor(Viewer.class)),

        entry(Animator.class,              ComponentMapper.getFor(Animator.class)),
        entry(Image.class,                 ComponentMapper.getFor(Image.class)),
        entry(KirbyShaderRenderable.class, ComponentMapper.getFor(KirbyShaderRenderable.class)),
        entry(Outline.class,               ComponentMapper.getFor(Outline.class)),

        entry(EnemyMario.class,     ComponentMapper.getFor(EnemyMario.class)),
        entry(EnemyAngrySun.class,  ComponentMapper.getFor(EnemyAngrySun.class)),

        entry(TilemapObject.Simple.class,  ComponentMapper.getFor(TilemapObject.Simple.class)),
        entry(TilemapObject.Spawner.class, ComponentMapper.getFor(TilemapObject.Spawner.class))
    );

    private Components() { /* don't allow instantiation */ }

    /**
     * Gets a component of the specified type from an entity.
     *
     * @param entity The entity to get the component from.
     * @param componentClass The class of the component to retrieve.
     * @param <T> The component type.
     * @return The component instance, or null if the entity does not have it.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Component> T get(Entity entity, Class<T> componentClass) {
        var mapper = mappers.get(componentClass);
        if (mapper == null) {
            throw new GdxRuntimeException("No mapper for component type: " + componentClass.getSimpleName());
        }
        return (T) mapper.get(entity);
    }

    /**
     * Gets a component of the specified type from an entity, wrapped in an Optional.
     *
     * @param entity The entity to get the component from.
     * @param componentClass The class of the component to retrieve.
     * @param <T> The component type.
     * @return An Optional containing the component, or an empty Optional if not present.
     */
    public static <T extends Component> Optional<T> optional(Entity entity, Class<T> componentClass) {
        return Optional.ofNullable(get(entity, componentClass));
    }


    public static <T extends Component> boolean has(Entity entity, Class<T> componentClass) {
        var mapper = mappers.get(componentClass);
        if (mapper == null) {
            throw new GdxRuntimeException("No mapper for component type: " + componentClass.getSimpleName());
        }
        return mapper.has(entity);
    }

    public static boolean hasEnemyComponent(Entity entity) {
        return Components.has(entity, EnemyMario.class)
            || Components.has(entity, EnemyAngrySun.class);
    }
}
