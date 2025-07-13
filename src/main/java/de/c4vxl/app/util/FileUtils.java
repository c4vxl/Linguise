package de.c4vxl.app.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import de.c4vxl.app.App;
import de.c4vxl.app.language.Language;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URI;
import java.util.Arrays;
import java.util.function.Consumer;

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
        return readContent(path, fallback, percentage -> {});
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
        FileDialog dialog = new FileDialog((Frame) null, Language.current.get("app.global.filedialog.title", fileTypeName));
        dialog.setFilenameFilter((dir, name) -> Arrays.stream(acceptedFileExtensions).anyMatch(name::endsWith));
        dialog.setDirectory(startPath);

        dialog.setVisible(true);

        File[] files = dialog.getFiles();
        if (files.length != 0) {
            File file = files[0];
            System.out.println("[ACTION]: Got file from user: " + file.getAbsolutePath());
            return file;
        }

        return null;
    }

    /**
     * Opens a directory dialog
     * @param startPath The path to open initially
     */
    public static File openDirDialog(String startPath) {
        // Using JFileChooser because FileDialog doesn't support directory selections
        JFileChooser dialog = new JFileChooser(new File(startPath));
        dialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (dialog.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = dialog.getSelectedFile();
            System.out.println("[ACTION]: Got dir from user: " + file.getAbsolutePath());
            return file;
        }

        return null;
    }

    /**
     * Returns the size of a file in a human-readable format
     * @param path The path to the file
     */
    public static String fileSize(String path) {
        File file = new File(path);
        if (!file.isFile())
            return "---";

        return toReadable(file.length());
    }

    /**
     * Converts a bytes length to a human-readable format
     * @param bytes The amount of bytes
     */
    public static String toReadable(long bytes) {
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        int i = 0;
        double size = bytes;
        while (size >= 1024 && i < units.length - 1) {
            size /= 1024;
            i++;
        }
        return String.format("%.1f %s", size, units[i]);
    }

    /**
     * Reads the content of a file
     * @param path The path to the file
     * @param fallback A fallback string if reading goes wrong
     * @param onUpdate Pass the handler for updates. The current percentage will be passed!
     */
    public static String readContent(String path, String fallback, Consumer<Integer> onUpdate) {
        File file = new File(path);
        if (!file.isFile()) return fallback;
        long totalBytes = file.length(), bytesRead = 0;

        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            int ch;
            int last = -1;
            while ((ch = reader.read()) != -1) {
                content.append((char) ch);
                bytesRead++;
                int next = (int) (100L * bytesRead / totalBytes);
                if (last != next) {
                    last = next;
                    onUpdate.accept(next);
                }
            }
        } catch (IOException e) {
            onUpdate.accept(100);
            return fallback;
        }

        onUpdate.accept(100);

        return content.toString();
    }

    /**
     * Downloads a file from an url into a local file
     * @param url The url to the file
     * @param outPath The output path the file should be downloaded into
     * @param onUpdate Pass the handler for updates. The current percentage will be passed!
     */
    public static void downloadFile(String url, String outPath, Consumer<Integer> onUpdate) {
        try (BufferedInputStream inputStream = new BufferedInputStream(new URI(url).toURL().openStream());
             FileOutputStream outputStream = new FileOutputStream(outPath)) {

            int fileSize = new URI(url).toURL().openConnection().getContentLength();

            byte[] dataBuffer = new byte[1024];
            int bytesRead, totalBytesRead = 0;
            int last = -1;

            while ((bytesRead = inputStream.read(dataBuffer, 0, 1024)) != -1) {
                outputStream.write(dataBuffer, 0, bytesRead);
                totalBytesRead += bytesRead;
                int next = (int) (100L * totalBytesRead / fileSize);
                if (last != next) {
                    last = next;
                    onUpdate.accept(next);
                }
            }

        } catch (Exception e) {
            App.notificationFromKey("danger", 200, "app.notifications.global.error.download_fail", url);
            System.out.println("[ERROR]: Error while downloading " + url);
        }

        onUpdate.accept(100);
    }
}