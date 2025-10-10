package lando.systems.ld58.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.github.tommyettinger.textra.TypingLabel;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisTable;
import lando.systems.ld58.Main;
import lando.systems.ld58.game.Components;
import lando.systems.ld58.game.components.Story;

public class StorySystem extends IteratingSystem {

    public final VisTable uiRoot;
    public final VisImage speaker;
    public final TypingLabel label;

    private final TextureRegionDrawable speakerAnimFrame;

    private float speakerAnimTime;

    public StorySystem() {
        super(Family.one(Story.class).get());
        this.uiRoot = new VisTable();
        this.speaker = new VisImage();
        this.label = new TypingLabel();
        this.speakerAnimFrame = new TextureRegionDrawable();
        this.speakerAnimTime = 0f;

        // TODO: tidy up, allow to change for different Dialogs?
        var background = new NinePatchDrawable(Main.game.assets.dimNine);
        uiRoot.setBackground(background);
    }

    public void setup(Rectangle dialogBounds) {
        uiRoot.clear();
        uiRoot.setBounds(
            dialogBounds.getX(), dialogBounds.getY(),
            dialogBounds.getWidth(), dialogBounds.getHeight());

        var speakerWidth = dialogBounds.getWidth() / 3f;
        var speakerHeight = dialogBounds.getHeight();
        uiRoot.add(speaker).size(speakerWidth, speakerHeight);
        uiRoot.add(label).grow();
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        var story = Components.get(entity, Story.class);
        if (story == null || story.isComplete()) return;

        update(story);

        if (!story.isStarted()) {
            story.start();
            restartLabel(story);
        }

        if (label.hasEnded()) {
            story.next();
            restartLabel(story);
        }

        speakerAnimTime += delta;
    }

    private void restartLabel(Story story) {
        speakerAnimTime = 0f;

        var dialog = story.currentDialog();
        if (dialog != null) {
            label.setFont(dialog.fontType.get(), true);
            label.restart(dialog.text);
        }
    }

    private void update(Story story) {
        if (story.isComplete()) return;

        var dialog = story.currentDialog();
        if (dialog == null) return;

        var anim = dialog.animType.get();
        var frame = anim.getKeyFrame(speakerAnimTime);
        speakerAnimFrame.setRegion(frame);
        speaker.setDrawable(speakerAnimFrame);
    }
}
