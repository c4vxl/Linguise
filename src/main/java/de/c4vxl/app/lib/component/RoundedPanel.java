package de.c4vxl.app.lib.component;

import javax.swing.*;
import java.awt.*;

/**
 * A rounded version of the JPanel
 */
public class RoundedPanel extends JPanel {
    private int arcw, arch;

    public RoundedPanel(int radius) { this(radius, radius); }
    public RoundedPanel(int arcw, int arch) {
        this.arcw = arcw;
        this.arch = arch;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arcw, arch);

        g2.dispose();
    }
}
