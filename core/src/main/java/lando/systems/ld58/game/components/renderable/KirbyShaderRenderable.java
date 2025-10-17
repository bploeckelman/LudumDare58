package lando.systems.ld58.game.components.renderable;

import com.badlogic.ashley.core.Component;
import lando.systems.ld58.assets.ImageType;
import lando.systems.ld58.assets.ShaderType;

public class KirbyShaderRenderable extends ShaderRenderable implements Component {

    public static final float radius = 64f;

    public float rampUpTime = 2.5f;
    public float rampDownTime = 20f;

    public float strength;
    public float targetStrength;

    public KirbyShaderRenderable() {
        this.shaderProgram = ShaderType.KIRBY.get();
        this.texture = ImageType.NOISE.get();
        this.bounds.set(-radius, -radius + 4, radius*2f, radius*2f);
        this.strength = 0f;
        this.targetStrength = 0f;
    }
}
