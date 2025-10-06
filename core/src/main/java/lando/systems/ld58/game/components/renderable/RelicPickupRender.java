package lando.systems.ld58.game.components.renderable;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld58.assets.AnimType;
import lando.systems.ld58.game.components.Pickup;

public class RelicPickupRender implements Component {
    public static float DURATION = 10f;

    Interpolation interpolation = Interpolation.sineIn;

    public float accum;
    public Pickup.Type type;

    public RelicPickupRender(Pickup.Type type) {
        accum = 0;
        this.type = type;
    }

    public TextureRegion getRelicTexture() {
        switch (type) {
            case COIN:
                return AnimType.COIN.get().getKeyFrame(accum);
            case RELIC_PLUNGER:
                return AnimType.RELIC_PLUNGER.get().getKeyFrame(accum);
            case RELIC_TORCH:
                return AnimType.RELIC_TORCH.get().getKeyFrame(accum);
            case RELIC_WRENCH:
                return AnimType.RELIC_WRENCH.get().getKeyFrame(accum);
        }
        return null;
    }

    public float getRotation() {
        return interpolation.apply(0, 6 * MathUtils.PI2, accum/DURATION);
    }

    public boolean isComplete() {
        return accum >= DURATION;
    }
}
