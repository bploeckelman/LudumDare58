package lando.systems.ld58.utils;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.github.tommyettinger.textra.Font;
import lombok.AllArgsConstructor;

public class FontAssetLoader extends AsynchronousAssetLoader<Font, FontAssetLoader.Param> {

    private final Array<Disposable> disposables;

    public FontAssetLoader(FileHandleResolver resolver, Array<Disposable> disposables) {
        super(resolver);
        this.disposables = disposables;
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, Param param) {
        // TODO: anything needed here? maybe see FreetypeFontLoader for exsample
        return null;
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, Param param) {
        // TODO: probably nothing to do here, fonts load pretty fast I think...
    }

    @Override
    public Font loadSync(AssetManager manager, String fileName, FileHandle file, Param param) {

        var generator = new FreeTypeFontGenerator(file);
        var genParam = new FreeTypeFontGenerator.FreeTypeFontParameter() {{ size = param.size; }};
        var bmpFont = generator.generateFont(genParam);
        disposables.add(bmpFont);

        var font = new Font(bmpFont);
        generator.dispose();
        return font;
    }

    // TODO: might be useful to expose more of the FreeTypeFontParameter fields here
    //  or just use FreeTypeFontParameter instead of having a custom one here,
    //  though this lets us specify the SDF gen params and such if we want
    @AllArgsConstructor
    public static class Param extends AssetLoaderParameters<Font> {

        public static final int DEFAULT_SIZE = 24;

        public final int size;

        public Param() {
            this(DEFAULT_SIZE);
        }
    }
}
