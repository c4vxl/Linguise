package de.c4vxl.app.lib.element.settings;

import de.c4vxl.app.App;
import de.c4vxl.app.config.Config;
import de.c4vxl.app.language.Language;
import de.c4vxl.app.lib.component.Elements;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class SettingsPageAbout extends SettingsPage {
    @Override
    public void init() {
        // Title
        int maxTextWidth = this.getWidth() - 20;
        this.add(Elements.title(Language.current.get("app.settings.about.title"), maxTextWidth), BorderLayout.PAGE_START);

        // Content
        this.textPanel.add(Elements.text("<p style='text-align: justify'>" + Language.current.get("app.settings.about.1") + "</p>", maxTextWidth));
        this.textPanel.add(Box.createVerticalStrut(10));
        this.textPanel.add(Elements.text("<p style='text-align: justify'>" + Language.current.get("app.settings.about.2") + "</p>", maxTextWidth));
        this.textPanel.add(Box.createVerticalStrut(10));
        this.textPanel.add(Elements.text("<p style='text-align: justify'>" + Language.current.get("app.settings.about.3") + "</p>", maxTextWidth));

        // Buttons
        buttonPanel.add(Elements.hollowButton()
                .withLabel(Language.current.get("app.settings.about.button.1"))
                .withAction(e -> {
                    System.out.println("[ACTION]: Opening GitHub");
                    try { Desktop.getDesktop().browse(new URI(Config.GITHUB_URL)); }
                    catch (IOException | URISyntaxException ex) { throw new RuntimeException(ex); }
                }));

        buttonPanel.add(Elements.hollowButton()
                .withLabel(Language.current.get("app.settings.about.button.2"))
                .withAction(e -> {
                    System.out.println("[ACTION]: Opening keyboard-shortcut overview");
                    App.instance.settings.openPage(5);
                }));
    }
}
