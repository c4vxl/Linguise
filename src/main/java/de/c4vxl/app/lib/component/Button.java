package de.c4vxl.app.lib.component;

import de.c4vxl.app.theme.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class Button extends JButton {
    public int arcw, arch;
    public Integer borderWidth = null;
    public Color background, pressed, hovered, foreground;
    public boolean hover;

    public Button() { this(Theme.current.accent, Theme.current.accent_2, Theme.current.accent_1, Theme.current.text); }
    public Button(Color background, Color foreground) { this(background, background.darker(), background.brighter(), foreground); }
    public Button(Color background, Color pressed, Color hovered, Color foreground) {
        this.background = background;
        this.pressed = pressed;
        this.hovered = hovered;
        this.foreground = foreground;

        this.setForeground(foreground);
        this.setBackground(background);
        this.setContentAreaFilled(false);
        this.setFocusPainted(false);
        this.setBorderPainted(false);
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hover = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hover = false;
                repaint();
            }
        });
    }

    /**
     * Add a label to the button
     * @param label The label
     */
    public Button withLabel(String label) {
        this.setText(label);
        return this;
    }

    /**
     * Add an icon to the button
     * @param icon The icon
     */
    public Button withIcon(ImageIcon icon) {
        this.setIcon(icon);
        return this;
    }

    /**
     * Add a gap between icon and label
     * @param gap The gap
     */
    public Button withIconTextGap(int gap) {
        this.setIconTextGap(gap);
        return this;
    }

    /**
     * Add a border radius
     * @param arcw Arch width
     * @param arch Arch height
     */
    public Button withBorderRadius(int arcw, int arch) {
        this.arcw = arcw;
        this.arch = arch;
        return this;
    }

    /**
     * Add a border radius
     * @param borderRadius The border radius
     */
    public Button withBorderRadius(int borderRadius) {
        return this.withBorderRadius(borderRadius, borderRadius);
    }

    /**
     * Add a click action listener
     * @param onClick The action
     */
    public Button withAction(Consumer<ActionEvent> onClick) {
        this.addActionListener(onClick::accept);
        return this;
    }

    /**
     * Set the style to border-only
     * @param width Set the width of the border
     */
    public Button borderStyle(int width) {
        borderWidth = width;
        return this;
    }

    /**
     * Set the style to border-only with a width of 2px
     */
    public Button borderStyle() {
        return this.borderStyle(2);
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (getModel().isPressed()) {
            g2.setColor(pressed);
        } else if (hover) {
            g2.setColor(hovered);
        } else {
            g2.setColor(background);
        }

        if (borderWidth == null || borderWidth < 0)
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arcw, arch);
        else {
            g2.setStroke(new BasicStroke(borderWidth));
            int offset = borderWidth / 2;
            g2.drawRoundRect(offset, offset, getWidth() - borderWidth, getHeight() - borderWidth, arcw, arch);
        }

        g2.dispose();

        super.paint(g);
    }
}