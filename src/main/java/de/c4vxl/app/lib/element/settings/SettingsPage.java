package de.c4vxl.app.lib.element.settings;

import de.c4vxl.app.lib.component.ScrollPane;
import de.c4vxl.app.util.Factory;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public abstract class SettingsPage extends JPanel {
    public JPanel buttonPanel = new Factory<>(new JPanel(new FlowLayout(FlowLayout.LEFT))).opaque(false).get();
    public JPanel panel = new Factory<>(new JPanel() {
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(this.getParent().getParent().getParent().getWidth() - 70,
                    Arrays.stream(this.getComponents()).map(x -> Math.max(x.getHeight(), x.getPreferredSize().height) + 5).reduce(Integer::sum).orElse(0));
        }
    }).opaque(false).get();
    public ScrollPane pane = new ScrollPane(this.panel);

    public JPanel textPanel = new Factory<>(new JPanel() {
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(this.getParent().getParent().getParent().getWidth() - 70,
                    Arrays.stream(this.getComponents()).map(x -> x.getHeight() + 5).reduce(Integer::sum).orElse(0));
        }
    }).opaque(false).apply(x -> x.setLayout(new BoxLayout(x, BoxLayout.Y_AXIS))).get();

    public SettingsPage() {
        this.setBorder(BorderFactory.createEmptyBorder(30, 20, 10, 20));
        this.setLayout(new BorderLayout());
        this.setOpaque(false);
    }

    public void _init() {
        this.removeAll();
        this.panel.removeAll();
        this.buttonPanel.removeAll();
        this.textPanel.removeAll();

        this.add(this.pane, BorderLayout.CENTER);
        this.panel.add(this.textPanel);
        this.add(buttonPanel, BorderLayout.PAGE_END);

        this.init();

        this.textPanel.add(Box.createVerticalGlue());
    }

    public abstract void init();
}