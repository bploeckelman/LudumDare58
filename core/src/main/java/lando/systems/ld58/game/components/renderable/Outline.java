package lando.systems.ld58.game.components.renderable;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;

public class Outline implements Component {
    private Color outlineColor;
    private Color fillColor;
    private float outlineThickness;

    public static final Outline CLEAR = new Outline();


    public Outline(){
        this(Color.CLEAR, Color.CLEAR, .5f);
    }

    public Outline(Color outlineColor, Color fillColor, float outlineThickness) {
        this.outlineColor = new Color(outlineColor);
        this.fillColor = new Color(fillColor);
        this.outlineThickness = outlineThickness;
    }

    public Color outlineColor() {
        return outlineColor;
    }

    public float outlineThickness() {
        return outlineThickness;
    }

    public Color fillColor() {
        return fillColor;
    }

    public void fillColor(Color fillColor) {
        this.fillColor = fillColor;
    }

    public void outlineColor(Color outlineColor) {
        this.outlineColor = outlineColor;
    }

    public void OutlineThickness(float outlineThickness) {
        this.outlineThickness = outlineThickness;
    }
}
