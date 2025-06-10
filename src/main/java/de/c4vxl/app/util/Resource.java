package de.c4vxl.app.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.function.Consumer;

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
}
