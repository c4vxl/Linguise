package de.c4vxl.app.lib.element.chatbar;

import de.c4vxl.app.App;
import de.c4vxl.app.Theme;
import de.c4vxl.app.config.Config;
import de.c4vxl.app.language.Language;
import de.c4vxl.app.lib.component.Button;
import de.c4vxl.app.lib.component.Elements;
import de.c4vxl.app.lib.element.messages.MessagePanel;
import de.c4vxl.app.util.Factory;
import de.c4vxl.app.util.FileUtils;
import de.c4vxl.app.util.Resource;

import javax.swing.*;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

public class ChatOptionButtons extends JPanel {
    public ChatOptionButtons() {
        this.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));
        this.setOpaque(false);

        stopGeneration();

        this.setSize(this.getPreferredSize());
    }

    /**
     * Shows the stop generation button
     */
    public void startGeneration() {
        this.removeAll();

        Button button = createButton(null, Language.current.get("chat.buttons.stop_generation"), () -> {
            System.out.println("[ACTION]: Stop generation");

            if (App.instance.generationThread != null)
                App.instance.generationThread.interrupt();


        });

        button.setForeground(Theme.current.danger);
        button.background = Theme.current.danger;
        button.pressed = Theme.current.danger.darker();
        button.hovered = Theme.current.danger.brighter();
        this.add(button);

        this.repaint();
        this.revalidate();
    }

    /**
     * Shows the regenerate and share buttons
     */
    public void stopGeneration() {
        this.removeAll();

        this.add(createButton("media/reload.png", Language.current.get("chat.buttons.regenerate"), () -> {
            System.out.println("[ACTION]: Regenerate");

            // Get last prompt
            MessagePanel panel = App.instance.messagePanel;
            String prompt = panel.getLastPrompt().getText();

            // Remove past messages
            panel.messages.remove(panel.getLastPrompt());
            panel.messages.remove(panel.getLastResponse());

            // Regenerate
            App.instance._handle_chat_bar(prompt);
        }));

        this.add(createButton("media/share.png", Language.current.get("chat.buttons.share"), () -> {
            System.out.println("[ACTION]: Share chat");

            // Get dir from user
            File dir = FileUtils.openDirDialog("user.home");

            // Handle error
            if (dir == null) {
                App.notificationFromKey("danger", 200, "app.notifications.chat.error.export_dir_invalid");
                System.out.println("[ERROR]: No directory passed!");
                return;
            }

            // Copy chat
            try {
                Files.copy(Path.of(Config.HISTORIES_DIRECTORY + "/" + App.instance.chat + Config.HISTORY_FILE_EXTENSION),
                        Path.of(dir.getAbsolutePath(), App.instance.chat + Config.HISTORY_FILE_EXTENSION));
            } catch (IOException e) {
                App.notificationFromKey("danger", 200, "app.notifications.chat.error.copy_failed");
                System.out.println("[ERROR]: Failed to copy chat!");
            }

            // Notify
            App.notificationFromKey("accent", 200, "app.notifications.chat.info.export_successful", MessagePanel.getDisplayNameFromFile(App.instance.chat));
        }));

        this.repaint();
        this.revalidate();
    }

    private Button createButton(String icon, String label, Runnable l) {
        return new Factory<>(Elements.hollowButton().withLabel(label).withIcon(icon == null ? null : Resource.loadIcon(icon, 20, Theme.current.accent)).withIconTextGap(10))
                .font(Theme.current.font_2.deriveFont(Collections.singletonMap(TextAttribute.WEIGHT, TextAttribute.WEIGHT_MEDIUM)))
                .onClick(l).get();
    }
}
