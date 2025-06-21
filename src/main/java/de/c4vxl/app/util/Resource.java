package de.c4vxl.app.util;

import com.google.gson.reflect.TypeToken;
import de.c4vxl.app.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class Resource {
    /**
     * Load a resource
     * @param path The path to the resource
     */
    public static URL loadResource(String path) { return Resource.class.getClassLoader().getResource(path); }

    /**
     * Resize an image
     * @param image The image
     * @param width The width to resize the image to
     * @param height The height to resize the image to
     */
    public static Image resizeImage(Image image, int width, int height) { return image.getScaledInstance(width, height, Image.SCALE_SMOOTH); }

    /**
     * Resize an image
     * @param image The image
     * @param width The width to resize the image to (calculate height via aspect ratio)
     */
    public static Image resizeImage(Image image, int width) { return resizeImage(image, width,
                (int) ((((double) image.getHeight(null)) / image.getWidth(null)) * width));
    }

    /**
     * Load a resource as an image
     * @param path The path to the image
     */
    public static ImageIcon loadIcon(String path) { return new ImageIcon(loadResource(path)); }

    /**
     * Load a resource as an image
     * @param path The path to the image
     * @param width The width to resize the image to
     * @param height The height to resize the image to
     */
    public static ImageIcon loadIcon(String path, int width, int height) { return new ImageIcon(resizeImage(loadIcon(path).getImage(), width, height)); }

    /**
     * Load a resource as an image
     * @param path The path to the image
     * @param width The width to resize the image to (calculate height via aspect ratio)
     * @param color The color of the icon
     */
    public static ImageIcon loadIcon(String path, int width, Color color) { return recolor(new ImageIcon(resizeImage(loadIcon(path).getImage(), width)), color); }

    /**
     * Recolors an icon
     * @param image The icon
     * @param color The new color
     */
    public static ImageIcon recolor(ImageIcon image, Color color) { return new ImageIcon(recolor(image.getImage(), color)); }

    /**
     * Recolors an image
     * @param image The image
     * @param color The new color
     */
    public static Image recolor(Image image, Color color) {
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        bufferedImage.getGraphics().drawImage(image, 0, 0, null);

        for (int y = 0; y < image.getHeight(null); y++) {
            for (int x = 0; x < image.getWidth(null); x++) {
                Color original = new Color(bufferedImage.getRGB(x, y), true);
                if (original.getRed() == 255 && original.getGreen() == 255 && original.getBlue() == 255) {
                    bufferedImage.setRGB(x, y, new Color(color.getRed(), color.getGreen(), color.getBlue(), original.getAlpha()).getRGB());
                }
            }
        }

        return bufferedImage;
    }

    /**
     * Copy a resource to the file system
     *
     * @param path The path to the resource
     * @param to   The path to copy the resource to
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void copyResource(String path, String to, boolean replace) {
        File parent = new File(to);
        if (parent.getParentFile() != null)
            parent.getParentFile().mkdirs();
        if (replace) {
            try { Files.deleteIfExists(Paths.get(to)); }
            catch (IOException e) { throw new RuntimeException(e); }
        }

        try (InputStream is = Theme.class.getClassLoader().getResourceAsStream(path)) {
            assert is != null;
            Files.copy(is, Paths.get(to));
        } catch (Exception ignored) {}
    }

    /**
     * Returns the content of a resource file as a string
     * @param path The path to the resource
     */
    public static String readResource(String path) {
        InputStream stream = Resource.class.getClassLoader().getResourceAsStream(path);
        if (stream == null) return null;

        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            int ch;
            while ((ch = reader.read()) != -1) content.append((char) ch);
        } catch (IOException e) { return null; }

        return content.toString();
    }

    /**
     * Get a list of all resources in a path
     * @param path The path/directory
     */
    public static String[] listResources(String path) {
        HashMap<String, ArrayList<String>> data = FileUtils.fromJSON(Resource.readResource("resources.json"), new TypeToken<>() {});
        if (data == null) return new String[0];

        return data.getOrDefault(path, new ArrayList<>()).toArray(String[]::new);
    }
}