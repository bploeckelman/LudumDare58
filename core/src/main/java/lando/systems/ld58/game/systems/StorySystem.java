package lando.systems.ld58.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.github.tommyettinger.digital.Stringf;
import com.github.tommyettinger.textra.TypingLabel;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisTable;
import lando.systems.ld58.Flag;
import lando.systems.ld58.Main;
import lando.systems.ld58.game.Components;
import lando.systems.ld58.game.Signals;
import lando.systems.ld58.game.components.Story;
import lando.systems.ld58.game.signals.StoryEvent;
import lando.systems.ld58.utils.FramePool;
import lando.systems.ld58.utils.Util;

public class StorySystem extends IteratingSystem implements InputProcessor, Listener<StoryEvent> {

    private static final float DEBOUNCE_DURATION = 0.3f;

    public final VisTable uiRoot;
    public final Stack speakerStack;
    public final VisImage speakerBorder;
    public final VisImage speaker;
    public final TypingLabel label;

    private final Vector2 pointer = new Vector2();
    private final Vector2 pointerScreen = new Vector2();
    private final TextureRegionDrawable speakerAnimFrame;

    private float speakerAnimTime;
    private float debounceTimer;
    private Entity activeStoryEntity;

    public StorySystem() {
        super(Family.one(Story.class).get());
        this.uiRoot = new VisTable();
        this.speakerStack = new Stack();
        this.speakerBorder = new VisImage();
        this.speaker = new VisImage();
        this.label = new TypingLabel();
        this.speakerAnimFrame = new TextureRegionDrawable();
        this.speakerAnimTime = 0f;
        this.debounceTimer = DEBOUNCE_DURATION;
        this.activeStoryEntity = null;

        // TODO: add a background frame for speaker
        var speakerBg = new NinePatchDrawable(Main.game.assets.dimNine);
        speakerBorder.setDrawable(speakerBg);

        // TODO: tidy up, allow to change for different Dialogs?
        var rootBg = new NinePatchDrawable(Main.game.assets.plainNine);
        uiRoot.setBackground(rootBg);

        Signals.advanceStory.add(this);
    }

    public void setup(Rectangle dialogBounds) {
        uiRoot.clear();
        uiRoot.setBounds(
            dialogBounds.getX(), dialogBounds.getY(),
            dialogBounds.getWidth(), dialogBounds.getHeight());
        uiRoot.setVisible(false);

        speaker.setAlign(Align.center);
        speakerStack.add(speakerBorder);
        speakerStack.add(speaker);

        label.setWrap(true);

        var speakerWidth = dialogBounds.getWidth() / 3f;
        var speakerHeight = dialogBounds.getHeight();
        uiRoot.add(speakerStack).size(speakerWidth, speakerHeight);
        uiRoot.add(label).size(uiRoot.getWidth() - speakerWidth, uiRoot.getHeight()).pad(10f);
    }

    @Override
    public void update(float delta) {
        debounceTimer = Math.max(0, debounceTimer - delta);
        super.update(delta);
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        var story = Components.get(entity, Story.class);
        if (story == null) return;

        // Remove entity if story should be cleared
        if (story.shouldClear()) {
            getEngine().removeEntity(entity);
            if (activeStoryEntity == entity) {
                activeStoryEntity = null;
                // Clear the UI
                uiRoot.setVisible(false);
            }
            return;
        }

        // Track the active story entity
        if (story.isStarted() && !story.isComplete()) {
            activeStoryEntity = entity;
            uiRoot.setVisible(true);
        }

        // Keep animating even when complete (until cleared)
        update(story);

        if (!story.isStarted()) {
            story.start();
            restartLabel(story);
        }

        if (label.hasEnded() && story.isTyping()) {
            story.finishTyping();
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

    @Override
    public void receive(Signal<StoryEvent> signal, StoryEvent event) {
        if (event instanceof StoryEvent.Advance) {
            if (activeStoryEntity == null) return;
            if (debounceTimer > 0) return;

            var story = Components.get(activeStoryEntity, Story.class);
            if (story == null || !story.isStarted()) return;

            // NEW: If story is complete, clear it
            if (story.isComplete()) {
                story.clear();
                debounceTimer = DEBOUNCE_DURATION;
                return;
            }

            // If still typing, skip to end
            if (story.isTyping()) {
                label.skipToTheEnd();
                story.finishTyping();
            }
            // If typing complete, advance to next dialog
            else {
                story.next();
                restartLabel(story);
            }

            debounceTimer = DEBOUNCE_DURATION;
        }
    }

    public boolean isStoryActive() {
        return activeStoryEntity != null;
    }

    public boolean shouldPauseGame() {
        if (activeStoryEntity == null) return false;
        var story = Components.get(activeStoryEntity, Story.class);
        return story != null && story.pauseGame;
    }

    // InputProcessor implementation ------------------------------------------

    @Override
    public boolean keyDown(int keycode) {
        log(Stringf.format("keyDown: %d", keycode));
        if (isStoryActive()) {
            Signals.advanceStory.dispatch(StoryEvent.advance());
            return true; // consume input
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        log(Stringf.format("keyUp: %d", keycode));
        return isStoryActive(); // consume but don't process
    }

    @Override
    public boolean keyTyped(char character) {
        log(Stringf.format("keyTyped: ch=%c", character));
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointerIndex, int button) {
        log(Stringf.format("touchDown: b%d p%d=(%d, %d)", button, pointerIndex, screenX, screenY));
        updatePointer(screenX, screenY);
        if (isStoryActive()) {
            Signals.advanceStory.dispatch(StoryEvent.advance());
            return true;
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointerIndex, int button) {
        log(Stringf.format("touchUp: b%d p%d=(%d, %d)", button, pointerIndex, screenX, screenY));
        updatePointer(screenX, screenY);
        return isStoryActive();
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointerIndex, int button) {
        log(Stringf.format("touchCancelled: b%d p%d=(%d, %d)", button, pointerIndex, screenX, screenY));
        updatePointer(screenX, screenY);
        return isStoryActive();
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointerIndex) {
        log(Stringf.format("touchDragged: p%d=(%d, %d)", pointerIndex, screenX, screenY));
        updatePointer(screenX, screenY);
        return isStoryActive();
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        // NOTE: log is too busy with this active
        //log("mouseMoved: (%d, %d)".formatted(screenX, screenY));
        updatePointer(screenX, screenY);
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        log(Stringf.format("scrolled: (%.1f, %.1f)", amountX, amountY));
        return isStoryActive();
    }

    protected void log(String event) {
        if (Flag.LOG_INPUT.isDisabled()) return;

        var clazz = getClass().getSimpleName();
        Util.log(clazz, "event: " + event);
    }

    protected void updatePointer(int screenX, int screenY) {
        var scene = Util.findScene(getEngine());
        if (scene == null) return;

        pointerScreen.set(screenX, screenY);

        var screenToWorld = FramePool.vec3(pointerScreen);
        var camera = scene.screen.worldCamera;
        camera.unproject(screenToWorld);

        pointer.set(screenToWorld.x, screenToWorld.y);
    }
}
