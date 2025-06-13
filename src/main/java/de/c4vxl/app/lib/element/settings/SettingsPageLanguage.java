package de.c4vxl.app.lib.element.settings;

import de.c4vxl.app.Theme;
import de.c4vxl.app.lib.component.RoundedPanel;
import de.c4vxl.app.lib.component.ScrollPane;
import de.c4vxl.app.lib.component.Elements;
import de.c4vxl.app.util.Factory;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SettingsPageLanguage extends SettingsPage {
    public ArrayList<String> languages = new ArrayList<>(List.of(new String[]{
            "English", "German", "French"
    }));

    @Override
    public void init() {
        // Title
        this.add(Elements.title("Language", this.getWidth() - 200), BorderLayout.NORTH);

        // Buttons
        // TODO: Implement language loading
        buttonPanel.add(Elements.hollowButton()
                .withLabel("Load custom language")
                .withAction(e -> System.out.println("Loading custom language...")));

        // TODO: Implement redirect
        buttonPanel.add(Elements.hollowButton()
                .withLabel("Contribute")
                .withAction(e -> System.out.println("Redirecting to contribution...")));

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

        this.repaint();
        this.revalidate();
    }

    /**
     * Creates the element/button for a language
     * @param name The name of the theme
     * @param isHighlighted Is the element highlighted/is it the currently selected language
     */
    private JPanel createEntry(String name, boolean isHighlighted) {
        JPanel panel = new Factory<>(new RoundedPanel(10)).layout(null).size(230, 60).opaque(false).cursor(Cursor.HAND_CURSOR)
                .hoverAnimation(Theme.current.background_3, Theme.current.background_2, isHighlighted)
                .get();

        // Label
        panel.add(new Factory<>(Elements.text(name, panel.getWidth())).center(panel).get());

        return panel;
    }
}