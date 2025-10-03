package lando.systems.ld58.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.OrthographicCamera;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Viewer implements Component {

    public final OrthographicCamera camera;

    public OrthographicCamera camera() { return camera; }

    public float width()  { return camera.viewportWidth  * camera.zoom; }
    public float height() { return camera.viewportHeight * camera.zoom; }

    public float left()   { return camera.position.x - width()  / 2f; }
    public float right()  { return camera.position.x + width()  / 2f; }
    public float bottom() { return camera.position.y - height() / 2f; }
    public float top()    { return camera.position.y + height() / 2f; }
}
