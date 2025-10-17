package lando.systems.ld58.assets;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import java.util.EnumMap;
import java.util.Objects;

import static com.badlogic.gdx.assets.loaders.ShaderProgramLoader.ShaderProgramParameter;

public enum ShaderType implements AssetType<ShaderProgram> {
    //@formatter:off
      FLAME     ("shaders/flame.frag")
    , FLASHBACK ("shaders/flashback.frag")
    , HIPPIE    ("shaders/hippie.frag")
    , KIRBY     ("shaders/kirby.frag")
    , OUTLINE   ("shaders/outline.frag")
    , PROGRESS  ("shaders/progress.frag")
    , RELIC     ("shaders/relic.frag")
    ;
    //@formatter:on

    private static final EnumMap<ShaderType, ShaderProgram> container = AssetType.createContainer(ShaderType.class);

    public final ShaderProgramParameter params;

    /**
     * Convenience constructor that uses the default vertex shader file: {@code "shaders/default.vert"}
     */
    ShaderType(String fragmentShaderPath) {
        this("shaders/default.vert", fragmentShaderPath);
    }

    ShaderType(String vertexShaderPath, String fragmentShaderPath) {
        this(new ShaderProgramParameter() {{
            vertexFile = vertexShaderPath;
            fragmentFile = fragmentShaderPath;
        }});
    }

    /**
     * @param params customizes how the {@link ShaderProgram} is loaded, mainly useful for prepending code to all vertex
     *               or fragment shaders via {@code prepend[Vertex|Fragment]Code} fields, <strong>requires</strong> that
     *               {@code vertexFile} and {@code fragmentFile} fields are set!
     */
    ShaderType(ShaderProgramParameter params) {
        Objects.requireNonNull(params.vertexFile, name() + " params requires vertexFile value");
        Objects.requireNonNull(params.fragmentFile, name() + " params requires fragmentFile value");
        this.params = params;
    }

    @Override
    public ShaderProgram get() {
        return container.get(this);
    }

    // NOTE:
    //  Loading/getting from asset manager is a little goofy because 'load|get()'
    //  both expect a single file name string and the default behavior of the loader
    //  is to specify a file path without extension, which assumes both vert and frag
    //  files share the same prefix so it can manually append '.vert|.frag' extensions
    //  at load time.
    //  Here we always populate both file name fields in the params object,
    //  so the string provided to 'load|get()' is likely just ignored internally.
    //  To provide something reasonable we'll just take the fragment path and strip the extension
    //  since most shader programs use the default vertex shader.

    private String fragmentPathNoExt() {
        var index = params.fragmentFile.indexOf(".frag");
        return params.fragmentFile.substring(0, index);
    }

    public static void load(Assets assets) {
        var mgr = assets.mgr;
        for (var type : values()) {
            mgr.load(type.fragmentPathNoExt(), ShaderProgram.class, type.params);
        }
    }

    public static void init(Assets assets) {
        var mgr = assets.mgr;
        for (var type : values()) {
            var shader = mgr.get(type.fragmentPathNoExt(), ShaderProgram.class);
            container.put(type, shader);
        }
    }
}
