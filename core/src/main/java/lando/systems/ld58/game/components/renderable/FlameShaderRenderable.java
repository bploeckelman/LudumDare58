package lando.systems.ld58.game.components.renderable;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;
import lando.systems.ld58.assets.ImageType;
import lando.systems.ld58.assets.ShaderType;
import lando.systems.ld58.game.components.Position;

public class FlameShaderRenderable extends ShaderRenderable implements Component {

    public final Color color1 = new Color(.9f, 0, 0, 1f);
    public final Color color2 = new Color(.9f, .9f, 0, 1f);

    public FlameShaderRenderable(Renderable renderable) {
        this.shaderProgram = ShaderType.FLAME.get();
        this.texture = ImageType.NOISE.get();
        this.bounds.set(renderable.rect(Position.ZERO));
        this.bounds.height *= 2f;
    }
}
