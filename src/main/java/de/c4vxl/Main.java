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
        Theme theme = getTheme();
        System.out.println("[INFO]: Loaded theme: " + theme.name);
        Language.current = Language.load(Config.LANGS_DIRECTORY + "/" + Config.getOrSetConfigValue("app.lang", "english.lang"));
        System.out.println("[INFO]: Loaded language: " + Language.current.file.getName());
        Model.current = getModel();
        System.out.println("[INFO]: Loaded model: " + (Model.current == null ? "None" : Model.current.name));

        // Open app
        System.out.println("[STARTUP]: Opening App...");
        new App(theme, Language.current).open();
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

    /**
     * Get the current theme or return a fallback
     */
    private static Theme getTheme() {
        // Load dark theme
        Theme theme = Theme.fromFile(Config.THEMES_DIRECTORY + "/" + Config.getOrSetConfigValue("app.theme", "dark.theme"));

        // Handle theme not found
        if (theme == null) {
            System.out.println("[ERROR]: Couldn't find theme");

            // Find fallback
            Theme[] themes = Config.getLocalThemes();
            if (themes.length == 0) {
                System.out.println("[ERROR]: No fallback theme found!");
                System.exit(1);
            }
            Theme fallback = themes[0];

            System.out.println("[ --> ]: Falling back to " + fallback.name);
            return fallback;
        }

        return theme;
    }

    /**
     * Get the current model or return a fallback
     */
    private static Model getModel() {
        // Load model
        Model model = Model.fromFile(Config.MODELS_DIRECTORY + "/" + Config.getOrSetConfigValue("app.model", "__fake__"));

        // Handle theme not found
        if (model == null) {
            System.out.println("[ERROR]: Couldn't find model");

            System.out.println("[ --> ]: Falling back to fake model");
            return ((boolean) Config.getOrSetConfigValue("app.isdev", false)) ? Model.getFakeModel(1) : null;
        }

        return model;
    }
}