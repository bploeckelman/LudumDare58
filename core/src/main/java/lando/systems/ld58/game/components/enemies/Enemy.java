package lando.systems.ld58.game.components.enemies;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import lando.systems.ld58.assets.AnimType;
import lando.systems.ld58.game.Components;

import java.util.List;
import java.util.Map;

public abstract class Enemy {

    public enum Behavior { PATROL, CUSTOM }
    public enum PatrolState { IDLE, MOVE }

    public Behavior behavior = Behavior.PATROL;

    public PatrolState patrolState = PatrolState.IDLE;
    public float stateTime = 0f;
    public int direction = 0;
    public float walkAccel = 300f;

    public static final List<Class<? extends Enemy>> ENEMY_TYPES = List.of(
          EnemyAngrySun.class
        , EnemyBulletBill.class
        , EnemyCaptainLou.class
        , EnemyGoombaCyborg.class
        , EnemyHammerBro.class
        , EnemyKoopa.class
        , EnemyLakitu.class
        , EnemyMario.class
        , EnemyMisty.class
    );
    // NOTE: these can't just be a copy of ENEMY_TYPES because the base type has different generic bounds
    @SuppressWarnings("unchecked")
    public static final Class<? extends Component>[] ENEMY_COMPONENT_TYPES_ARR = new Class[] {
          EnemyAngrySun.class
        , EnemyBulletBill.class
        , EnemyCaptainLou.class
        , EnemyGoombaCyborg.class
        , EnemyHammerBro.class
        , EnemyKoopa.class
        , EnemyLakitu.class
        , EnemyMario.class
        , EnemyMisty.class
    };
    public static final List<Class<? extends Component>> ENEMY_COMPONENT_TYPES = List.of(
          EnemyAngrySun.class
        , EnemyBulletBill.class
        , EnemyCaptainLou.class
        , EnemyGoombaCyborg.class
        , EnemyHammerBro.class
        , EnemyKoopa.class
        , EnemyLakitu.class
        , EnemyMario.class
        , EnemyMisty.class
    );

    // TODO: very 'fuck it, ludum dare' vibe here...
    //  setup a tidier way to get AnimType by 'state' for a given Enemy
    //  maybe a util method here that switches on instanceof enemy?

    public static final Map<Class<? extends Enemy>, AnimType> ENEMY_ANIM_TYPE_IDLE = Map.of(
          EnemyAngrySun.class,     AnimType.ANGRY_SUN
        , EnemyBulletBill.class,   AnimType.BULLET_BILL_IDLE
        , EnemyCaptainLou.class,   AnimType.CAPTAIN_LOU_IDLE
        , EnemyGoombaCyborg.class, AnimType.GOOMBA_CYBORG_IDLE
        , EnemyHammerBro.class,    AnimType.HAMMER_BRO_IDLE
        , EnemyKoopa.class,        AnimType.KOOPA_WALK
        , EnemyLakitu.class,       AnimType.LAKITU_IDLE
        , EnemyMario.class,        AnimType.MARIO_IDLE
        , EnemyMisty.class,        AnimType.MISTY_IDLE
    );

    // TODO: missing several walk animations
    public static final Map<Class<? extends Enemy>, AnimType> ENEMY_ANIM_TYPE_WALK = Map.of(
          EnemyAngrySun.class,     AnimType.ANGRY_SUN
        , EnemyBulletBill.class,   AnimType.BULLET_BILL_WALK
        , EnemyCaptainLou.class,   AnimType.CAPTAIN_LOU_IDLE
        , EnemyGoombaCyborg.class, AnimType.GOOMBA_CYBORG_IDLE
        , EnemyHammerBro.class,    AnimType.HAMMER_BRO_IDLE
        , EnemyKoopa.class,        AnimType.KOOPA_WALK
        , EnemyLakitu.class,       AnimType.LAKITU_WALK
        , EnemyMario.class,        AnimType.MARIO_WALK
        , EnemyMisty.class,        AnimType.MISTY_IDLE
    );

    @SuppressWarnings("unchecked")
    public static <E extends Enemy> E getEnemyComponent(Entity entity) {
        for (var enemyType : ENEMY_COMPONENT_TYPES) {
            var enemy = Components.get(entity, enemyType);
            if (enemy != null) {
                return (E) enemy;
            }
        }
        return null;
    }
}
