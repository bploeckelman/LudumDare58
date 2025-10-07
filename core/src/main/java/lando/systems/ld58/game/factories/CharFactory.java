package lando.systems.ld58.game.factories;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld58.assets.AnimType;
import lando.systems.ld58.game.Constants;
import lando.systems.ld58.game.Factory;
import lando.systems.ld58.game.components.*;
import lando.systems.ld58.game.components.collision.CollisionMask;
import lando.systems.ld58.game.components.enemies.*;
import lando.systems.ld58.game.components.renderable.Animator;
import lando.systems.ld58.game.components.renderable.KirbyShaderRenderable;
import lando.systems.ld58.game.components.renderable.Outline;

import static lando.systems.ld58.game.Factory.createEntity;

public class CharFactory {

    // ------------------------------------------------------------------------
    // Player character
    // ------------------------------------------------------------------------

    public static Entity player(TilemapObject.Spawner spawner) {
        var entity = Factory.createEntity();

        entity.add(new Player());
        entity.add(new Name("PLAYER"));
        entity.add(new Input());
        entity.add(new MySpawner(spawner));
        entity.add(new Position(spawner));
        entity.add(new Velocity());
        entity.add(new Friction());
        entity.add(new Gravity());
        entity.add(new Outline(Color.BLACK, Color.CLEAR_WHITE, 1f));
        entity.add(new KirbyShaderRenderable());
//        entity.add(new FlameShaderRenderable(animator));
        entity.add(new Cooldowns()
            .add("jump", 0.2f)
            .add("taunt", 0.2f));

        var animBounds = Constants.BILLY_ANIMATOR_BOUNDS;
        var animOrigin = animBounds.getPosition(new Vector2());
        var animator = new Animator(AnimType.BILLY_IDLE, animOrigin);
        animator.size.set(animBounds.width, animBounds.height);
        animator.depth = 1;
        entity.add(animator);

        var collidesWith   = new CollisionMask[] {
            CollisionMask.SOLID,
            CollisionMask.ENEMY,
            CollisionMask.DESTRUCTIBLE,
            CollisionMask.KILLER
        };
        entity.add(Collider.rect(CollisionMask.PLAYER, Constants.BILLY_COLLIDER_BOUNDS, collidesWith));

        return entity;
    }

    // ------------------------------------------------------------------------
    // Enemy characters
    // ------------------------------------------------------------------------

