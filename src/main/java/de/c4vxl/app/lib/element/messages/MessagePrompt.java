package de.c4vxl.app.lib.element.messages;

import de.c4vxl.app.Theme;
import de.c4vxl.app.util.Elements;
import de.c4vxl.app.util.Factory;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MessagePrompt extends JPanel {
    private final JLabel infoText;
    private final JLabel text;
    private final int width;

    public MessagePrompt(String prompt) { this(prompt, 500); }
    public MessagePrompt(String prompt, int width) {
        width = width - 20;
        this.width = width;

        this.setLayout(new GridLayout(2, 1));
        this.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, Theme.current.background_1),
                BorderFactory.createEmptyBorder(5, 10, 5, 5)
        ));

        this.setBackground(Theme.current.background_3);

        // Information
        this.infoText = new Factory<>(Elements.text("<p style='font-weight: 100; font-size: 11px;'>" +
                        "You - <b>" + DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm").format(LocalDateTime.now()) + "</b></p>",
                width - 200)).foreground(Theme.current.text_1).get();
        this.add(this.infoText);

        // Prompt
        this.text = Elements.text(prompt, width - 200);
        this.add(this.text);

        update();
    }

    public void update() {
        this.setMaximumSize(new Dimension(width, Integer.MAX_VALUE));
        this.setSize(width + 20, this.getPreferredSize().height);
        this.repaint();
    }
}
