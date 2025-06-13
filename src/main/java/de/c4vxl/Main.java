package de.c4vxl;

import de.c4vxl.app.App;
import de.c4vxl.app.Theme;
import de.c4vxl.app.config.Config;
import de.c4vxl.app.util.Resource;

public class Main {
    public static void main(String[] args) {
        // Info logging
        System.out.println("[INFO]: Appdata path: " + Config.APP_DIRECTORY);
        System.out.println("[INFO]: Models path: " + Config.MODELS_DIRECTORY);
        System.out.println("[INFO]: Themes path: " + Config.THEMES_DIRECTORY);
        System.out.println("[INFO]: Languages path: " + Config.LANGS_DIRECTORY);
        System.out.println("[INFO]: Histories path: " + Config.HISTORIES_DIRECTORY);

        // Load default themes
        for (String theme : Resource.listResources("themes")) {
            String out = Config.THEMES_DIRECTORY + "/" + theme;
            theme = "themes/" + theme;

            System.out.println("[STARTUP]: Copying loading default theme: " + theme);
            Resource.copyResource(theme, out);
        }

        // Load config
        System.out.println("[STARTUP]: Loading config...");
        Theme.current = Theme.fromFile(Config.THEMES_DIRECTORY + "/" + Config.getOrSetConfigValue("app.theme", "dark.theme"));
        Config.getOrSetConfigValue("app.lang", "english");

        // Open app
        System.out.println("[STARTUP]: Opening App...");
        new App().open();
    }
}