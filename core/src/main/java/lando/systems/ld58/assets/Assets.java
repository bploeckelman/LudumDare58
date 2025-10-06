package lando.systems.ld58.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.I18NBundle;
import lando.systems.ld58.Config;
import lando.systems.ld58.utils.Util;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.ArrayList;
import java.util.List;

public class Assets implements Disposable {

    public enum Load {SYNC, ASYNC}

    public boolean loaded = false;

    public final List<Class<? extends AssetType<?>>> assetClasses;
    public final Preferences prefs;
    public final AssetManager mgr;
    public final SpriteBatch batch;
    public final ShapeDrawer shapes;
    public final GlyphLayout glyphLayout;
    public final Array<Disposable> disposables;

    public TextureAtlas atlas;
    public I18NBundle strings;

    public final Texture pixel;

    public NinePatch plainNine;
    public NinePatch dimNine;

    public TextureRegion pixelRegion;
    public ShaderProgram outlineShader;
    public ShaderProgram kirbyShader;
    public ShaderProgram flameShader;
    public ShaderProgram progressShader;
    public ShaderProgram flashbackShader;
    public ShaderProgram relicShader;
    public ShaderProgram hippieShader;

    public Assets() {
        this(Load.SYNC);
    }

    public Assets(Load load) {
        prefs = Gdx.app.getPreferences(Config.preferences_name);

        disposables = new Array<>();
        assetClasses = new ArrayList<>();
        assetClasses.add(AnimType.class);
        assetClasses.add(ColorType.class);
        assetClasses.add(EffectType.class);
        assetClasses.add(EmitterType.class);
        assetClasses.add(FontType.class);
        assetClasses.add(IconType.class);
        assetClasses.add(ImageType.class);
        assetClasses.add(MusicType.class);
        assetClasses.add(SkinType.class);
        assetClasses.add(SoundType.class);

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

        // setup asset manager to support ttf/otf fonts
        var resolver = new InternalFileHandleResolver();
        var fontLoader = new FreetypeFontLoader(resolver);
        var fontGenLoader = new FreeTypeFontGeneratorLoader(resolver);
        mgr.setLoader(FreeTypeFontGenerator.class, fontGenLoader);
        mgr.setLoader(BitmapFont.class, ".ttf", fontLoader);
        mgr.setLoader(BitmapFont.class, ".otf", fontLoader);

        // populate asset manager
        {
            // one-off items
            mgr.load("sprites/sprites.atlas", TextureAtlas.class);
            mgr.load("i18n/strings", I18NBundle.class);

            // TODO: does FreeTypistSkin have a supported loader?
//            mgr.load("ui/uiskin.json", Skin.class);

            for (var assets : assetClasses) {
                AssetType.load(assets, this);
            }
        }

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

        outlineShader = Util.loadShader("shaders/default.vert", "shaders/outline.frag");
        kirbyShader = Util.loadShader("shaders/default.vert",  "shaders/kirby.frag");
        flameShader = Util.loadShader("shaders/default.vert", "shaders/flame.frag");
        progressShader =  Util.loadShader("shaders/default.vert", "shaders/progress.frag");
        flashbackShader = Util.loadShader("shaders/default.vert", "shaders/flashback.frag");
        relicShader = Util.loadShader("shaders/default.vert", "shaders/relic.frag");
        hippieShader = Util.loadShader("shaders/default.vert", "shaders/hippie.frag");

        atlas = mgr.get("sprites/sprites.atlas");
        strings = mgr.get("i18n/strings");

        for (var assets : assetClasses) {
            AssetType.init(assets, this);
        }

        plainNine = new NinePatch(atlas.findRegion("patch/plain"), 5, 5, 5, 5);
        dimNine = new NinePatch(atlas.findRegion("patch/plain-dim"), 5, 5, 5, 5);


        loaded = true;
        return 1;
    }

    @Override
    public void dispose() {
        disposables.forEach(Disposable::dispose);
    }
}
