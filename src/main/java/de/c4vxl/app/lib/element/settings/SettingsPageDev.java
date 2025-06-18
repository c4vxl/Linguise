package de.c4vxl.app.lib.element.settings;

import de.c4vxl.app.App;
import de.c4vxl.app.Theme;
import de.c4vxl.app.config.Config;
import de.c4vxl.app.language.Language;
import de.c4vxl.app.lib.component.Button;
import de.c4vxl.app.lib.component.Elements;
import de.c4vxl.app.model.Model;

import javax.swing.*;
import java.awt.*;

public class SettingsPageDev extends SettingsPage {
    public static boolean isDevMode;

    public Button devmodeButton = Elements.hollowButton().withAction((e) -> {
                SwingUtilities.getWindowAncestor(this).dispose();
                Config.setConfigValue("app.isdev", !isDevMode);
                isDevMode = !isDevMode;
                reloadDevModeButton();

                Model.current = isDevMode && Model.current == null ? Model.getFakeModel(1) : // if devmode and no model -> fake model
                        !isDevMode && Model.current == Model.getFakeModel(1) ? null :        // if not devmode and fake model -> no model
                        Model.current;                                                             // else keep model

                // Save to config
                Config.setConfigValue("app.model", Model.current != null ? Model.current.path.replace(Config.MODELS_DIRECTORY + "/", "") : null);

                new App(Theme.current, Language.current).open();

                App.notificationFromKey(
                        isDevMode ? "accent" : "danger",
                        300,
                        "app.notifications.devmode.info." + (isDevMode ? "enabled" : "disabled")
                );
            });

    @Override
    public void init() {
        // Title
        this.add(Elements.title(Language.current.get("app.settings.dev.title"), this.getWidth() - 200), BorderLayout.NORTH);

        this.textPanel.add(Elements.text(Language.current.get("app.settings.dev.devmode.text"), this.getWidth() - 200));
        this.textPanel.add(Box.createVerticalStrut(5));

        isDevMode = (boolean) Config.getOrSetConfigValue("app.isdev", false);
        this.textPanel.add(this.devmodeButton);
        reloadDevModeButton();
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
}
