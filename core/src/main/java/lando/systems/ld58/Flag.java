package lando.systems.ld58;

import com.github.tommyettinger.digital.Stringf;
import lando.systems.ld58.utils.Util;

/**
 * Flags for enabling/disabling certain features, mostly used for debugging.
 * {@link Flag#GLOBAL} can be disabled to globally ignore any debug flag,
 * intended for disabling all debug features at once in production builds
 */
public enum Flag {
    //@formatter:off
      GLOBAL        (true)
    , LOG_GENERAL   (true)
    , LOG_DEBUG     (true)
    , LOG_WARN      (true)
    , LOG_INPUT     (false)
    , DEBUG_RENDER  (false)
    , DEBUG_UI      (false)
    , FRAME_STEP    (false)
    , LAUNCH_SCREEN (true)
    , GAME_SCREEN   (false)
    ;
    //@formatter:on

    private boolean isEnabled;

    Flag(boolean isEnabled) {
        // NOTE: checks for JVM flags in order to override the initial value,
        //  without modifying code and leaving uncommitted in version control...
        //  usage: in run config 'jvm options' add: -Dapp.{flag_name_lowercase}=[true|false]
        //  see: lwjgl3/build.gradle - run { systemProperties ... }
        // TODO: gwt doesn't like this because it expects a string constant for System.getProperty(String)
        //var override = System.getProperty(name().toLowerCase());
        //if (override != null) {
        //    isEnabled = Boolean.parseBoolean(override);
        //}
        this.isEnabled = isEnabled;
    }

    public boolean isEnabled() {
        return GLOBAL.isEnabled && isEnabled;
    }

    public boolean isDisabled() {
        return !GLOBAL.isEnabled || !isEnabled;
    }

    /**
     * @return whether the specified flag is enabled or not, taking into account {@link Flag#GLOBAL} for global control
     */
    public static boolean isEnabled(Flag flag) {
        return GLOBAL.isEnabled && flag.isEnabled;
    }

    /**
     * Enables this flag, regardless of its current status
     */
    public boolean enable() {
        isEnabled = true;
        Util.log(Stringf.format("Enabled Flag.%s='%b'", name(), isEnabled));
        return isEnabled;
    }

    /**
     * Disables this flag, regardless of its current status
     */
    public boolean disable() {
        isEnabled = false;
        Util.log(Stringf.format("Disabled Flag.%s='%b'", name(), isEnabled));
        return isEnabled;
    }

    /**
     * Enables or disables this flag, based of the specified value regardless of its current status
     *
     * @param enabled whether to enable or disable this flag
     * @return the new value of this flag, or false if a null flag type was provided
     */
    public boolean set(boolean enabled) {
        isEnabled = enabled;
        Util.log(Stringf.format("Set Flag.%s='%b'", name(), isEnabled));
        return isEnabled;
    }

    /**
     * Toggles whether this flag is enabled or disabled
     *
     * @return the new value of this flag, after toggling
     */
    public boolean toggle() {
        isEnabled = !isEnabled;
        Util.log(Stringf.format("Toggled Flag.%s='%b'", name(), isEnabled));
        return isEnabled;
    }
}
