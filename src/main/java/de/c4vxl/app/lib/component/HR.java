package de.c4vxl.app.lib.component;

import javax.swing.*;
import java.awt.*;

public class HR extends JPanel {
    public HR(int width, int height, Color background) {
        this.setSize(width, height);
        this.setPreferredSize(this.getSize());
        this.setBackground(background);
    }

    /**
     * Set the location of the element
     * @param x The x coordinate
     * @param y The y coordinate
     */
    public HR position(int x, int y) {
        this.setLocation(x, y);
        return this;
    }
}
