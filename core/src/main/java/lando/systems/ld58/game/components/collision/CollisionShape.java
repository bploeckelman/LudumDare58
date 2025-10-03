package lando.systems.ld58.game.components.collision;

import com.badlogic.gdx.math.Shape2D;
import com.github.tommyettinger.gdcrux.PointI2;
import lando.systems.ld58.game.components.Position;

public interface CollisionShape {

    Shape2D shape2d();
    boolean overlaps(CollisionShape that, Position thisPos, Position thatPos, PointI2 offset);
}
