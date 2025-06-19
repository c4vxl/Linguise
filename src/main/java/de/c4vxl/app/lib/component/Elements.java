package de.c4vxl.app.lib.component;

import de.c4vxl.app.Theme;
import de.c4vxl.app.util.Factory;

import javax.swing.*;
import java.awt.*;

public class Elements {
    /**
     * Creates a JLabel with a certain text with predefined styling
     * @param text The title
     * @param maxWidth The maximum width of the text
     */
    public static JLabel title(String text, int maxWidth) {
        JLabel label = text(text, maxWidth);
        label.setFont(Theme.current.font_2.deriveFont(Font.BOLD).deriveFont(25.0f));
        label.setSize(label.getPreferredSize());
        label.setBorder(BorderFactory.createEmptyBorder(5, 0, 20, 0));
        return label;
    }

    /**
     * Creates a JLabel with a certain text with predefined styling
     * @param text The text
     * @param maxWidth The maximum width of the text
     */
    public static JLabel text(String text, int maxWidth) {
        JLabel label = new JLabel("<html><div style='max-width: " + maxWidth + "px'>" + text + "</div></html>");
        label.setMaximumSize(new Dimension(maxWidth, 0));
        label.setForeground(Theme.current.text);
        label.setFont(Theme.current.font);
        label.setSize(label.getPreferredSize());
        return label;
    }

    /**
     * Creates a JLabel button with an icon
     * @param icon The icon
     */
    public static JLabel iconButton(ImageIcon icon) {
        JLabel label = new JLabel(icon) {
            @Override
            public JToolTip createToolTip() {
                Tooltip tip = new Tooltip(Theme.current.text, Theme.current.accent.darker());
                tip.setComponent(this);
                return tip;
            }
        };
        label.setSize(label.getPreferredSize());
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return label;
    }

    /**
     * Creates a border-only button with predefined styling
     */
    public static Button hollowButton() {
        return new Factory<>(new Button().borderStyle().withBorderRadius(10))
                .size(300, 50).foreground(Theme.current.accent).font(Theme.current.font.deriveFont(14f)).get();
    }
}