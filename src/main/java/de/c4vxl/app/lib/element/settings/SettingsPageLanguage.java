package de.c4vxl.app.lib.element.settings;

import de.c4vxl.app.App;
import de.c4vxl.app.Theme;
import de.c4vxl.app.config.Config;
import de.c4vxl.app.language.Language;
import de.c4vxl.app.lib.component.Elements;
import de.c4vxl.app.lib.component.RoundedPanel;
import de.c4vxl.app.model.Model;
import de.c4vxl.app.util.Factory;
import de.c4vxl.app.util.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class SettingsPageLanguage extends SettingsPage {
    @Override
    public void init() {
        // Title
        this.add(Elements.title(Language.current.get("app.settings.language.title"), this.getWidth() - 200), BorderLayout.NORTH);

        // Buttons
        buttonPanel.add(Elements.hollowButton()
                .withLabel(Language.current.get("app.settings.language.button.1"))
                .withAction(e -> {
                    System.out.println("[ACTION]: Loading custom language");
                    File file = FileUtils.openFileDialog("user.home", "Linguise language files", new String[]{Config.LANG_FILE_EXTENSION});
                    if (file == null) return;

                    try {
                        Files.copy(file.toPath(), Path.of(Config.LANGS_DIRECTORY + "/" + file.getName()));
                    } catch (IOException ex) {
                        App.notificationFromKey("danger", 300, "app.notifications.global.error.copy_failed", file.getAbsolutePath());
                        System.out.println("[ERROR]: Couldn't copy theme file!");
                    }
                    reload();
                }));

        buttonPanel.add(Elements.hollowButton()
                .withLabel(Language.current.get("app.settings.language.button.2"))
                .withAction(e -> {
                    System.out.println("[ACTION]: Reloading languages list");
                    reload();
                }));

        reload();
    }

    @Override
    public Integer calculatePanelHeight(JPanel panel) {
        return Arrays.stream(panel.getComponents()).map(x -> Math.max(x.getHeight(), x.getPreferredSize().height) + 5).reduce(Integer::sum).orElse(0) / 3;
    }

    /**
     * Reloads all themes
     */
    public void reload() {
        // Add "normal" entries
        this.panel.removeAll();
        System.out.println("[UPDATE]: Reloading SettingsPageLanguage");
        Language[] languages = Config.getLocalLangs();
        System.out.println("[UPDATE]: Found languages: " + String.join(", ", Arrays.stream(languages).map(x -> x.name).toArray(String[]::new)));

        for (Language language : languages) {
            this.panel.add(createEntry(language.name, Language.current.name.equals(language.name), () -> {
                if (Language.current.name.equals(language.name)) return;
                Config.setLanguage(language);
                App.reopen(Theme.current, language);
                App.notificationFromKey("accent", 300, "app.notifications.language.info.switched", language.name);
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