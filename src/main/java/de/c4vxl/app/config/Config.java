package de.c4vxl.app.config;

import com.google.gson.reflect.TypeToken;
import de.c4vxl.app.App;
import de.c4vxl.app.Theme;
import de.c4vxl.app.language.Language;
import de.c4vxl.app.model.Model;
import de.c4vxl.app.util.FileUtils;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.stream.Stream;

public class Config {
    // Global paths
    public static String APP_DIRECTORY = "./appdata";                      // Path to the app's configuration directory
    public static String MODELS_DIRECTORY = APP_DIRECTORY + "/models";     // Path to the models
    public static String THEMES_DIRECTORY = APP_DIRECTORY + "/themes";     // Path to the themes
    public static String LANGS_DIRECTORY = APP_DIRECTORY + "/langs";       // Path to the languages
    public static String HISTORIES_DIRECTORY = APP_DIRECTORY + "/chats";   // Path to the chat histories
    public static String CONFIG_FILE = APP_DIRECTORY + "/app.lconf";       // Path to the configuration file
    public static String MODEL_FILE_EXTENSION = ".mdl";                    // File extension for model files
    public static String THEME_FILE_EXTENSION = ".theme";                  // File extension for theme files
    public static String LANG_FILE_EXTENSION = ".lang";                    // File extension for language files

    /**
     * Gets the content of the config file
     */
    public static HashMap<String, Object> getConfig() {
        return FileUtils.fromJSON(FileUtils.readContent(Config.CONFIG_FILE, "{}"), new TypeToken<>() {});
    }

    /**
     * Sets a value in the config
     * @param key The key of the element
     * @param value The new value for the element. Set to null to remove
     */
    public static void setConfigValue(String key, Object value) {
        HashMap<String, Object> config = getConfig();
        if (value == null)
            config.remove(key);
        else
            config.put(key, value);

        FileUtils.writeContent(Config.CONFIG_FILE, FileUtils.toJSON(config, true));
    }

    /**
     * Gets a value in the config or sets if it isn't present
     * @param key The key of the element
     * @param value The new value for the element. Set to null to remove
     */
    public static Object getOrSetConfigValue(String key, Object value) {
        Object obj = getConfigValue(key, Object.class);
        if (obj == null) {
            setConfigValue(key, value);
            return value;
        }
        return obj;
    }

    /**
     * Gets an element from the config
     * @param key The key of the element
     * @param clazz A class to cast the element to
     */
    public static <T> T getConfigValue(String key, Class<T> clazz) {
        HashMap<String, Object> config = getConfig();
        Object object = config.get(key);
        if (object == null) return null;
        if (clazz.isInstance(object)) return clazz.cast(object);
        else return null;
    }

    /**
     * Get a list of all filenames in a folder
     * @param path The directory to search
     * @param extension The extension to look for
     */
    private static String[] listFiles(String path, String extension) {
        return Arrays.stream(Objects.requireNonNull(FileUtils.getOrCreateDirectory(path).listFiles((dir, name) -> name.endsWith(extension))))
                .map(File::getPath).toArray(String[]::new);
    }

    /**
     * Gets a list of all locally installed models
     */
    public static Model[] getLocalModels() {
        Model[] models = Arrays.stream(listFiles(Config.MODELS_DIRECTORY, Config.MODEL_FILE_EXTENSION))
                .map(Model::fromFile).filter(Objects::nonNull).toArray(Model[]::new);

        if ((boolean) getOrSetConfigValue("app.isdev", false)) {
            models = Stream.concat(Stream.of(Model.getFakeModel(1)), Arrays.stream(models)).toArray(Model[]::new);
        }

        return models;
    }

    /**
     * Gets a list of all locally installed languages
     */
    public static Language[] getLocalLangs() {
        return Arrays.stream(listFiles(Config.LANGS_DIRECTORY, Config.LANG_FILE_EXTENSION))
                .map(Language::load).toArray(Language[]::new);
    }

    /**
     * Returns a list of locally found themes
     */
    public static Theme[] getLocalThemes() {
        return Arrays.stream(listFiles(Config.THEMES_DIRECTORY, Config.THEME_FILE_EXTENSION))
                .map(Theme::fromFile).toArray(Theme[]::new);
    }

    /**
     * Returns an element or if it is null the first option out of a list of fallbacks that isn't
     * @param element The initial element
     * @param fallbacks The fallback options
     * @param loggingName The name of the items (for logging)
     */
    public static <T> T getOrFallback(T element, T[] fallbacks, String loggingName) {
        if (element != null) return element;

        // No fallbacks found
        if (fallbacks.length == 0) {
            System.out.println("[ERROR]: No fallback " + loggingName + " found");
            return null;
        }

        // Get first fallback option or continue looking for one
        element = getOrFallback(
                fallbacks[0],
                Arrays.copyOfRange(fallbacks, 1, fallbacks.length),
                loggingName
        );

        // No element in fallbacks works
        if (element == null) {
            System.out.println("[ERROR]: Couldn't find a fallback " + loggingName);
            App.notificationFromKey("danger", 200, "app.notifications.global.error.no_fallback_found", loggingName);
            return null;
        }

        return element;
    }
}