package de.c4vxl.app.theme;

import com.google.gson.reflect.TypeToken;
import de.c4vxl.app.App;
import de.c4vxl.app.util.FileUtils;
import de.c4vxl.app.util.Resource;

import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.function.Function;

public class Theme {
    public final String name;
    public final Color accent, accent_1, accent_2, background, background_1, background_2, background_3, text, text_1, danger;
    public final Font font, font_2;
    private final String fileName;

    public Theme(String file, String name, Font font, Font font_2, Color accent, Color accent_1, Color accent_2, Color background, Color background_1, Color background_2, Color background_3, Color text, Color text_1, Color danger) {
        this.font = font;
        this.font_2 = font_2;

        this.accent = accent;
        this.accent_1 = accent_1;
        this.accent_2 = accent_2;
        this.background = background;
        this.background_1 = background_1;
        this.background_2 = background_2;
        this.background_3 = background_3;
        this.text = text;
        this.text_1 = text_1;
        this.danger = danger;

        this.name = name;
        this.fileName = file;
    }

    public String getFileName() { return fileName; }

    /**
     * Export this theme to a file
     * @param path The path to the file
     */
    public void export(String path) {
        Function<Color, String> toHex = (color) -> String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());

        FileUtils.writeContent(path, FileUtils.toJSON(new LinkedHashMap<>() {{
            put("name", name);
            put("accent", toHex.apply(accent));
            put("accent_1", toHex.apply(accent_1));
            put("accent_2", toHex.apply(accent_2));
            put("background", toHex.apply(background));
            put("background_1", toHex.apply(background_1));
            put("background_2", toHex.apply(background_2));
            put("background_3", toHex.apply(background_3));
            put("text", toHex.apply(text));
            put("text_1", toHex.apply(text_1));
            put("danger", toHex.apply(danger));
            put("font", font.getName());
            put("font_2", font_2.getName());
        }}, true));
    }

    /**
     * Loads a Theme from a file
     * @param path The path to the file
     */
    public static Theme fromFile(String path) {
        String filename = Path.of(path).getFileName().toString();
        return interpret(filename, FileUtils.readContent(path, "{}"));
    }

    /**
     * Loads a Theme from jar-packed resources
     * @param name The name of the theme
     */
    public static Theme fromResource(String name) {
        String filename = "themes/" + name + ".theme";
        return interpret(name + ".theme", Resource.readResource(filename));
    }

    /**
     * Interprets a theme from it's json
     * @param fileName The filename the json comes from
     * @param string The json in string representation
     */
    private static Theme interpret(String fileName, String string) {
        HashMap<String, String> content = FileUtils.fromJSON(string, new TypeToken<>() {});
        Field[] args = Arrays.stream(Theme.class.getFields()).filter(x -> !Modifier.isStatic(x.getModifiers())).toArray(Field[]::new);

        // Return if file doesn't contain all needed variables
        if (Arrays.stream(args).map(Field::getName).map(content::containsKey).filter(x -> !x).toList().contains(false)) {
            App.notificationFromKey("danger", 300, "app.notifications.themes.error.invalid_file", fileName);
            return null;
        }

        return new Theme(
                fileName,
                content.get("name"),
                new Font(content.get("font"), Font.PLAIN, 17),
                new Font(content.get("font_1"), Font.PLAIN, 17),
                Color.decode(content.get("accent")),
                Color.decode(content.get("accent_1")),
                Color.decode(content.get("accent_2")),
                Color.decode(content.get("background")),
                Color.decode(content.get("background_1")),
                Color.decode(content.get("background_2")),
                Color.decode(content.get("background_3")),
                Color.decode(content.get("text")),
                Color.decode(content.get("text_1")),
                Color.decode(content.get("danger"))
        );
    }


    public static Theme current;
}