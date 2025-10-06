package lando.systems.ld58.game.components.renderable;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import lando.systems.ld58.Main;
import lando.systems.ld58.assets.ImageType;
import lando.systems.ld58.game.components.Collider;
import lando.systems.ld58.game.components.Position;
import lando.systems.ld58.utils.FramePool;

public class KirbyShaderRenderable extends ShaderRenderable implements Component {

    private static final float radius = 64f;
    public float strength;
    public float targetStrength;
    public float rampUpTime = 2.5f;
    public float rampDownTime = 20f;

    public KirbyShaderRenderable() {
        shaderProgram = Main.game.assets.kirbyShader;
        texture = ImageType.NOISE.get();

        bounds.set(-radius, -radius + 4, radius*2f, radius*2f);
        this.strength = 0f;
        this.targetStrength = 0f;
    }

}
