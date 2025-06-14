package de.c4vxl;

import de.c4vxl.app.App;
import de.c4vxl.app.Theme;
import de.c4vxl.app.config.Config;
import de.c4vxl.app.language.Language;
import de.c4vxl.app.util.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        // Info logging
        System.out.println("[INFO]: Appdata path: " + Config.APP_DIRECTORY);
        System.out.println("[INFO]: Models path: " + Config.MODELS_DIRECTORY);
        System.out.println("[INFO]: Themes path: " + Config.THEMES_DIRECTORY);
        System.out.println("[INFO]: Languages path: " + Config.LANGS_DIRECTORY);
        System.out.println("[INFO]: Histories path: " + Config.HISTORIES_DIRECTORY);

        // Load defaults
        loadDefaults("theme", "themes/", Config.THEMES_DIRECTORY);
        loadDefaults("language", "languages/", Config.LANGS_DIRECTORY);

        // Load config
        System.out.println("[STARTUP]: Loading config...");
        Theme theme = getTheme();
        System.out.println("[INFO]: Loaded theme: " + theme.name);
        Language language = Language.load(Config.LANGS_DIRECTORY + "/" + Config.getOrSetConfigValue("app.lang", "english.lang"));
        System.out.println("[INFO]: Loaded language: " + language.file.getName());

        // Open app
        System.out.println("[STARTUP]: Opening App...");
        new App(theme, language).open();
    }

    /**
     * Loads the default configs from the resources
     * @param name The name of the resources for logging
     * @param resourceDir The directory in resources
     * @param dest The destination directory to put the resources in
     */
    private static void loadDefaults(String name, String resourceDir, String dest) {
        // Load default themes
        for (String element : Resource.listResources(resourceDir)) {
            String out = dest + "/" + element;
            element = resourceDir + element;

            System.out.println("[STARTUP]: Loading default " + name + ": " + element);
            try {
                Files.deleteIfExists(Path.of(out));
            } catch (IOException ignored) { }
            Resource.copyResource(element, out);
        }
    }

    /**
     * Get the current theme or return a fallback theme
     */
    private static Theme getTheme() {
        // Load dark theme
        Theme theme = Theme.fromFile(Config.THEMES_DIRECTORY + "/" + Config.getOrSetConfigValue("app.theme", "dark.theme"));

        // Handle theme not found
        if (theme == null) {
            System.out.println("[ERROR]: Couldn't find default theme");

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
}