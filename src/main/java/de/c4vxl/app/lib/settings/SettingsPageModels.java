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

public class SettingsPageModels extends SettingsPage {
    private JPanel panel = new JPanel(new GridLayout(0, 1, 0, 5));
    private ScrollPane pane = new ScrollPane(this.panel);

    @Override
    public void init() {
        this.setLayout(new BorderLayout());

        this.add(Elements.title("Models", this.getWidth() - 200), BorderLayout.NORTH);
        this.add(this.pane, BorderLayout.CENTER);
        this.panel.setOpaque(false);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setOpaque(false);

        // TODO: Implement model loading
        buttonPanel.add(Elements.hollowButton()
                .withLabel("Load model from file")
                .withAction(e -> System.out.println("Loading custom model [file]...")));
        buttonPanel.add(Elements.hollowButton()
                .withLabel("Load model from url")
                .withAction(e -> System.out.println("Loading custom model [url]...")));

        this.add(buttonPanel, BorderLayout.PAGE_END);

        reload();
    }

    /**
     * Reloads all themes
     */
    public void reload() {
        // Add "normal" entries
        this.panel.removeAll();

        ArrayList<String> models = new ArrayList<>(List.of(new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"}));

        for (int i = 0; i < models.size(); i++) {
            this.panel.add(createEntry(models.get(i), i % 2 == 0));
        }

        // Create "fake" entries so that the GridLayout won't mess up the sizing of the actual entries
        int missing = 7 - models.size();
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
     * Creates the element/button for a model
     * @param name The name of the model
     */
    private JPanel createEntry(String name, boolean isOdd) {
        JPanel panel = new RoundedPanel(10);
        panel.setLayout(null);
        panel.setSize(getWidth() - 90, 60);
        panel.setPreferredSize(panel.getSize());
        panel.setOpaque(false);
        panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));


        Color color = isOdd ? Theme.current.background : Theme.current.background.darker();
        panel.setBackground(color);
        panel.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { panel.setBackground(Theme.current.background_2); }
            @Override public void mouseExited(MouseEvent e) { panel.setBackground(color); }
        });

        JLabel label = Elements.text(name, panel.getWidth());
        label.setSize(label.getPreferredSize());
        label.setLocation((panel.getWidth() - label.getWidth()) / 2, 20);
        panel.add(label);

        return panel;
    }
}
