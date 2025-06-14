package de.c4vxl.app.lib.element.settings;

import de.c4vxl.app.App;
import de.c4vxl.app.Theme;
import de.c4vxl.app.config.Config;
import de.c4vxl.app.language.Language;
import de.c4vxl.app.lib.component.Elements;
import de.c4vxl.app.lib.component.RoundedPanel;
import de.c4vxl.app.util.Factory;
import de.c4vxl.app.util.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class SettingsPageTheme extends SettingsPage {
    @SuppressWarnings("CallToPrintStackTrace")
    @Override
    public void init() {
        // Title
        this.add(Elements.title(Language.current.get("app.settings.themes.title"), this.getWidth() - 200), BorderLayout.NORTH);

        // Buttons
        buttonPanel.add(Elements.hollowButton()
                .withLabel(Language.current.get("app.settings.themes.button.1"))
                .withAction(e -> {
                    System.out.println("[ACTION]: Loading custom theme");
                    File file = FileUtils.openFileDialog("user.home", "Linguise theme files", new String[]{Config.THEME_FILE_EXTENSION});
                    if (file == null) return;

                    try {
                        Files.copy(file.toPath(), Path.of(Config.THEMES_DIRECTORY + "/" + file.getName()));
                    } catch (IOException ex) {
                        System.out.println("[ERROR]: Couldn't copy theme file!");
                        ex.printStackTrace();
                    }
                    reload();
                }));

        buttonPanel.add(Elements.hollowButton()
                .withLabel(Language.current.get("app.settings.themes.button.2"))
                .withAction(e -> {
                    System.out.println("[ACTION]: Reloading themes list");
                    reload();
                }));

        this.reload();
    }

    /**
     * Reloads all themes
     */
    public void reload() {
        System.out.println("[UPDATE]: Reloading SettingsPageTheme");
        Theme[] themes = Config.getLocalThemes();
        System.out.println("[UPDATE]: Found themes: " + String.join(", ", Arrays.stream(themes).map(x -> x.name).toArray(String[]::new)));

        // Add entries
        this.panel.removeAll();
        for (Theme theme : themes) {
            this.panel.add(createEntry(theme.name, theme.background_1, theme.background_3, theme.name.equals(Theme.current.name), () -> {
                if (Theme.current.name.equals(theme.name)) return;
                SwingUtilities.getWindowAncestor(this).dispose();
                new App(theme, Language.current).open();
                Config.setConfigValue("app.theme", theme.getFileName());
            }));
        }

        this.repaint();
        this.revalidate();
    }

    /**
     * Creates the element/button for a theme
     * @param name The name of the theme
     * @param c1 The first color of the theme
     * @param c2 The last color of the theme
     * @param isHighlighted Is the element highlighted/is it the currently selected theme
     */
    private JPanel createEntry(String name, Color c1, Color c2, boolean isHighlighted, Runnable onClick) {
        JPanel panel = new Factory<>(new RoundedPanel(10)).layout(null).size(130, 190).opaque(false).cursor(Cursor.HAND_CURSOR)
                .hoverAnimation(isHighlighted ? Theme.current.background_2 : Theme.current.background_1, Theme.current.background_2, false)
                .onClick(onClick).get();

        // Label
        panel.add(new Factory<>(Elements.text(name, panel.getWidth())).posY(20).centerX(panel).get());

        // Colors preview
        panel.add(new Factory<>(new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int size = getWidth();

                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setPaint(new GradientPaint(0, 0, c1, size, size, c2));
                g2d.fillOval((getWidth() - size) / 2, (getHeight() - size) / 2, size, size);
                g2d.dispose();
            }
        }).size(100, 100).opaque(false).posY(70).centerX(panel).get());

        return panel;
    }
}