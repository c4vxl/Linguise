package de.c4vxl.app.lib.element.settings;

import de.c4vxl.Main;
import de.c4vxl.app.App;
import de.c4vxl.app.theme.Theme;
import de.c4vxl.app.config.Config;
import de.c4vxl.app.language.Language;
import de.c4vxl.app.lib.component.Elements;
import de.c4vxl.app.lib.component.RoundedPanel;
import de.c4vxl.app.util.Factory;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;

public class SettingsPageKeyboardShortcuts extends SettingsPage {
    public static HashMap<String, String> KEYBOARD_SHORTCUTS = new HashMap<>() {{
        put("overview", "ctrl + O");
        put("settings_toggle", "ctrl + K");
        put("settings_escape", "escape");
        put("switch_tab", "ctrl + $");
        put("chat_new", "ctrl + N");
        put("models_dropdown", "ctrl + M");
    }};

    /**
     * Returns a shortcut from the configuration
     * @param key The name of the shortcut
     */
    public static String getKeyboardShortcut(String key) {
        String shortcut = KEYBOARD_SHORTCUTS.getOrDefault(key, "");

        if (shortcut.equalsIgnoreCase("escape"))
            shortcut = shortcut.toUpperCase();

        shortcut = shortcut.replace(" + ", " ");
        shortcut = shortcut.replace("ctrl", "control");

        return shortcut;
    }

    @Override
    public void init() {
        // Title
        int maxTextWidth = this.getWidth() - 20;
        this.add(Elements.title(Language.current.get("app.settings.tabs.keyboard"), maxTextWidth), BorderLayout.PAGE_START);

        KEYBOARD_SHORTCUTS.forEach((k, v) -> {
            this.textPanel.add(createElement(k, v.replace("$", "[1, 2, 3, ...]")));
            this.textPanel.add(Box.createVerticalStrut(5));
        });

        this.buttonPanel.add(Elements.hollowButton().withLabel(Language.current.get("app.settings.keyboard.button.1")).withAction(e -> {
            try {
                Desktop.getDesktop().browse(Path.of(Config.CONFIG_FILE).toUri());
            } catch (IOException ex) {
                App.notificationFromKey("danger", 300, "app.notifications.global.error.file_open_fail", Path.of(Config.CONFIG_FILE).getFileName().toString());
                System.out.println("[ERROR]: Couldn't open config file!");
            }
        }));

        this.buttonPanel.add(Elements.hollowButton().withLabel(Language.current.get("app.settings.keyboard.button.2")).withAction(e -> {
            if (App.instance != null)
                App.instance.close();

            Main.start();
            App.instance.openSettings();
            App.instance.settings.openPage(5);
        }));
    }

    public JPanel createElement(String identifier, String value) {
        String key = Language.current.get("app.settings.keyboard." + identifier);

        JPanel label = new Factory<>(new RoundedPanel(15))
                .border(BorderFactory.createEmptyBorder(3, 10, -3, 10))
                .apply(panel -> panel.add(Elements.text(value, -1)))
                .toPreferredSize()
                .background(Theme.current.background_2)
                .get();

        return new Factory<>(new JPanel())
                .opaque(false)
                .layout(new BorderLayout())
                .apply(panel -> {
                    panel.add(Elements.text(key, -1), BorderLayout.WEST);
                    panel.add(label, BorderLayout.EAST);
                })
                .get();
    }
}