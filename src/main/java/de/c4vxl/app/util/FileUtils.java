package de.c4vxl.app.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import de.c4vxl.app.config.Config;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;

public class FileUtils {
    /**
     * Gets or creates a file from its path
     * @param path The path
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static File getOrCreateFile(String path) {
        File file = new File(path);
        try {
            if (file.getParentFile() != null)
                file.getParentFile().mkdirs();
            file.createNewFile();
        } catch (IOException e) { throw new RuntimeException(e); }

        return file;
    }

    /**
     * Gets or creates a directory from its path
     * @param path The path
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static File getOrCreateDirectory(String path) {
        File file = new File(path);
        file.mkdirs();
        return file;
    }

    /**
     * Reads the content of a file
     * @param path The path to the file
     * @param fallback A fallback string if reading goes wrong
     */
    public static String readContent(String path, String fallback) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        } catch (IOException e) { return fallback; }

        return content.toString();
    }

    /**
     * Writes content to a file
     * @param path The path to the file
     * @param content The content to write
     */
    public static void writeContent(String path, String content) {
        try (FileWriter writer = new FileWriter(getOrCreateFile(path))) {
            writer.write(content);
        } catch (IOException e) { throw new RuntimeException(e); }
    }

    /**
     * Loads a json string
     * @param data The json in string format
     * @param token The type to load it in
     */
    public static <T> T fromJSON(String data, TypeToken<T> token) {
        return new Gson().fromJson(data, token);
    }

    /**
     * Encodes an object into json format
     * @param data The element to encode
     * @param isPretty If true pretty printing will be enabled
     */
    public static String toJSON(Object data, boolean isPretty) {
        GsonBuilder builder = new GsonBuilder();
        if (isPretty) builder = builder.setPrettyPrinting();
        return builder.create().toJson(data);
    }

    /**
     * Opens a file dialog
     * @param startPath The path to open initially
     * @param fileTypeName The type of file
     * @param acceptedFileExtensions The possible extensions of the type
     */
    public static File openFileDialog(String startPath, String fileTypeName, String[] acceptedFileExtensions) {
        JFileChooser fileChooser = new JFileChooser(startPath);
        fileChooser.setFileFilter(new FileNameExtensionFilter(
                fileTypeName, Arrays.stream(acceptedFileExtensions).map(x -> x.replace(".", "")).toArray(String[]::new)
        ));

        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            System.out.println("[ACTION]: Got file from user: " + file.getAbsolutePath());
            return file;
        }

        return null;
    }
}