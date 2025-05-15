package de.c4vxl.app;

import java.awt.*;

public class Theme {
    public final Color accent, accent_1, accent_2, background, background_1, background_2, background_3, text, text_1;

    public Theme(Color accent, Color accent_1, Color accent_2, Color background, Color background_1, Color background_2, Color background_3, Color text, Color text_1) {
        this.accent = accent;
        this.accent_1 = accent_1;
        this.accent_2 = accent_2;
        this.background = background;
        this.background_1 = background_1;
        this.background_2 = background_2;
        this.background_3 = background_3;
        this.text = text;
        this.text_1 = text_1;
    }

    public static Theme current;

    // standard theme (darkish)
    public static Theme standard = new Theme(
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
}