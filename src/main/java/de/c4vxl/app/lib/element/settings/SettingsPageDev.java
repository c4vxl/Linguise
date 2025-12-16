package de.c4vxl.app.lib.element.settings;

import de.c4vxl.app.App;
import de.c4vxl.app.theme.Theme;
import de.c4vxl.app.config.Config;
import de.c4vxl.app.language.Language;
import de.c4vxl.app.lib.component.Button;
import de.c4vxl.app.lib.component.Elements;
import de.c4vxl.app.model.Model;
import de.c4vxl.app.util.TextUtils;
import de.c4vxl.jNN;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

public class SettingsPageDev extends SettingsPage {
    public static boolean isDevMode;

    public Button devmodeButton = Elements.hollowButton().withAction((e) -> {
                SwingUtilities.getWindowAncestor(this).dispose();
                Config.setConfigValue("app.isdev", !isDevMode);
                isDevMode = !isDevMode;
                reloadDevModeButton();

                // Reload settings
                Config.setModel(
                        isDevMode && Model.current == null ? Model.getFakeModel(1) :            // if devmode and no model -> fake model
                                !isDevMode && Model.current == Model.getFakeModel(1) ? null :   // if not devmode and fake model -> no model
                                        Model.current                                                 // else keep model
                );

                // Reopen app
                App.reopen(Theme.current, Language.current);

                // Show notification
                App.notificationFromKey(
                        isDevMode ? "accent" : "danger",
                        300,
                        "app.notifications.devmode.info." + (isDevMode ? "enabled" : "disabled"));
            });

    @Override
    public void init() {
        int maxWidth = getWidth() - 200;
        // Title
        this.add(Elements.title(Language.current.get("app.settings.dev.title"), maxWidth), BorderLayout.NORTH);

        // Devmode button
        isDevMode = (boolean) Config.getOrSetConfigValue("app.isdev", false);
        this.textPanel.add(Elements.text(Language.current.get("app.settings.dev.devmode.text"), maxWidth));
        this.textPanel.add(Box.createVerticalStrut(5));
        this.textPanel.add(this.devmodeButton);
        reloadDevModeButton();

        buttonPanel.add(Elements.hollowButton()
                .withLabel(Language.current.get("app.settings.dev.button.open_data_dir"))
                .withAction(e -> {
                    System.out.println("[ACTION]: Opening data dir");
                    try { Desktop.getDesktop().open(new File(Config.APP_DIRECTORY)); }
                    catch (IOException ex) { throw new RuntimeException(ex); }
                }));

        // Only display in devmode
        if (isDevMode) {
            this.textPanel.add(Box.createVerticalStrut(30)); // Gap

            // Environment variables
            this.textPanel.add(Elements.title(Language.current.get("app.settings.dev.env_title"), maxWidth)); // title
            this.textPanel.add(Elements.text(Language.current.get("app.settings.dev.env.models_dir", Config.MODELS_DIRECTORY), maxWidth));
            this.textPanel.add(Elements.text(Language.current.get("app.settings.dev.env.models_ext", Config.MODEL_FILE_EXTENSION), maxWidth));
            this.textPanel.add(Elements.text(Language.current.get("app.settings.dev.env.themes_dir", Config.THEMES_DIRECTORY), maxWidth));
            this.textPanel.add(Elements.text(Language.current.get("app.settings.dev.env.themes_ext", Config.THEME_FILE_EXTENSION), maxWidth));
            this.textPanel.add(Elements.text(Language.current.get("app.settings.dev.env.langs_dir", Config.LANGS_DIRECTORY), maxWidth));
            this.textPanel.add(Elements.text(Language.current.get("app.settings.dev.env.langs_ext", Config.LANG_FILE_EXTENSION), maxWidth));
            this.textPanel.add(Elements.text(Language.current.get("app.settings.dev.env.chats_dir", Config.HISTORIES_DIRECTORY), maxWidth));
            this.textPanel.add(Elements.text(Language.current.get("app.settings.dev.env.chats_ext", Config.HISTORY_FILE_EXTENSION), maxWidth));
            this.textPanel.add(Elements.text(Language.current.get("app.settings.dev.env.date_format", TextUtils.DATE_FORMAT), maxWidth));
            this.textPanel.add(Elements.text(Language.current.get("app.settings.dev.env.matmul_type", String.valueOf(jNN.MATMUL_TYPE)), maxWidth));
            this.textPanel.add(Box.createVerticalStrut(10));

            this.buttonPanel.add(Elements.hollowButton().withLabel(Language.current.get("app.settings.dev.button.edit_config")).withAction(e -> {
                try {
                    Desktop.getDesktop().browse(Path.of(Config.CONFIG_FILE).toUri());
                } catch (IOException ex) {
                    App.notificationFromKey("danger", 300, "app.notifications.global.error.file_open_fail", Path.of(Config.CONFIG_FILE).getFileName().toString());
                    System.out.println("[ERROR]: Couldn't open config file!");
                }
            }));
        }
    }

    private void reloadDevModeButton() {
        devmodeButton = this.devmodeButton
                .withLabel(Language.current.get("app.settings.dev.devmode." + (isDevMode ? "enabled" : "disabled") + ".button"))
                .borderStyle(isDevMode ? -1 : 1);

        devmodeButton.background = isDevMode ? Theme.current.danger : Theme.current.accent;
        devmodeButton.hovered = isDevMode ? Theme.current.danger.brighter() : Theme.current.accent_1;
        devmodeButton.pressed = isDevMode ? Theme.current.danger.darker() : Theme.current.accent_2;
        devmodeButton.setForeground(Theme.current.text);

        devmodeButton.repaint();
        devmodeButton.revalidate();
    }

    @Override
    public Integer calculateTextPanelHeight(JPanel panel) {
        return Arrays.stream(panel.getComponents()).map(x -> x.getHeight() + 5).reduce(Integer::sum).orElse(0);
    }
}