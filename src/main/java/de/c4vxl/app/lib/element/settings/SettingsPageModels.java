package de.c4vxl.app.lib.element.settings;

import de.c4vxl.app.Theme;
import de.c4vxl.app.config.Config;
import de.c4vxl.app.language.Language;
import de.c4vxl.app.lib.component.Elements;
import de.c4vxl.app.lib.component.RoundedPanel;
import de.c4vxl.app.model.Model;
import de.c4vxl.app.util.Factory;
import de.c4vxl.app.util.FileUtils;
import de.c4vxl.app.util.Resource;
import de.c4vxl.app.util.TextUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SettingsPageModels extends SettingsPage {
    @SuppressWarnings("CallToPrintStackTrace")
    @Override
    public void init() {
        // Title
        this.add(Elements.title(Language.current.get("app.settings.models.title"), this.getWidth() - 200), BorderLayout.NORTH);

        // Buttons
        // TODO: Implement model loading
        buttonPanel.add(Elements.hollowButton()
                .withLabel(Language.current.get("app.settings.models.button.1"))
                .withAction(e -> {
                    System.out.println("[ACTION]: Loading custom model [file]");
                    File file = FileUtils.openFileDialog("user.home", "Linguise model", new String[]{Config.MODEL_FILE_EXTENSION});
                    if (file == null) return;

                    try {
                        Files.copy(file.toPath(), Path.of(Config.MODELS_DIRECTORY + "/" + file.getName()));
                    } catch (IOException ex) {
                        System.out.println("[ERROR]: Couldn't copy model file!");
                        ex.printStackTrace();
                    }
                    reload();
                }));
        buttonPanel.add(Elements.hollowButton()
                .withLabel(Language.current.get("app.settings.models.button.1"))
                .withAction(e -> System.out.println("Loading custom model [url]...")));

        reload();
    }

    /**
     * Reloads all themes
     */
    public void reload() {
        // Add entries
        this.panel.removeAll();
        for (Model model : Config.getLocalModels()) {
            this.panel.add(createEntry(model));
        }

        this.repaint();
        this.revalidate();
    }

    /**
     * Creates the element/button for a model
     * @param model The model
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private JPanel createEntry(Model model) {
        // Main panel
        boolean isHighlighted = model.name.equals(Model.current == null ? "" : Model.current.name);
        JPanel panel = new Factory<>(new RoundedPanel(10)).layout(null).size(getWidth() - 90, 60)
                .opaque(false)
                .hoverAnimation(isHighlighted ? Theme.current.background_2 : Theme.current.background_1, Theme.current.background_2, false)
                .get();

        // Delete button
        JLabel deleteButton = new Factory<>(Elements.iconButton(Resource.loadIcon("media/trash.png", 30, Theme.current.danger)))
                .onClick(() -> {
                    if (model.file != null && model.file.isFile())
                        model.file.delete();

                    reload();
                })
                .centerY(panel).posX(panel.getWidth() - 30 - 15).cursor(Cursor.HAND_CURSOR).get();

        // Size label
        JLabel sizeLabel = new Factory<>(Elements.text(FileUtils.fileSize(model.path), -1)).foreground(Theme.current.text_1).centerY(panel).get();
        sizeLabel.setLocation(panel.getWidth() - 10 - sizeLabel.getWidth(), sizeLabel.getY());
        panel.add(sizeLabel);

        // Model name
        int maxWidth = panel.getWidth() - sizeLabel.getWidth();
        String normal = TextUtils.cutString(model.name, "...", Theme.current.font, maxWidth);
        String cut = TextUtils.cutString(model.name, "...", Theme.current.font, maxWidth - 50);
        JLabel label = new Factory<>(Elements.text(normal, -1)).centerY(panel).posX(10).get();
        panel.add(label);

        // Hover handler
        new Factory<>(panel).onHoverEnter(() -> {
            sizeLabel.setLocation(panel.getWidth() - deleteButton.getWidth() - 100, sizeLabel.getY());
            label.setText(cut);
            panel.add(deleteButton);
            panel.repaint();
        }).onHoverLeave(() -> {
            sizeLabel.setLocation(panel.getWidth() - 10 - sizeLabel.getWidth(), sizeLabel.getY());
            label.setText(normal);
            panel.remove(deleteButton);
            panel.repaint();
        });

        return panel;
    }
}
