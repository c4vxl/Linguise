package de.c4vxl.app.lib.element.settings;

import de.c4vxl.app.util.Elements;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class SettingsPageAbout extends SettingsPage {
    @Override
    public void init() {
        int maxTextWidth = this.getWidth() - 200;

        this.add(Elements.title("About Linguise", maxTextWidth));
        this.add(Box.createVerticalStrut(10));
        this.add(Elements.text("Linguise is a chat bot written entirely from scratch in the programming language ‘Java’programming language ‘Java’", maxTextWidth));

        this.add(Box.createVerticalGlue());


        this.add(Elements.hollowButton().withLabel("Visit project on GitHub")
                .withAction((event) -> {
                    try {
                        Desktop.getDesktop().browse(new URI("https://github.com/"));
                    } catch (IOException | URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }));
    }
}
