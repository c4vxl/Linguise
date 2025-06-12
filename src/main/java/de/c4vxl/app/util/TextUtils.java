package de.c4vxl.app.util;

import java.awt.*;

public class TextUtils {
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
}
