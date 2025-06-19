package de.c4vxl.app.util;

import de.c4vxl.app.App;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TextUtils {
    public static String DATE_FORMAT = "dd.MM.yyyy - HH:mm";

    /**
     * Cut a string to a certain max width
     * @param str The width
     * @param maxWidth The maximum width of the string
     * @param end Add after the cut (e.g. "...")
     */
    public static String cutString(String str, String end, Font font, int maxWidth) {
        FontMetrics metrics = new Canvas().getFontMetrics(font);
        int strWidth = metrics.stringWidth(str);
        int maxChars = maxWidth / metrics.stringWidth("a");
        return strWidth > maxWidth ? str.substring(0, maxChars) + end : str;
    }

    /**
     * Copies a string to the users clipboard
     * @param text The text to copy
     */
    public static void copyToClipboard(String text) {
        if (text == null) return;
        StringSelection stringSelection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    /**
     * Narrates a text using a FreeTTS narration model
      * @param text The text to narrate
     */
    public static void narrateText(String text) {
        // Find command based on os
        String osName = System.getProperty("os.name").toLowerCase();
        String[] command =
                // Windows
                osName.equals("win") ? new String[] { "PowerShell -Command \"Add-Type –AssemblyName System.Speech; "
                + "$speak = New-Object System.Speech.Synthesis.SpeechSynthesizer; "
                + "$speak.Speak('" + text + "');\"" }

                // MacOS
                : osName.equals("mac") ? new String[] { "say", text }

                // Linux
                : new String[] { "espeak", text };

        try {
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            App.notificationFromKey("danger", 100, "app.notifications.chat.error.tts_model");
            System.out.println("[ERROR]: Failed tts!");
        }
    }

    /**
     * Returns the current date
     */
    public static String date() { return date(System.currentTimeMillis()); }

    /**
     * Returns the current date
     * @param millis The date to display in milliseconds
     */
    public static String date(Long millis) { return new SimpleDateFormat(TextUtils.DATE_FORMAT).format(new Date(millis)); }
}
