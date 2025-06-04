package de.c4vxl.app.lib.element;

import de.c4vxl.app.Theme;
import de.c4vxl.app.util.Resource;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class ChatBar extends JPanel {
    public boolean isLocked = false;

    public ChatBar(int width, int height, Consumer<String> onSubmit) {
        this.setSize(width, height);
        this.setPreferredSize(this.getSize());
        this.setLayout(null);

        JTextField textField = new JTextField();
        textField.addActionListener(l -> {
            if (textField.getText().isBlank() || isLocked) return;

            onSubmit.accept(textField.getText());
            textField.setText("");
        });
        textField.setOpaque(false);
        textField.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        textField.setForeground(Theme.current.text);
        textField.setBounds(0, 0, getWidth() - 50, getHeight());
        this.add(textField);

        JLabel submitButton = new JLabel(Resource.loadIcon("send_b.png", 40));
        submitButton.setSize(submitButton.getPreferredSize());
        submitButton.setBounds(getWidth() - 45, (getHeight() - submitButton.getHeight()) / 2, submitButton.getWidth(), submitButton.getHeight());
        submitButton.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { textField.postActionEvent(); }
        });
        this.add(submitButton);
    }

    /**
     * Lock chatbar to run your handling logic
     */
    public ChatBar startHandling() {
        this.isLocked = true;
        return this;
    }

    /**
     * Unlock chatbar when your handling logic is done
     */
    public ChatBar stopHandling() {
        this.isLocked = false;
        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setPaint(new GradientPaint(0, 0, Theme.current.background_1, getWidth() - 10, 10, Theme.current.background_2));
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

        g2d.dispose();
    }
}