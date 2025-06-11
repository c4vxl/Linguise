package de.c4vxl.app.util;

import de.c4vxl.app.Theme;
import de.c4vxl.app.lib.component.Button;

import javax.swing.*;
import java.awt.*;

public class Elements {
    /**
     * Creates a JLabel with a certain text with predefined styling
     * @param text The title
     */
    public static JLabel title(String text, int maxWidth) {
        JLabel label = text(text, maxWidth);
        label.setFont(Theme.current.font.deriveFont(Font.BOLD).deriveFont(25.0f));
        label.setSize(label.getPreferredSize());
        label.setBorder(BorderFactory.createEmptyBorder(5, 0, 20, 0));
        return label;
    }

    /**
     * Creates a JLabel with a certain text with predefined styling
     * @param text The text
     */
    public static JLabel text(String text, int maxWidth) {
        JLabel label = new JLabel("<html><div style='max-width: " + maxWidth + "px'>" + text + "</div></html>");
        label.setMaximumSize(new Dimension(maxWidth, 0));
        label.setForeground(Theme.current.text);
        label.setSize(label.getPreferredSize());
        label.setFont(Theme.current.font);
        return label;
    }

    /**
     * Creates a border-only button with predefined styling
     */
    public static Button hollowButton() {
        return new Button()
                .size(300, 50)
                .foreground(Theme.current.accent)
                .borderStyle().withBorderRadius(10)
                .font(Theme.current.font.deriveFont(14f));
    }
}
