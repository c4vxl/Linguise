package de.c4vxl.app.lib.element;

import de.c4vxl.app.Theme;
import de.c4vxl.app.lib.component.Tooltip;
import de.c4vxl.app.util.Resource;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MessageResponse extends JPanel {
    private final JLabel text;
    private final int width = 860;

    public MessageResponse() {
        this.setLayout(new GridLayout(1, 1));
        this.setBackground(Theme.current.accent_1);
        this.setForeground(Theme.current.text);
        this.setBorder(BorderFactory.createEmptyBorder(3, 10, 10, 10));

        text = new JLabel();
        text.setForeground(Theme.current.text);
        this.add(text);

        updateMessage("");
    }

    public MessageResponse updateMessage(String message) {
        this.text.setText("<html>" +
                "<div style='width: " + width + "px; font-family: Inter; font-weight: 100; font-size: 11px;'>" +
                message +
                "</div></html>"
        );
        this.update();
        return this;
    }

    public MessageResponse complete(String info) {
        this.setLayout(new GridLayout(2, 1));
        this.add(createBottomPanel(info));
        update();
        return this;
    }

    public void update() {
        this.setMaximumSize(new Dimension(width, Integer.MAX_VALUE));
        this.setSize(width + 20, this.getPreferredSize().height);
        this.repaint();
    }

    private JLabel createButton(String path, int width, String tooltipText, Runnable onClick) {
        MessageResponse self = this;
        JLabel label = new JLabel(Resource.loadIcon(path, width)) {
            @Override
            public JToolTip createToolTip() {
                Tooltip tip = new Tooltip(self.getForeground(), self.getBackground().darker());
                tip.setComponent(this);
                return tip;
            }
        };;
        label.setToolTipText(tooltipText);

        label.setSize(label.getPreferredSize());
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        label.addMouseListener(new MouseAdapter() { @Override public void mousePressed(MouseEvent e) { onClick.run(); } });
        return label;
    }

    private JPanel createBottomPanel(String info) {
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Left-aligned info label
        JLabel infoText = new JLabel("<html><body style='font-family: Inter; font-weight: 100'>" + info + "</body></html>");
        infoText.setForeground(Theme.standard.text_1);
        infoText.setVerticalAlignment(SwingConstants.CENTER); // vertical centering in case fonts vary

        // Right-aligned button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentY(Component.CENTER_ALIGNMENT);

        buttonPanel.add(createButton("copy.png", 22, "Copy message", () -> System.out.println("Copy")));
        buttonPanel.add(createButton("change.png", 18, "Regenerate with a different model", () -> System.out.println("Change")));
        buttonPanel.add(createButton("sound.png", 25, "Speak out loud", () -> System.out.println("Speak")));

        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.X_AXIS));
        inner.add(infoText);
        inner.add(Box.createHorizontalGlue()); // push button panel to the right
        inner.add(buttonPanel);

        bottomPanel.add(inner, BorderLayout.CENTER);

        return bottomPanel;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(getBackground());
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

        g2d.dispose();
    }
}
