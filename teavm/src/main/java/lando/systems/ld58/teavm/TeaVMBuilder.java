package lando.systems.ld58.teavm;

import com.github.xpenatan.gdx.backends.teavm.config.AssetFileHandle;
import com.github.xpenatan.gdx.backends.teavm.config.TeaBuildConfiguration;
import com.github.xpenatan.gdx.backends.teavm.config.TeaBuilder;
import com.github.xpenatan.gdx.backends.teavm.config.TeaTargetType;
import com.github.xpenatan.gdx.backends.teavm.config.plugins.TeaReflectionSupplier;
import java.io.File;
import java.io.IOException;
import org.teavm.tooling.TeaVMSourceFilePolicy;
import org.teavm.tooling.TeaVMTargetType;
import org.teavm.tooling.TeaVMTool;
import org.teavm.tooling.sources.DirectorySourceFileProvider;
import org.teavm.vm.TeaVMOptimizationLevel;

/** Builds the TeaVM/HTML application. */
public class TeaVMBuilder {
    /**
     * A single point to configure most debug vs. release settings.
     * This defaults to false in new projects; set this to false when you want to release.
     * If this is true, the output will not be obfuscated, and debug information will usually be produced.
     * You can still set obfuscation to false in a release if you want the source to be at least a little legible.
     * This works well when the targetType is set to JAVASCRIPT, but you can still set the targetType to WEBASSEMBLY_GC
     * while this is true in order to test that higher-performance target before releasing.
     */
    private static final boolean DEBUG = true;

    // see: https://github.com/xpenatan/gdx-teavm/blob/1.2.4/examples/core/teavm/src/main/java/com/github/xpenatan/gdx/examples/teavm/BuildTeaVMTestDemo.java
    public static void main(String[] args) throws IOException {
        // TODO: do we need to add all reflection aware packages here,
        //  similar to what we do for the gwt-based html backend?
        TeaReflectionSupplier.addReflectionClass("com.badlogic.gdx.math");
        TeaReflectionSupplier.addReflectionClass("com.badlogic.ashley.core.Entity");
        TeaReflectionSupplier.addReflectionClass("lando.systems.ld58.assets");
        TeaReflectionSupplier.addReflectionClass("lando.systems.ld58.game.components");
        TeaReflectionSupplier.addReflectionClass("lando.systems.ld58.game.components.collision");
        TeaReflectionSupplier.addReflectionClass("lando.systems.ld58.particles");
        TeaReflectionSupplier.addReflectionClass("lando.systems.ld58.particles.effects");
        TeaReflectionSupplier.addReflectionClass("com.github.tommyettinger.gdcrux");

        var cfg = new TeaBuildConfiguration();
        cfg.assetsPath.add(new AssetFileHandle("../assets"));
        cfg.shouldGenerateAssetFile = true;
        cfg.webappPath = new File("build/dist").getCanonicalPath();
        cfg.targetType = TeaTargetType.JAVASCRIPT;
        TeaBuilder.config(cfg);

        var tool = new TeaVMTool();
        tool.setObfuscated(!DEBUG);
        tool.setOptimizationLevel(TeaVMOptimizationLevel.SIMPLE);
//        tool.setOptimizationLevel(TeaVMOptimizationLevel.FULL);
//        tool.setOptimizationLevel(TeaVMOptimizationLevel.ADVANCED);
        tool.setMainClass(TeaVMLauncher.class.getName());

        tool.setDebugInformationGenerated(true);
        tool.setSourceMapsFileGenerated(true);
        tool.setSourceFilePolicy(TeaVMSourceFilePolicy.COPY);

        // TODO: pulled from original, maybe worth wrapping tool config above into this too for easier debug toggle
        if (DEBUG && tool.getTargetType() == TeaVMTargetType.JAVASCRIPT) {
            tool.addSourceFileProvider(new DirectorySourceFileProvider(new File("../core/src/main/java/")));
        }

        int size = 64 * (1 << 20);
        tool.setMaxDirectBuffersSize(size);
        TeaBuilder.build(tool);
    }

//    public static void ORIGINAL_main(String[] args) throws IOException {
//        TeaBuildConfiguration teaBuildConfiguration = new TeaBuildConfiguration();
//        teaBuildConfiguration.assetsPath.add(new AssetFileHandle("../assets"));
//        teaBuildConfiguration.webappPath = new File("build/dist").getCanonicalPath();
//
//        // Register any extra classpath assets here:
//        // teaBuildConfiguration.additionalAssetsClasspathFiles.add("lando/systems/ld58/asset.extension");
//
//        // Register any classes or packages that require reflection here:
//        // TeaReflectionSupplier.addReflectionClass("lando.systems.ld58.reflect");
//
//        TeaVMTool tool = TeaBuilder.config(teaBuildConfiguration);
//
//        // JavaScript is the default target type for TeaVM, and it works better during debugging.
//        tool.setTargetType(TeaVMTargetType.JAVASCRIPT);
//        // You can choose to use the WebAssembly (WASM) GC target instead, which tends to perform better, but isn't
//        // as easy to debug. It might be a good idea to alternate target types during development if you plan on using
//        // WASM at release time.
////        tool.setTargetType(TeaVMTargetType.WEBASSEMBLY_GC);
//
//        tool.setMainClass(TeaVMLauncher.class.getName());
//        // For many (or most) applications, using a high optimization won't add much to build time.
//        // If your builds take too long, and runtime performance doesn't matter, you can change ADVANCED to SIMPLE .
//        tool.setOptimizationLevel(TeaVMOptimizationLevel.ADVANCED);
//        // The line below should use tool.setObfuscated(false) if you want clear debugging info.
//        // You can change it to tool.setObfuscated(true) when you are preparing to release, to try to hide your original code.
//        tool.setObfuscated(!DEBUG);
//
//        // If targetType is set to JAVASCRIPT, you can use the following lines to debug JVM languages from the browser,
//        // setting breakpoints in Java code and stopping in the appropriate place in generated JavaScript code.
//        // These settings don't quite work currently if generating WebAssembly. They may in a future release.
//        if(DEBUG && tool.getTargetType() == TeaVMTargetType.JAVASCRIPT) {
//            tool.setDebugInformationGenerated(true);
//            tool.setSourceMapsFileGenerated(true);
//            tool.setSourceFilePolicy(TeaVMSourceFilePolicy.COPY);
//            tool.addSourceFileProvider(new DirectorySourceFileProvider(new File("../core/src/main/java/")));
//        }
//
//        TeaBuilder.build(tool);
//    }
}
