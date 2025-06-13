package de.c4vxl.app.lib.element.settings;

import de.c4vxl.app.Theme;
import de.c4vxl.app.lib.component.Elements;
import de.c4vxl.app.lib.component.RoundedPanel;
import de.c4vxl.app.util.Factory;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SettingsPageModels extends SettingsPage {
    @Override
    public void init() {
        // Title
        this.add(Elements.title("Models", this.getWidth() - 200), BorderLayout.NORTH);

        // Buttons
        // TODO: Implement model loading
        buttonPanel.add(Elements.hollowButton()
                .withLabel("Load model from file")
                .withAction(e -> System.out.println("Loading custom model [file]...")));
        buttonPanel.add(Elements.hollowButton()
                .withLabel("Load model from url")
                .withAction(e -> System.out.println("Loading custom model [url]...")));

        reload();
    }

    /**
     * Reloads all themes
     */
    public void reload() {
        // Add entries
        this.panel.removeAll();

        ArrayList<String> models = new ArrayList<>(List.of(new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"}));

        for (int i = 0; i < models.size(); i++) {
            this.panel.add(createEntry(models.get(i), i % 2 == 0));
        }

        this.repaint();
        this.revalidate();
    }

    /**
     * Creates the element/button for a model
     * @param name The name of the model
     */
    private JPanel createEntry(String name, boolean isOdd) {
        JPanel panel = new Factory<>(new RoundedPanel(10)).layout(null).size(getWidth() - 90, 60)
                .opaque(false).cursor(Cursor.HAND_CURSOR)
                .hoverAnimation(isOdd ? Theme.current.background : Theme.current.background.darker(), Theme.current.background_2, false)
                .get();

        // Label
        panel.add(new Factory<>(Elements.text(name, panel.getWidth())).posY(20).centerX(panel).get());

        return panel;
    }
}
