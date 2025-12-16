package de.c4vxl.app.lib.component;

import de.c4vxl.app.theme.Theme;
import de.c4vxl.app.util.AnimationUtils;
import de.c4vxl.app.util.Factory;
import de.c4vxl.app.util.Resource;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;

public class NotificationPanel extends JPanel {
    private static HashMap<JPanel, Component> elements = new HashMap<>();

    public NotificationPanel() {
        this.setOpaque(false);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

    /**
     * Reload the layout
     */
    public void reload() {
        this.setPreferredSize(null);
        this.setSize(this.getWidth(), this.getPreferredSize().height);
        this.repaint();
        this.revalidate();
        this.getParent().setComponentZOrder(this, 0);
    }

    /**
     * Removes a message and it's gap
     * @param panel The messages panel
     */
    public void removeMessage(JPanel panel) {
        this.remove(panel);
        this.remove(elements.get(panel));
        this.reload();
    }

    /**
     * Create a notification
     * @param message The message to display
     * @param color The background color of the message box
     * @param time The duration the message should stay on screen
     */
    public NotificationPanel addMessage(String message, Color color, int time) {
        // Label
        JLabel label = new Factory<>(Elements.text("<p style='width: " + (getWidth() / 1.4) + "px; text-align: center; font-width: 200'>" + message + "</p>", -1))
                .pos(10, 10).get();

        // Panel
        JPanel panel = new Factory<>(new RoundedPanel(20))
                .background(color).layout(null).size(getWidth(), label.getHeight() + 20).opaque(true)
                .apply(p -> {
                    p.add(label); // Label

                    // Close button
                    p.add(new Factory<>(Elements.iconButton(Resource.loadIcon("media/cross.png", 15, Theme.current.text_1))).posX(getWidth() - 15 - 10).centerY(p)
                            .onClick(() -> removeMessage(p)).get());
                }).get();

        // Create gap
        Component gap = Box.createVerticalStrut(7);

        // Add elements
        elements.put(panel, gap);
        this.add(panel);
        this.add(gap);
        reload();

        // Animate
        time /= 10;
        int total = 255 + 255 + time; // 255 fade in; 255 fade out; time stay
        AnimationUtils.animate(panel, 10, (element, frame) -> {
            if (Arrays.stream(this.getComponents()).noneMatch(c -> c.equals(panel)) ||  // Panel removed
                    !((Window) this.getParent().getParent().getParent().getParent()).isActive() || // Window closed
                    frame == total + 1)                                                            // Animation over
                return true;


            if (frame <= 255) panel.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), frame));
            else if (frame >= total - 255) panel.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), total - frame));
            if (frame == total) removeMessage(panel);

            panel.repaint();

            return false;
        });

        return this;
    }
}
