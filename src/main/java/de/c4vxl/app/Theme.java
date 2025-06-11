package de.c4vxl.app;

import java.awt.*;

public class Theme {
    public final String name;
    public final Color accent, accent_1, accent_2, background, background_1, background_2, background_3, text, text_1;
    public final Font font, font_2;

    public Theme(String name, Font font, Font font_2, Color accent, Color accent_1, Color accent_2, Color background, Color background_1, Color background_2, Color background_3, Color text, Color text_1) {
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

        this.name = name;
    }

    public static Theme current;

    // standard theme (darkish)
    public static Theme dark = new Theme(
            "Dark",
            new Font("Inter", Font.PLAIN, 17),
            new Font("Roboto", Font.PLAIN, 17),
            new Color(0x6B6886),
            new Color(0x464451),
            new Color(0x605E6A),
            new Color(0x2A2828),
            new Color(0x413E3E),
            new Color(0x515050),
            new Color(0x333232),
            new Color(0xFFFFFF),
            new Color(0xD0D0D0)
    );

    // light theme
    public static Theme light = new Theme(
            "Light",
            new Font("Inter", Font.PLAIN, 17),
            new Font("Roboto", Font.PLAIN, 17),
            new Color(0x333333),
            new Color(0xFFFFFF),
            new Color(0xF5F5F5),
            new Color(0xE0E0E0),
            new Color(0xD6D6D6),
            new Color(0xCCCCCC),
            new Color(0xAAAAAA),
            new Color(0x6B6B6C),
            new Color(0x1C1C1E)
    );
}