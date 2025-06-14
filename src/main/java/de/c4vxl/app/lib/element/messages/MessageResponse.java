package de.c4vxl.app.lib.element.messages;

import de.c4vxl.app.Theme;
import de.c4vxl.app.language.Language;
import de.c4vxl.app.lib.component.RoundedPanel;
import de.c4vxl.app.lib.component.Tooltip;
import de.c4vxl.app.lib.component.Elements;
import de.c4vxl.app.util.Resource;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MessageResponse extends RoundedPanel {
    private final JLabel text;
    private final int width;
    public float opacity = 1.0f;

    public MessageResponse() { this(500); }
    public MessageResponse(int width) {
        super(15);

        this.width = width - 20;

        this.setLayout(new BorderLayout());
        this.setBackground(Theme.current.accent_1);
        this.setForeground(Theme.current.text);
        this.setBorder(BorderFactory.createEmptyBorder(3, 10, 10, 10));

        text = new JLabel();
        text.setForeground(Theme.current.text);
        this.add(text, BorderLayout.CENTER);

        updateMessage("");
    }

    public MessageResponse updateMessage(String message) {
        this.text.setText("<html>" +
                "<div style='width: " + (width - 200) + "px; font-family: Inter; font-weight: 100; font-size: 11px;'>" +
                message +
                "</div></html>"
        );
        this.update();
        return this;
    }

    public MessageResponse complete(String info) {
        this.add(createBottomPanel(info), BorderLayout.SOUTH);
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
        JLabel label = new JLabel(Resource.loadIcon(path, width, Theme.current.text)) {
            @Override
            public JToolTip createToolTip() {
                Tooltip tip = new Tooltip(self.getForeground(), self.getBackground().darker());
                tip.setComponent(this);
                return tip;
            }
        };
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
        bottomPanel.setSize(getWidth(), 20);


        // Left-aligned info label
        JLabel label = Elements.text("<p style='font-weight: 100; font-size: 11px;'>" + info + "</p>", (int) (width / 1.25));
        label.setForeground(Theme.current.text_1);
        bottomPanel.add(label, BorderLayout.LINE_START);

        // Right-aligned button panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(createButton("media/copy.png", 22, Language.current.get("chat.message.response.options.copy.tooltip"), () -> System.out.println("Copy")));
        buttonPanel.add(createButton("media/change.png", 18, Language.current.get("chat.message.response.options.switch.tooltip"), () -> System.out.println("Change")));
        buttonPanel.add(createButton("media/sound.png", 25, Language.current.get("chat.message.response.options.speak.tooltip"), () -> System.out.println("Speak")));
        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        return bottomPanel;
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        super.paint(g2);
        g2.dispose();
    }
}
