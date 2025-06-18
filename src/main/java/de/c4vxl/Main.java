package de.c4vxl;

import de.c4vxl.app.App;
import de.c4vxl.app.Theme;
import de.c4vxl.app.config.Config;
import de.c4vxl.app.language.Language;
import de.c4vxl.app.model.Model;
import de.c4vxl.app.util.Resource;

public class Main {
    public static void main(String[] args) {
        // Info logging
        System.out.println("[INFO]: Appdata path: " + Config.APP_DIRECTORY);
        System.out.println("[INFO]: Models path: " + Config.MODELS_DIRECTORY);
        System.out.println("[INFO]: Themes path: " + Config.THEMES_DIRECTORY);
        System.out.println("[INFO]: Languages path: " + Config.LANGS_DIRECTORY);
        System.out.println("[INFO]: Histories path: " + Config.HISTORIES_DIRECTORY);

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
        // Load dev-mode
        boolean isDevMode = ((boolean) Config.getOrSetConfigValue("app.isdev", false));
        System.out.println("[INFO]: isDevMode: " + isDevMode);

        // Load language
        Language language = Language.current = Config.getOrFallback(
                Language.load(Config.LANGS_DIRECTORY + "/" + Config.getOrSetConfigValue("app.lang", "english.lang")),
                Config.getLocalLangs(),
                "language"
        );
        System.out.println("[INFO]: Loaded language: " + language.file.getName());

        // Load theme
        Theme theme = Theme.current = Config.getOrFallback(
                Theme.fromFile(Config.THEMES_DIRECTORY + "/" + Config.getOrSetConfigValue("app.theme", "dark.theme")),
                Config.getLocalThemes(),
                "theme"
        );
        assert theme != null: "No theme found!";
        System.out.println("[INFO]: Loaded theme: " + theme.name);

        // Load model
        Model model = Model.current = Config.getOrFallback(
                Model.fromFile(Config.MODELS_DIRECTORY + "/" + Config.getOrSetConfigValue("app.model", isDevMode ? "__fake__" : "")),
                Config.getLocalModels(),
                "model"
        );
        if (model != null) {
            model.initialize();
            System.out.println("[INFO]: Loaded model: " + model.name);
        } else
            System.out.println("[INFO]: No model loaded!");
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