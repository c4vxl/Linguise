package de.c4vxl.app.lib.element;

import de.c4vxl.app.App;
import de.c4vxl.app.util.Resource;
import de.c4vxl.app.util.VoidFunction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ChatBar extends JPanel {
    private App app;

    public ChatBar(App app, VoidFunction<String> onSubmit) {
        this.app = app;

        this.setBounds((app.content.getWidth() - 600) / 2, app.getHeight() - 90, 600, 50);
        this.setPreferredSize(this.getSize());
        this.setLayout(null);

        JTextField textField = new JTextField();
        textField.addActionListener(l -> {
            if (textField.getText().isBlank()) return;

            onSubmit.apply(textField.getText());
            textField.setText("");
        });
        textField.setOpaque(false);
        textField.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        textField.setForeground(app.theme.text);
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

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setPaint(new GradientPaint(0, 0, app.theme.background_1, getWidth() - 10, 10, app.theme.background_2));
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

        g2d.dispose();
    }
}