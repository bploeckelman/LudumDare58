package lando.systems.ld58.teavm;

import com.github.xpenatan.gdx.backends.teavm.config.AssetFileHandle;
import com.github.xpenatan.gdx.backends.teavm.config.TeaBuildConfiguration;
import com.github.xpenatan.gdx.backends.teavm.config.TeaBuilder;
import com.github.xpenatan.gdx.backends.teavm.config.TeaTargetType;
import com.github.xpenatan.gdx.backends.teavm.config.plugins.TeaReflectionSupplier;
import java.io.File;
import java.io.IOException;
import java.util.List;

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

    public static void main(String[] args) throws IOException {
        var buildConfig = new TeaBuildConfiguration();
        buildConfig.assetsPath.add(new AssetFileHandle("../assets"));
        buildConfig.webappPath = new File("build/dist").getCanonicalPath();
        buildConfig.targetType = TeaTargetType.JAVASCRIPT;

        var reflection = List.of(
            "com.badlogic.ashley.core.Entity"
            , "lando.systems.ld58.assets"
            , "lando.systems.ld58.game.components"
            , "lando.systems.ld58.game.components.collision"
            , "lando.systems.ld58.particles"
            , "lando.systems.ld58.particles.effects"
            , "com.github.tommyettinger.gdcrux"
            , "com.kotcrab.vis.ui.Sizes"
            , "com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator"
        );

        buildConfig.reflectionListener = fullClassName -> {
            for (var name : reflection) {
                if (fullClassName.startsWith(name)) {
                    return true;
                }
            }
            // Catch-all for Style types
            if (fullClassName.startsWith("com.kotcrab.vis.ui") && fullClassName.endsWith("Style")) {
                return true;
            }
            return false;
        };
        buildConfig.assetsClasspath.add("com/kotcrab/vis/ui/skin");

        TeaBuilder.config(buildConfig);
        TeaReflectionSupplier.printReflectionClasses();

        var tool = new TeaVMTool();
        tool.setMainClass(TeaVMLauncher.class.getName());
        tool.setOptimizationLevel(TeaVMOptimizationLevel.SIMPLE);
        tool.setObfuscated(!DEBUG);
        tool.setDebugInformationGenerated(DEBUG);
        tool.setSourceMapsFileGenerated(DEBUG);
        // TODO: not sure what this impacts, but since I'm having an issue with loading stuff via asset mgr and it seems like the issue might be down in a native copy operation, worth trying this out to see if it makes a difference
        tool.setMaxDirectBuffersSize(64 * (1 << 20));
        TeaBuilder.build(tool);


        // ------------------------------------------------------------------------------------

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
    }
}
