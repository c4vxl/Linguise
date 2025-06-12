package de.c4vxl.app.util;

import javax.swing.*;
import java.awt.*;

public class ModalUtils {
    /**
     * Creates a dark semi-transparent overlay on the frame passed
     * @param frame The frame
     */
    public static JPanel createModalBackground(JFrame frame) {
        JPanel panel = new JPanel();
        panel.setOpaque(true);
        panel.setBackground(new Color(0, 0, 0, 50));
        panel.setLayout(null);
        frame.remove(frame.getGlassPane());
        frame.setGlassPane(panel);
        frame.getGlassPane().setVisible(true);
        return panel;
    }
}