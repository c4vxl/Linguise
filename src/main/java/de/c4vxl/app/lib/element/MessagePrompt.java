package de.c4vxl.app.lib.element;

import de.c4vxl.app.Theme;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MessagePrompt extends JPanel {
    private final JLabel infoText;
    private final JLabel text;
    private final int width = 860;

    public MessagePrompt(String prompt) {
        this.setLayout(new GridLayout(2, 1));
        this.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, Theme.current.background_1),
                BorderFactory.createEmptyBorder(5, 10, 5, 5)
        ));

        this.setBackground(Theme.current.background_3);

        infoText = new JLabel("<html><body style='width:" + width + "px; font-family: Inter; font-weight: 100; font-size: 11px;'>" +
                "You - <b>" + DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm").format(LocalDateTime.now()) + "</b>" +
                "</body></html>");
        infoText.setForeground(Theme.current.text_1);
        this.add(infoText);

        text = new JLabel(prompt);
        text.setForeground(Theme.current.text);
        this.add(text);

        update();
    }

    public void update() {
        this.setMaximumSize(new Dimension(width, Integer.MAX_VALUE));
        this.setSize(width + 20, this.getPreferredSize().height);
        this.repaint();
    }
}
