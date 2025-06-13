package de.c4vxl.app.util;

import de.c4vxl.app.Theme;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;

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
     */
    public static ImageIcon loadIcon(String path, int width) { return new ImageIcon(resizeImage(loadIcon(path).getImage(), width)); }

    /**
     * Copy a resource to the file system
     *
     * @param path The path to the resource
     * @param to   The path to copy the resource to
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void copyResource(String path, String to) {
        File parent = new File(to);
        if (parent.getParentFile() != null)
            parent.getParentFile().mkdirs();

        try (InputStream is = Theme.class.getClassLoader().getResourceAsStream(path)) {
            assert is != null;
            Files.copy(is, Paths.get(to));
        } catch (Exception ignored) {}
    }

    /**
     * Get a list of all resources in a path
     * @param path The path/directory
     */
    public static String[] listResources(String path) {
        try {
            return Arrays.stream(Objects.requireNonNull(new File(Objects.requireNonNull(Resource.class.getClassLoader().getResource(path)).toURI())
                    .listFiles())).map(File::getName).toArray(String[]::new);
        } catch (URISyntaxException e) {
            return new String[0];
        }
    }
}