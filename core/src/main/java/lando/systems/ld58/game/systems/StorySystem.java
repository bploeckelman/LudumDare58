package lando.systems.ld58.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.github.tommyettinger.textra.TypingLabel;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisTable;
import lando.systems.ld58.Main;
import lando.systems.ld58.game.Components;
import lando.systems.ld58.game.components.Story;

public class StorySystem extends IteratingSystem {

    public final VisTable uiRoot;
    public final Stack speakerStack;
    public final VisImage speakerBorder;
    public final VisImage speaker;
    public final TypingLabel label;

    private final TextureRegionDrawable speakerAnimFrame;

    private float speakerAnimTime;

    public StorySystem() {
        super(Family.one(Story.class).get());
        this.uiRoot = new VisTable();
        this.speakerStack = new Stack();
        this.speakerBorder = new VisImage();
        this.speaker = new VisImage();
        this.label = new TypingLabel();
        this.speakerAnimFrame = new TextureRegionDrawable();
        this.speakerAnimTime = 0f;

        // TODO: add a background frame for speaker
        var speakerBg = new NinePatchDrawable(Main.game.assets.dimNine);
        speakerBorder.setDrawable(speakerBg);

        // TODO: tidy up, allow to change for different Dialogs?
        var rootBg = new NinePatchDrawable(Main.game.assets.plainNine);
        uiRoot.setBackground(rootBg);
    }

    public void setup(Rectangle dialogBounds) {
        uiRoot.clear();
        uiRoot.setBounds(
            dialogBounds.getX(), dialogBounds.getY(),
            dialogBounds.getWidth(), dialogBounds.getHeight());

        speaker.setAlign(Align.center);
        speakerStack.add(speakerBorder);
        speakerStack.add(speaker);

        var speakerWidth = dialogBounds.getWidth() / 3f;
        var speakerHeight = dialogBounds.getHeight();
        uiRoot.add(speakerStack).size(speakerWidth, speakerHeight);
        uiRoot.add(label).size(uiRoot.getWidth() - speakerWidth, uiRoot.getHeight()).pad(10f);
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        var story = Components.get(entity, Story.class);
        if (story == null) return;

        // TODO: to just keep it animating on the last dialog element until cleared
        if (story.isComplete()) return;

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
