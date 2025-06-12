package de.c4vxl.app.lib.element.settings;

import de.c4vxl.app.App;
import de.c4vxl.app.Theme;
import de.c4vxl.app.lib.component.RoundedPanel;
import de.c4vxl.app.lib.component.ScrollPane;
import de.c4vxl.app.util.Elements;
import de.c4vxl.app.util.Factory;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class SettingsPageTheme extends SettingsPage {
    private JPanel panel = new JPanel(new GridLayout(0, 5, 10, 20));
    private ScrollPane pane = new ScrollPane(this.panel);

    public ArrayList<Theme> themes = new ArrayList<>(){{
        add(Theme.dark);
        add(Theme.light);
    }};


    @Override
    public void init() {
        this.setLayout(new BorderLayout());

        this.add(Elements.title("Themes", this.getWidth() - 200), BorderLayout.NORTH);
        this.add(this.pane, BorderLayout.CENTER);


        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setOpaque(false);

        // TODO: Implement theme loading
        buttonPanel.add(Elements.hollowButton()
                .withLabel("Load custom theme")
                .withAction(e -> System.out.println("Loading custom theme...")));
        this.add(buttonPanel, BorderLayout.PAGE_END);

        this.panel.setOpaque(false);

        this.reload();
    }

    /**
     * Reloads all themes
     */
    public void reload() {
        // Add "normal" entries
        this.panel.removeAll();
        for (Theme theme : themes) {
            this.panel.add(createEntry(theme.name, theme.background_1, theme.background_3, theme == Theme.current, () -> {
                SwingUtilities.getWindowAncestor(this).dispose();
                new App(theme).open();
            }));
        }

        // Create "fake" entries so that the GridLayout won't mess up the sizing of the actual entries
        int missing = 3 * 5 - themes.size();
        if (missing > 0) {
            for (int i = 0; i < missing; i++) {
                JPanel p = createEntry("Mystery", Color.BLUE, Color.CYAN, false, () -> {});
                p.setVisible(false);
                this.panel.add(p);
            }

            this.pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
            this.pane.getVerticalScrollBar().setEnabled(false);
            this.pane.getVerticalScrollBar().setUnitIncrement(0);
        }

        this.repaint();
        this.revalidate();
    }

    /**
     * Creates the element/button for a theme
     * @param name The name of the theme
     * @param c1 The first color of the theme
     * @param c2 The last color of the theme
     * @param isHighlighted Is the element highlighted/is it the currently selected theme
     */
    private JPanel createEntry(String name, Color c1, Color c2, boolean isHighlighted, Runnable onClick) {
        JPanel panel = new Factory<>(new RoundedPanel(10)).layout(null).size(130, 190).opaque(false).cursor(Cursor.HAND_CURSOR)
                .hoverAnimation(isHighlighted ? Theme.current.background_2 : Theme.current.background_1, Theme.current.background_2, false)
                .onClick(onClick).get();

        // Label
        panel.add(new Factory<>(Elements.text(name, panel.getWidth())).posY(20).centerX(panel).get());

        // Colors preview
        panel.add(new Factory<>(new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int size = getWidth();

                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setPaint(new GradientPaint(0, 0, c1, size, size, c2));
                g2d.fillOval((getWidth() - size) / 2, (getHeight() - size) / 2, size, size);
                g2d.dispose();
            }
        }).size(100, 100).opaque(false).posY(70).centerX(panel).get());

        return panel;
    }
}