    public static Entity enemy(TilemapObjectType.Enemies type, TilemapObject.Spawner spawner) {
        var entity = createEntity();

        var tag = new EnemyTag();
        var name = new Name(spawner.type);
        var spawn = new MySpawner(spawner);
        var position = new Position(spawner);
        var velocity = new Velocity();
        var friction = new Friction();
        var gravity = new Gravity();
        var outline = new Outline(Color.BLACK, Color.CLEAR_WHITE, 1f);

        entity.add(tag);
        entity.add(name);
        entity.add(spawn);
        entity.add(position);
        entity.add(velocity);
        entity.add(friction);
        entity.add(gravity);
        entity.add(outline);

        // Customizations by type
        // TODO: review and update anim, collider bounds
        switch (type) {
            case ANGRY_SUN: {
                entity.add(new EnemyAngrySun());
                entity.add(new KirbyPower(KirbyPower.PowerType.SUN));

                var animOrigin = new Vector2(16, 12);
                var anim = new Animator(AnimType.ANGRY_SUN, animOrigin);
                anim.depth = 1000;
                entity.add(anim);

                var radius = 8f;
                var collidesWith  = new CollisionMask[] { CollisionMask.PLAYER };
                entity.add(Collider.circ(CollisionMask.ENEMY, 0, 0, radius, collidesWith));
            } break;

            case BULLET_BILL: {
                entity.add(new EnemyBulletBill());
                entity.add(new KirbyPower(KirbyPower.PowerType.BULLET));

                var animBounds = Constants.MISTY_ANIMATOR_BOUNDS;
                var anim = new Animator(AnimType.BULLET_BILL_IDLE);
                anim.origin.set(animBounds.x, animBounds.y);
                anim.size.set(animBounds.width, animBounds.height);
                entity.add(anim);

                var colliderBounds = Constants.MISTY_COLLIDER_BOUNDS;
                var collidesWith  = new CollisionMask[] { CollisionMask.SOLID, CollisionMask.PLAYER };
                entity.add(Collider.rect(CollisionMask.ENEMY, colliderBounds, collidesWith));
            } break;

            case CAPTAIN_LOU: {
                entity.add(new EnemyCaptainLou());
                // TODO: power?
                outline.outlineColor(Color.RED);

                var animBounds = Constants.CAPTAIN_LOU_ANIMATOR_BOUNDS;
                var anim = new Animator(AnimType.CAPTAIN_LOU_IDLE);
                anim.origin.set(animBounds.x, animBounds.y);
                anim.size.set(animBounds.width, animBounds.height);
                entity.add(anim);

                var colliderBounds = Constants.CAPTAIN_LOU_COLLIDER_BOUNDS;
                var collidesWith  = new CollisionMask[] { CollisionMask.SOLID, CollisionMask.PLAYER };
                entity.add(Collider.rect(CollisionMask.ENEMY, colliderBounds, collidesWith));
            } break;

            case GOOMBA_CYBORG: {
                entity.add(new EnemyGoombaCyborg());
                // TODO: power?

                var animBounds = Constants.GOOMBA_CYBORG_ANIMATOR_BOUNDS;
                var anim = new Animator(AnimType.GOOMBA_CYBORG_IDLE);
                anim.origin.set(animBounds.x, animBounds.y);
                anim.size.set(animBounds.width, animBounds.height);
                entity.add(anim);

                var colliderBounds = Constants.GOOMBA_CYBORG_COLLIDER_BOUNDS;
                var collidesWith  = new CollisionMask[] { CollisionMask.SOLID, CollisionMask.PLAYER };
                entity.add(Collider.rect(CollisionMask.ENEMY, colliderBounds, collidesWith));
            } break;

            case HAMMER_BRO: {
                entity.add(new EnemyHammerBro());
                entity.add(new KirbyPower(KirbyPower.PowerType.HAMMER));

                var animBounds = Constants.MISTY_ANIMATOR_BOUNDS;
                var anim = new Animator(AnimType.HAMMER_BRO_IDLE);
                anim.origin.set(animBounds.x, animBounds.y);
                anim.size.set(animBounds.width, animBounds.height);
                entity.add(anim);

                var colliderBounds = Constants.MISTY_COLLIDER_BOUNDS;
                var collidesWith  = new CollisionMask[] { CollisionMask.SOLID, CollisionMask.PLAYER };
                entity.add(Collider.rect(CollisionMask.ENEMY, colliderBounds, collidesWith));
            } break;

            case KOOPA: {
                entity.add(new EnemyKoopa());
                entity.add(new KirbyPower(KirbyPower.PowerType.KOOPA));

                var animBounds = Constants.MISTY_ANIMATOR_BOUNDS;
                var anim = new Animator(AnimType.KOOPA_WALK);
                anim.origin.set(animBounds.x, animBounds.y);
                anim.size.set(animBounds.width, animBounds.height);
                entity.add(anim);

                var colliderBounds = Constants.MISTY_COLLIDER_BOUNDS;
                var collidesWith  = new CollisionMask[] { CollisionMask.SOLID, CollisionMask.PLAYER };
                entity.add(Collider.rect(CollisionMask.ENEMY, colliderBounds, collidesWith));
            } break;

            case LAKITU: {
                entity.add(new EnemyLakitu());
                entity.add(new KirbyPower(KirbyPower.PowerType.LAKITU));

                var animBounds = Constants.MISTY_ANIMATOR_BOUNDS;
                var anim = new Animator(AnimType.LAKITU_IDLE);
                anim.origin.set(animBounds.x, animBounds.y);
                anim.size.set(animBounds.width, animBounds.height);
                entity.add(anim);

                var colliderBounds = Constants.MISTY_COLLIDER_BOUNDS;
                var collidesWith  = new CollisionMask[] { CollisionMask.SOLID, CollisionMask.PLAYER };
                entity.add(Collider.rect(CollisionMask.ENEMY, colliderBounds, collidesWith));
            } break;

            case MARIO: {
                entity.add(new EnemyMario());
                // TODO: power?

                var animBounds = Constants.MARIO_ANIMATOR_BOUNDS;
                var animOrigin = animBounds.getPosition(new Vector2());
                entity.add(new Animator(AnimType.MARIO_IDLE, animOrigin));

                var collidesWith  = new CollisionMask[] { CollisionMask.SOLID, CollisionMask.PLAYER };
                entity.add(Collider.rect(CollisionMask.ENEMY, Constants.MARIO_COLLIDER_BOUNDS, collidesWith));
            } break;

            case MISTY: {
                entity.add(new EnemyMisty());
                // TODO: power?

                var animBounds = Constants.MISTY_ANIMATOR_BOUNDS;
                var anim = new Animator(AnimType.MISTY_IDLE);
                anim.origin.set(animBounds.x, animBounds.y);
                anim.size.set(animBounds.width, animBounds.height);
                entity.add(anim);

                var colliderBounds = Constants.MISTY_COLLIDER_BOUNDS;
                var collidesWith  = new CollisionMask[] { CollisionMask.SOLID, CollisionMask.PLAYER };
                entity.add(Collider.rect(CollisionMask.ENEMY, colliderBounds, collidesWith));
            } break;
        }

        return entity;
    }
}
