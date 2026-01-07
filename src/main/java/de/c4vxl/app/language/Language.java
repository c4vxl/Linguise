package de.c4vxl.app.language;

import com.google.gson.reflect.TypeToken;
import de.c4vxl.app.App;
import de.c4vxl.app.config.Config;
import de.c4vxl.app.theme.Theme;
import de.c4vxl.app.util.FileUtils;
import de.c4vxl.app.util.Resource;

import java.awt.*;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class Language {
    public final File file;
    public final String path;
    public final String name;
    public final HashMap<String, String> translations;

    public Language(File file, HashMap<String, String> translations) {
        this.file = file;
        this.path = file != null ? file.getPath() : "";
        this.translations = translations;

        if (file != null) {
            String n = file.getName().split("\\.")[0];
            this.name = n.substring(0, 1).toUpperCase() + n.substring(1).toLowerCase();
        } else
            this.name = Language.current != null ? Language.current.get("app.settings.language.name.unknown") : "unknown";
    }

    public static Language fallback = load(Config.LANGS_DIRECTORY + "/" + "english" + Config.LANG_FILE_EXTENSION);
    public static Language current = null;

    /**
     * Returns a fallback language
     */
    public static Language getFallback() {
        if (fallback != null && fallback.file != null) {
            App.notificationFromKey("accent", 300, "app.notifications.language.info.falling_back", fallback.file.getName());
            System.out.println("[ --> ]: Falling back to " + fallback.file.getName());
            return fallback;
        } else {
            App.notificationFromKey("danger", 300, "app.notifications.language.error.no_fallback_found");
            System.out.println("[ERROR]: Couldn't find fallback language!");
            return new Language(null, new HashMap<>());
        }
    }

    /**
     * Loads translations from file
     * @param path The path to the file
     */
    public static Language load(String path) {
        // Check if file exists
        File file = new File(path);

        // Handle non-existent language
        if (!file.isFile()) {
            System.out.println("[ERROR]: Language file not found: " + path);
            return getFallback();
        }

        return interpret(path, FileUtils.readContent(path, "{}"));
    }

    /**
     * Loads a Language from jar-packed resources
     * @param name The name of the language
     */
    public static Language fromResource(String name) {
        String filename = "languages/" + name + ".lang";
        return interpret(filename, Resource.readResource(filename));
    }

    /**
     * Interprets a language from it's json
     * @param path The file the json comes from
     * @param string The json in string representation
     */
    private static Language interpret(String path, String string) {
        File file = new File(path);

        // Load translations
        HashMap<String, String> translations = FileUtils.fromJSON(string, new TypeToken<>() {});

        // Handle empty language file
        if (translations.isEmpty()) {
            App.notificationFromKey("danger", 300, "app.notifications.language.error.invalid_file", file.getName());
            System.out.println("[ERROR]: Not a language file: " + path);
            return getFallback();
        }

        return new Language(file, translations);
    }

    /**
     * Get the translation from a key
     * @param key The key
     * @param args Possible arguments
     */
    public String get(String key, String... args) {
        // Get translation
        String translation = translations.getOrDefault(key, key);

        // Apply arguments
        for (int i = 0; i < args.length; i++)
            translation = translation.replace("$" + (i + 1), args[i]);

        if (Objects.equals(translation, key)) {
            App.notificationFromKey("danger", 300, "app.notifications.language.error.no_translation", key, file.getName());
            System.out.println("[ERROR]: Couldn't find a translation for " + key + "!");
        }

        return translation;
    }
}