package de.c4vxl;

import de.c4vxl.app.App;
import de.c4vxl.app.Theme;
import de.c4vxl.app.config.Config;
import de.c4vxl.app.language.Language;
import de.c4vxl.app.model.Model;
import de.c4vxl.app.util.Resource;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        // Info logging
        System.out.println("[INFO]: Appdata path: " + Config.APP_DIRECTORY);

        // Load defaults
        loadDefaults("theme", "themes/", Config.THEMES_DIRECTORY, false);
        loadDefaults("language", "languages/", Config.LANGS_DIRECTORY, false);

        // Load config
        System.out.println("[STARTUP]: Loading config...");
        loadConfig();

        // Open app
        System.out.println("[STARTUP]: Opening App...");
        new App(Theme.current, Language.current).open();
    }

    /**
     * Loads the data from the config file
     */
    private static void loadConfig() {
        // Load environment variables
        loadEnvs();

        // Load dev-mode
        boolean isDevMode = ((boolean) Config.getOrSetConfigValue("app.isdev", false));
        System.out.println("[INFO]: isDevMode: " + isDevMode);

        // Load language
        Language language = Config.setLanguage(Config.getOrFallback(
                Language.load(Config.LANGS_DIRECTORY + "/" + Config.getOrSetConfigValue("app.lang", "english.lang")),
                Config.getLocalLangs(),
                "language"
        ));
        System.out.println("[INFO]: Loaded language: " + language.file.getName());

        // Load theme
        Theme theme = Config.setTheme(Config.getOrFallback(
                Theme.fromFile(Config.THEMES_DIRECTORY + "/" + Config.getOrSetConfigValue("app.theme", "dark.theme")),
                Config.getLocalThemes(),
                "theme"
        ));
        assert theme != null: "No theme found!";
        System.out.println("[INFO]: Loaded theme: " + theme.name);

        // Load model
        String modelName = (String) Config.getOrSetConfigValue("app.model", isDevMode ? "__fake__" : null);
        Model model = Config.setModel(modelName == null ? null : Config.getOrFallback(
                Model.fromFile(Config.MODELS_DIRECTORY + "/" + modelName),
                Config.getLocalModels(),
                "model"
        ));
        if (model != null) {
            model.initialize();
            System.out.println("[INFO]: Loaded model: " + model.name);
        } else
            System.out.println("[INFO]: No model loaded!");
    }

    /**
     * Loads the environment variables from the config
     */
    @SuppressWarnings("unchecked")
    private static void loadEnvs() {
        LinkedHashMap<String, String> env = (LinkedHashMap<String, String>) Config.getConfigValue("env", HashMap.class);
        env = env == null ? new LinkedHashMap<>() : env;

        // Get config
        Config.MODELS_DIRECTORY = env.getOrDefault("models_dir", Config.MODELS_DIRECTORY);
        Config.MODEL_FILE_EXTENSION = env.getOrDefault("models_ext", Config.MODEL_FILE_EXTENSION);
        Config.THEMES_DIRECTORY = env.getOrDefault("themes_dir", Config.THEMES_DIRECTORY);
        Config.THEME_FILE_EXTENSION = env.getOrDefault("themes_ext", Config.THEME_FILE_EXTENSION);
        Config.LANGS_DIRECTORY = env.getOrDefault("langs_dir", Config.LANGS_DIRECTORY);
        Config.LANG_FILE_EXTENSION = env.getOrDefault("langs_ext", Config.LANG_FILE_EXTENSION);
        Config.HISTORIES_DIRECTORY = env.getOrDefault("chats_dir", Config.HISTORIES_DIRECTORY);
        Config.HISTORY_FILE_EXTENSION = env.getOrDefault("chats_ext", Config.HISTORY_FILE_EXTENSION);

        // Save config
        env.put("models_dir", Config.MODELS_DIRECTORY);
        env.put("models_ext", Config.MODEL_FILE_EXTENSION);
        env.put("themes_dir", Config.THEMES_DIRECTORY);
        env.put("themes_ext", Config.THEME_FILE_EXTENSION);
        env.put("langs_dir", Config.LANGS_DIRECTORY);
        env.put("langs_ext", Config.LANG_FILE_EXTENSION);
        env.put("chats_dir", Config.HISTORIES_DIRECTORY);
        env.put("chats_ext", Config.HISTORY_FILE_EXTENSION);
        Config.setConfigValue("env", env);

        // Log env
        System.out.println("[INFO]: Models path: " + Config.MODELS_DIRECTORY);
        System.out.println("[INFO]: Model file extension: " + Config.MODEL_FILE_EXTENSION);
        System.out.println("[INFO]: Themes path: " + Config.THEMES_DIRECTORY);
        System.out.println("[INFO]: Theme file extension: " + Config.THEME_FILE_EXTENSION);
        System.out.println("[INFO]: Languages path: " + Config.LANGS_DIRECTORY);
        System.out.println("[INFO]: Language file extension: " + Config.LANG_FILE_EXTENSION);
        System.out.println("[INFO]: Histories path: " + Config.HISTORIES_DIRECTORY);
        System.out.println("[INFO]: History file extension: " + Config.HISTORY_FILE_EXTENSION);
    }

    /**
     * Loads the default configs from the resources
     * @param name The name of the resources for logging
     * @param resourceDir The directory in resources
     * @param dest The destination directory to put the resources in
     * @param replace Should a previously loaded resources be replaced
     */
    private static void loadDefaults(String name, String resourceDir, String dest, boolean replace) {
        // Load default themes
        for (String element : Resource.listResources(resourceDir)) {
            String out = dest + "/" + element;
            element = resourceDir + element;

            System.out.println("[STARTUP]: Loading default " + name + ": " + element);
            Resource.copyResource(element, out, replace);
        }
    }
}