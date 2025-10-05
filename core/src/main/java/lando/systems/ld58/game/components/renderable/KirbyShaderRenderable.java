package lando.systems.ld58.game.components.renderable;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import lando.systems.ld58.Main;
import lando.systems.ld58.game.components.Collider;
import lando.systems.ld58.game.components.Position;
import lando.systems.ld58.utils.FramePool;

public class KirbyShaderRenderable extends ShaderRenderable implements Component {

    private static final float radius = 48f;
    private float strength;

    public KirbyShaderRenderable() {
        shaderProgram = Main.game.assets.kirbyShader;
        texture = Main.game.assets.pixel;

        bounds.set(-radius, -radius + 4, radius*2f, radius*2f);
        this.strength = 1f;
    }

    public float strength() {
        return strength;
    }

    public void strength(float strength) {
        this.strength = MathUtils.clamp(strength, 0f, 1f);
    }

}
