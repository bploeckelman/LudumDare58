package lando.systems.ld58.utils.loaders;

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
import lando.systems.ld58.assets.FontType;
import lombok.AllArgsConstructor;

import static com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

public class FontAssetLoader extends AsynchronousAssetLoader<Font, FontAssetLoader.Param> {

    private final Array<Disposable> disposables;

    public FontAssetLoader(FileHandleResolver resolver, Array<Disposable> disposables) {
        super(resolver);
        this.disposables = disposables;
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, Param param) {
        // no dependencies for ttf/otf fonts... would need an atlas file if also loading .fnt files
        return null;
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, Param param) {
        // nothing to do, font gen happens on GL thread in loadSync() and is likely fast enough to run synchronously
    }

    @Override
    public Font loadSync(AssetManager manager, String fileName, FileHandle file, Param param) {
        // Bypass the normal FileHandle and resolve based on the actual font file path instead
        // so that it doesn't try to use the FontType2.uniqueKey() value as the file path.
        var actualFile = resolve(param.fontFilePath);
        var generator = new FreeTypeFontGenerator(actualFile);

        var bmpFont = generator.generateFont(param.ttfParams);
        disposables.add(bmpFont);

        var font = new Font(bmpFont);
        generator.dispose();

        return font;
    }

    @AllArgsConstructor
    public static class Param extends AssetLoaderParameters<Font> {
        public final String fontFilePath;
        public final FreeTypeFontParameter ttfParams;
        public Param(String fontFilePath) {
            this(fontFilePath, new FreeTypeFontParameter() {{ size = FontType.DEFAULT_SIZE; }});
        }
    }
}
