package de.c4vxl.app.lib.settings;

import de.c4vxl.app.Theme;
import de.c4vxl.app.lib.component.RoundedPanel;
import de.c4vxl.app.lib.component.ScrollPane;
import de.c4vxl.app.util.Elements;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class SettingsPageLanguage extends SettingsPage {
    private JPanel panel = new JPanel(new GridLayout(0, 3, 10, 20));
    private ScrollPane pane = new ScrollPane(this.panel);

    public ArrayList<String> languages = new ArrayList<>(List.of(new String[]{
            "English", "German", "French"
    }));

    @Override
    public void init() {
        this.setLayout(new BorderLayout());

        this.add(Elements.title("Language", this.getWidth() - 200), BorderLayout.NORTH);
        this.add(this.pane, BorderLayout.CENTER);
        this.panel.setOpaque(false);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setOpaque(false);

        // TODO: Implement language loading
        buttonPanel.add(Elements.hollowButton()
                .withLabel("Load custom language")
                .withAction(e -> System.out.println("Loading custom language...")));

        // TODO: Implement redirect
        buttonPanel.add(Elements.hollowButton()
                .withLabel("Contribute")
                .withAction(e -> System.out.println("Redirecting to contribution...")));
        this.add(buttonPanel, BorderLayout.PAGE_END);

        reload();
    }

    /**
     * Reloads all themes
     */
    public void reload() {
        // Add "normal" entries
        this.panel.removeAll();
        for (String language : languages) {
            this.panel.add(createEntry(language, false));
        }

        // Create "fake" entries so that the GridLayout won't mess up the sizing of the actual entries
        int missing = 7 * 3 - languages.size();
        if (missing > 0) {
            for (int i = 0; i < missing; i++) {
                JPanel p = createEntry("Mystery", false);
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
     * Creates the element/button for a language
     * @param name The name of the theme
     * @param isHighlighted Is the element highlighted/is it the currently selected language
     */
    private JPanel createEntry(String name, boolean isHighlighted) {
        JPanel panel = new RoundedPanel(10);
        panel.setLayout(null);
        panel.setSize(230, 60);
        panel.setPreferredSize(panel.getSize());
        panel.setOpaque(false);
        panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        Color color = isHighlighted ? Theme.current.background_2 : Theme.current.background_3;
        panel.setBackground(color);
        panel.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { panel.setBackground(Theme.current.background_2); }
            @Override public void mouseExited(MouseEvent e) { panel.setBackground(color); }
        });

        JLabel label = Elements.text(name, panel.getWidth());
        label.setSize(label.getPreferredSize());
        label.setLocation((panel.getWidth() - label.getWidth()) / 2, (panel.getHeight() - label.getHeight()) / 2);
        panel.add(label);

        return panel;
    }
}