package lando.systems.ld58.game.components.renderable;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import lando.systems.ld58.Main;
import lando.systems.ld58.assets.ImageType;
import lando.systems.ld58.game.components.Position;

public class FlameShaderRenderable extends ShaderRenderable implements Component {

    public Color color1 = new Color(.9f, 0, 0, 1f);
    public Color color2 = new Color(.9f, .9f, 0, 1f);

    public FlameShaderRenderable(Renderable renderable) {
        shaderProgram = Main.game.assets.flameShader;
        texture = ImageType.NOISE.get();
        this.bounds.set(renderable.rect(Position.ZERO));
        this.bounds.height *= 2f;
    }

}
