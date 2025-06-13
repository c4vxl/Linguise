package de.c4vxl.app.lib.element.settings;

import de.c4vxl.app.config.Config;
import de.c4vxl.app.lib.component.Elements;
import de.c4vxl.app.util.Factory;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class SettingsPageAbout extends SettingsPage {
    @Override
    public void init() {
        // Title
        int maxTextWidth = this.getWidth() - 200;
        this.add(Elements.title("About Linguise", maxTextWidth), BorderLayout.PAGE_START);

        // Content
        this.panel.add(new Factory<>(new JPanel()).opaque(false).apply((panel) -> {
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.add(Elements.text("Linguise is a chat bot written entirely from scratch in the programming language ‘Java’", maxTextWidth));
            panel.add(Box.createVerticalGlue());
        }).get());

        // Buttons
        buttonPanel.add(Elements.hollowButton()
                .withLabel("Visit Project on GitHub")
                .withAction(e -> {
                    System.out.println("[ACTION]: Opening GitHub");
                    try { Desktop.getDesktop().browse(new URI("https://github.com/")); }
                    catch (IOException | URISyntaxException ex) { throw new RuntimeException(ex); }
                }));

        buttonPanel.add(Elements.hollowButton()
                .withLabel("Open data folder")
                .withAction(e -> {
                    System.out.println("[ACTION]: Opening data dir");
                    try { Desktop.getDesktop().open(new File(Config.APP_DIRECTORY)); }
                    catch (IOException ex) { throw new RuntimeException(ex); }
                }));
    }
}
