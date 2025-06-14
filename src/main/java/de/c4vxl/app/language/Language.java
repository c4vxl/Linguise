package de.c4vxl.app.language;

import com.google.gson.reflect.TypeToken;
import de.c4vxl.app.config.Config;
import de.c4vxl.app.util.FileUtils;

import java.io.File;
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
            System.out.println("[ --> ]: Falling back to " + fallback.file.getName());
            return fallback;
        } else {
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

        // Load translations
        HashMap<String, String> translations = FileUtils.fromJSON(FileUtils.readContent(path, "{}"), new TypeToken<>() {});

        // Handle empty language file
        if (translations.isEmpty()) {
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

        if (Objects.equals(translation, key))
            System.out.println("[ERROR]: Couldn't find a translation for " + key + "!");

        return translation;
    }
}