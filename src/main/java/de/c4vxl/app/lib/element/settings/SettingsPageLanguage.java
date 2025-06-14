package de.c4vxl.app.lib.element.settings;

import de.c4vxl.app.App;
import de.c4vxl.app.Theme;
import de.c4vxl.app.config.Config;
import de.c4vxl.app.language.Language;
import de.c4vxl.app.lib.component.RoundedPanel;
import de.c4vxl.app.lib.component.ScrollPane;
import de.c4vxl.app.lib.component.Elements;
import de.c4vxl.app.util.Factory;
import de.c4vxl.app.util.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class SettingsPageLanguage extends SettingsPage {
    @SuppressWarnings("CallToPrintStackTrace")
    @Override
    public void init() {
        // Title
        this.add(Elements.title(Language.current.get("app.settings.language.title"), this.getWidth() - 200), BorderLayout.NORTH);

        // Buttons
        buttonPanel.add(Elements.hollowButton()
                .withLabel(Language.current.get("app.settings.language.button.1"))
                .withAction(e -> {
                    System.out.println("[ACTION]: Loading custom language");
                    File file = FileUtils.openFileDialog("user.downloads", "Linguise language files", new String[]{Config.LANG_FILE_EXTENSION});
                    if (file == null) return;

                    try {
                        Files.copy(file.toPath(), Path.of(Config.LANGS_DIRECTORY + "/" + file.getName()));
                    } catch (IOException ex) {
                        System.out.println("[ERROR]: Couldn't copy theme file!");
                        ex.printStackTrace();
                    }
                    reload();
                }));

        // TODO: Implement redirect
        buttonPanel.add(Elements.hollowButton()
                .withLabel(Language.current.get("app.settings.language.button.2"))
                .withAction(e -> System.out.println("Redirecting to contribution...")));

        reload();
    }

    /**
     * Reloads all themes
     */
    public void reload() {
        // Add "normal" entries
        this.panel.removeAll();
        for (Language language : Config.getLocalLangs()) {
            this.panel.add(createEntry(language.name, Language.current.name.equals(language.name), () -> {
                if (Language.current.name.equals(language.name)) return;
                SwingUtilities.getWindowAncestor(this).dispose();
                new App(Theme.current, language).open();
                Config.setConfigValue("app.lang", language.file.getName());
            }));
        }

        this.repaint();
        this.revalidate();
    }

    /**
     * Creates the element/button for a language
     * @param name The name of the theme
     * @param isHighlighted Is the element highlighted/is it the currently selected language
     */
    private JPanel createEntry(String name, boolean isHighlighted, Runnable onClick) {
        JPanel panel = new Factory<>(new RoundedPanel(10)).layout(null).size(230, 60).opaque(false).cursor(Cursor.HAND_CURSOR)
                .hoverAnimation(isHighlighted ? Theme.current.background_2 : Theme.current.background_3, Theme.current.background_2, false)
                .onClick(onClick)
                .get();

        // Label
        panel.add(new Factory<>(Elements.text(name, panel.getWidth())).center(panel).get());

        return panel;
    }
}