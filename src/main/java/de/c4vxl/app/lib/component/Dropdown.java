package de.c4vxl.app.lib.component;

import de.c4vxl.app.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Consumer;

public class Dropdown extends RoundedPanel {
    public JLabel label;

    public JPanel elementsPanel = new JPanel();
    public ScrollPane elementsPanelWrapper = new ScrollPane(elementsPanel);

    public ArrayList<Component> elements = new ArrayList<>();

    public int padding = 10;
    public int elementHeight = 50;
    public int gap = 10;

    public int elementWidth;

    public boolean isExpanded;

    public Dropdown(String label) { this(null, label); }
    public Dropdown(Icon icon) { this(icon, null); }

    public Dropdown(Icon icon, String label) {
        super(10);

        this.setLayout(new GridLayout());
        this.setBackground(Theme.current.background_1);
        this.setSize(350, 50);
        this.setForeground(Theme.current.text);

        this.label = new JLabel(label, icon, JLabel.CENTER);
        this.label.setSize(this.label.getPreferredSize());
        this.label.setForeground(this.getForeground());
        this.add(this.label);

        elementWidth = getWidth() - padding * 2;

        elementsPanel.setLayout(new BoxLayout(elementsPanel, BoxLayout.Y_AXIS));
        elementsPanel.setOpaque(false);
        elementsPanelWrapper.setMaximumSize(new Dimension(elementWidth, 5000));

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isExpanded)
                    collapse();
                else
                    expand();
            }
        });
    }

    public Dropdown addOption(String label, Icon icon, Consumer<String> onClick) {
        RoundedPanel panel = new RoundedPanel(10);
        panel.setLayout(new GridLayout());
        // panel.setBackground(Theme.current.background_1);
        panel.setPreferredSize(new Dimension(elementWidth - 30, elementHeight));
        panel.addMouseListener(new MouseAdapter() { @Override public void mouseClicked(MouseEvent e) { onClick.accept(label); } });

        JLabel text = new JLabel(label, icon, JLabel.CENTER);
        text.setSize(text.getPreferredSize());
        text.setForeground(this.getForeground());
        panel.add(text);

        elements.add(panel);

        return this;
    }

    public Dropdown collapse() {
        if (!isExpanded) return this;
        isExpanded = false;

        this.elementsPanel.removeAll();
        this.remove(this.elementsPanelWrapper);
        this.setLayout(new GridLayout());
        this.setSize(this.getWidth(), 50);

        SwingUtilities.invokeLater(() -> {
            this.repaint();
            this.revalidate();
        });

        return this;
    }

    public Dropdown expand() {
        if (isExpanded) return this;
        isExpanded = true;

        this.setLayout(null);

        this.elementsPanel.removeAll();
        for (int i = 0; i < elements.size(); i++) {
            this.elementsPanel.add(elements.get(i));
            if (i != elements.size() - 1)
                this.elementsPanel.add(Box.createVerticalStrut(gap));
        }

        elementsPanel.setBounds(padding, 50, elementWidth, elements.size() * elementHeight + gap * elements.size());
        elementsPanelWrapper.setBounds(elementsPanel.getBounds());

        this.setSize(this.getWidth(), elementsPanel.getHeight() + 70);

        this.add(elementsPanelWrapper);

        SwingUtilities.invokeLater(() -> {
            elementsPanelWrapper.scrollToTop();
            this.repaint();
            this.revalidate();
        });

        return this;
    }
}
