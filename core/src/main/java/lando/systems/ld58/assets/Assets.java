package lando.systems.ld58.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.I18NBundle;
import lando.systems.ld58.Config;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class Assets implements Disposable {

    public enum Load {SYNC, ASYNC}

    public boolean loaded = false;

    public final Preferences prefs;
    public final AssetManager mgr;
    public final SpriteBatch batch;
    public final ShapeDrawer shapes;
    public final GlyphLayout glyphLayout;
    public final Array<Disposable> disposables;
    public final AssetTypeRegistry assetTypeRegistry;

    public TextureAtlas atlas;
    public I18NBundle strings;

    public final Texture pixel;
    public TextureRegion pixelRegion;

    public NinePatch plainNine;
    public NinePatch dimNine;

    public Assets() {
        this(Load.SYNC);
    }

    public Assets(Load load) {
        prefs = Gdx.app.getPreferences(Config.preferences_name);

        disposables = new Array<>();
        assetTypeRegistry = new AssetTypeRegistry();

        // create a single pixel texture and associated region
        var pixmap = new Pixmap(2, 2, Pixmap.Format.RGBA8888);
        {
            pixmap.setColor(Color.WHITE);
            pixmap.drawPixel(0, 0);
            pixmap.drawPixel(1, 0);
            pixmap.drawPixel(0, 1);
            pixmap.drawPixel(1, 1);

            pixel = new Texture(pixmap);
            pixelRegion = new TextureRegion(pixel);
        }
        disposables.add(pixmap);
        disposables.add(pixel);

        mgr = new AssetManager();
        batch = new SpriteBatch();
        shapes = new ShapeDrawer(batch, pixelRegion);
        glyphLayout = new GlyphLayout();
        disposables.add(mgr);
        disposables.add(batch);

        // load one-off items
        mgr.load("sprites/sprites.atlas", TextureAtlas.class);
        mgr.load("i18n/strings", I18NBundle.class);

        // load asset type items
        assetTypeRegistry.loadAll(this);

        if (load == Load.SYNC) {
            mgr.finishLoading();
            updateLoading();
        }
    }

    public float updateLoading() {
        if (loaded) return 1;
        if (!mgr.update()) {
            return mgr.getProgress();
        }

        atlas = mgr.get("sprites/sprites.atlas");
        strings = mgr.get("i18n/strings");

        assetTypeRegistry.initAll(this);

        plainNine = new NinePatch(atlas.findRegion("patch/plain"), 5, 5, 5, 5);
        dimNine = new NinePatch(atlas.findRegion("patch/plain-dim"), 5, 5, 5, 5);

        loaded = true;
        return 1;
    }

    @Override
    public void dispose() {
        disposables.forEach(Disposable::dispose);
        mgr.dispose();
    }
}
