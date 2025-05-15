package de.c4vxl.app.lib.component;

import javax.swing.*;
import java.awt.*;

public class Tooltip extends JToolTip {
    public Tooltip(Color foreground, Color background) {
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        setForeground(foreground);
        setBackground(background);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

        g2.dispose();

        super.paintComponent(g);
    }
}
