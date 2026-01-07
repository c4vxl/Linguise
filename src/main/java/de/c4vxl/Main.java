package de.c4vxl;

import de.c4vxl.app.App;
import de.c4vxl.app.onboarding.Onboarding;
import de.c4vxl.app.theme.Theme;
import de.c4vxl.app.config.Config;
import de.c4vxl.app.language.Language;
import de.c4vxl.app.lib.element.settings.SettingsPageKeyboardShortcuts;
import de.c4vxl.app.model.Model;
import de.c4vxl.app.util.Resource;
import de.c4vxl.app.util.TextUtils;
import de.c4vxl.core.tensor.grad.GradContext;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        // Parse arguments
        for (String arg : args) {
            arg = arg.toLowerCase();
            if (arg.startsWith("--app-dir=")) {
                System.out.println("[INFO]: Overwriting Config.APP_DIRECTORY due to program arguments!");
                Config.APP_DIRECTORY = arg.replace("--app-dir=", "");
                Config.reloadArgs();
            }

            if (arg.startsWith("--help")) {
                System.out.println("========================= Arguments =========================");
                System.out.println("[HELP]: --app-dir=<dir>   -   Set the directory the app will store data in.");
                System.exit(0);
            }
        }

        jNN.DEFAULT_REQUIRE_GRADIENT = false;
        GradContext.setNoGrad(true);

        start();
    }

    /**
     * Load configs and start a new instance of App
     */
    public static void start() {
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

        // Run onboarding
        if (Config.getOrSetConfigValue("app.onboarding", false) == Boolean.FALSE) {
            App.instance.close();
            App.instance.dispose();
            App.instance = null;
            System.out.println("[STARTUP]: Opening Onboarding...");
            Onboarding onboarding = new Onboarding(Language.current, Theme.current);
            onboarding.open();
        }
    }

    /**
     * Loads the data from the config file
     */
    private static void loadConfig() {
        // Load environment variables
        loadEnvVars();

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
    private static void loadEnvVars() {
        LinkedHashMap<String, String> env = new LinkedHashMap<>((Map<String, String>) Config.getOrSetConfigValue("env", new HashMap<>()));

        // Get config
        Config.MODELS_DIRECTORY = env.getOrDefault("models_dir", Config.MODELS_DIRECTORY);
        Config.MODEL_FILE_EXTENSION = env.getOrDefault("models_ext", Config.MODEL_FILE_EXTENSION);
        Config.THEMES_DIRECTORY = env.getOrDefault("themes_dir", Config.THEMES_DIRECTORY);
        Config.THEME_FILE_EXTENSION = env.getOrDefault("themes_ext", Config.THEME_FILE_EXTENSION);
        Config.LANGS_DIRECTORY = env.getOrDefault("langs_dir", Config.LANGS_DIRECTORY);
        Config.LANG_FILE_EXTENSION = env.getOrDefault("langs_ext", Config.LANG_FILE_EXTENSION);
        Config.HISTORIES_DIRECTORY = env.getOrDefault("chats_dir", Config.HISTORIES_DIRECTORY);
        Config.HISTORY_FILE_EXTENSION = env.getOrDefault("chats_ext", Config.HISTORY_FILE_EXTENSION);
        TextUtils.DATE_FORMAT = env.getOrDefault("date_format", TextUtils.DATE_FORMAT);
        jNN.MATMUL_TYPE = Integer.parseInt(env.getOrDefault("matmul_type", String.valueOf(jNN.MATMUL_TYPE)));
        SettingsPageKeyboardShortcuts.KEYBOARD_SHORTCUTS = new LinkedHashMap<>((Map<String, String>) Config.getOrSetConfigValue("keyboard_shortcuts", SettingsPageKeyboardShortcuts.KEYBOARD_SHORTCUTS));

        // Save config
        env.put("models_dir", Config.MODELS_DIRECTORY);
        env.put("models_ext", Config.MODEL_FILE_EXTENSION);
        env.put("themes_dir", Config.THEMES_DIRECTORY);
        env.put("themes_ext", Config.THEME_FILE_EXTENSION);
        env.put("langs_dir", Config.LANGS_DIRECTORY);
        env.put("langs_ext", Config.LANG_FILE_EXTENSION);
        env.put("chats_dir", Config.HISTORIES_DIRECTORY);
        env.put("chats_ext", Config.HISTORY_FILE_EXTENSION);
        env.put("date_format", TextUtils.DATE_FORMAT);
        env.put("matmul_type", String.valueOf(jNN.MATMUL_TYPE));
        Config.setConfigValue("env", env);

        // Log env
        System.out.println("[INFO]: Loading environment variables.");
        System.out.println("[ENV]: Models path: " + Config.MODELS_DIRECTORY);
        System.out.println("[ENV]: Model file extension: " + Config.MODEL_FILE_EXTENSION);
        System.out.println("[ENV]: Themes path: " + Config.THEMES_DIRECTORY);
        System.out.println("[ENV]: Theme file extension: " + Config.THEME_FILE_EXTENSION);
        System.out.println("[ENV]: Languages path: " + Config.LANGS_DIRECTORY);
        System.out.println("[ENV]: Language file extension: " + Config.LANG_FILE_EXTENSION);
        System.out.println("[ENV]: Histories path: " + Config.HISTORIES_DIRECTORY);
        System.out.println("[ENV]: History file extension: " + Config.HISTORY_FILE_EXTENSION);
        System.out.println("[ENV]: Date format: " + TextUtils.DATE_FORMAT);
        System.out.println("[ENV]: Matmul type: " + jNN.MATMUL_TYPE);
        System.out.println("[VAR]: Keyboard shortcuts: " + SettingsPageKeyboardShortcuts.KEYBOARD_SHORTCUTS);
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