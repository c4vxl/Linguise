package de.c4vxl.app;

import de.c4vxl.app.lib.component.HR;
import de.c4vxl.app.lib.component.Window;
import de.c4vxl.app.lib.element.*;
import de.c4vxl.app.lib.settings.Settings;
import de.c4vxl.app.util.AnimationUtils;
import de.c4vxl.app.util.GenerationUtils;
import de.c4vxl.app.util.Resource;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class App extends Window {
    private final Container contentPane;

    public Theme theme;

    public JPanel content;
    public final ChatBar chatBar;
    public final ChatOptionButtons chatOptionButtons;
    public final Sidebar sidebar;
    public final MessagePanel messagePanel;
    public final ModelDropdown modelDropdown;

    private Settings settings;
    private boolean isInSettings = false;

    public App() { this(Theme.dark); }
    public App(Theme theme) {
        super("Linguise", 1200, 800);

        this.contentPane = this.getContentPane();
        this.contentPane.setLayout(null);

        this.theme = theme;
        Theme.current = theme;

        this.background(theme.background)
            .borderRadius(20)
            .layout(null)
            .withButtons();

        this.content = _create_content_pane();
        this.chatBar = _create_chat_bar();
        this.chatOptionButtons = _create_chat_option_buttons();
        this.sidebar = _create_size_bar();
        this.messagePanel = _create_message_panel();
        this.modelDropdown = _create_model_dropdown();
        this.settings = new Settings(this, getWidth() - 150, getHeight() - 70);

        this.add(this.sidebar);

        this.add(new HR(getWidth(), 1, Theme.current.background_1)
                .position(0, 85));

        this.add(content);
        this.add(_create_settings_button());
        this.content.add(modelDropdown);
        this.content.add(this.chatBar);
        this.content.add(_create_notice());
        this.content.add(this.messagePanel.pane);
    }

    public void openSettings() {
        if (isInSettings) return;
        isInSettings = true;
        this.settings.setLocation((getWidth() - settings.getWidth()) / 2, (getHeight() - settings.getHeight()) / 2);
        contentPane.add(settings);
        contentPane.setComponentZOrder(this.settings, 1);
        this.repaint();
        this.revalidate();
    }

    public void closeSettings() {
        isInSettings = false;
        this.contentPane.remove(this.settings);
        this.repaint();
        this.revalidate();
    }

    public JLabel _create_settings_button() {
        JLabel label = new JLabel(Resource.loadIcon("settings.png", 40));
        label.setSize(label.getPreferredSize());
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        label.setLocation(getWidth() - 40 - 10, getHeight() - 40 - 10);
        label.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { openSettings(); }
        });
        return label;
    }

    public ChatBar _create_chat_bar() {
        ChatBar bar = new ChatBar(600, 50, this::_handle_chat_bar);
        bar.setLocation((this.content.getWidth() - 600) / 2, this.getHeight() - 90);
        return bar;
    }

    public ModelDropdown _create_model_dropdown() {
        ModelDropdown dropdown = new ModelDropdown();
        dropdown.setLocation((this.content.getWidth() - dropdown.getWidth()) / 2, 15);
        return dropdown;
    }

    public ChatOptionButtons _create_chat_option_buttons() {
        ChatOptionButtons buttons = new ChatOptionButtons();
        buttons.setLocation((this.content.getWidth() - buttons.getWidth()) / 2, this.content.getHeight() - 150);
        return buttons;
    }

    public MessagePanel _create_message_panel() {
        MessagePanel panel = new MessagePanel(this.content.getWidth(), this.content.getHeight() - 250);
        panel.pane.setLocation(0, 85);

        return panel;
    }

    public JPanel _create_content_pane() {
        JPanel content = new JPanel();
        content.setLayout(null);
        content.setBounds((getWidth() - 890) / 2, 0, 890, getHeight());
        content.setOpaque(false);
        return content;
    }

    public Sidebar _create_size_bar() {
        this.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (e.getX() < 400 && !isInSettings) {
                    if (sidebar.getX() != -300) return;

                    AnimationUtils.animateEaseCubic(sidebar, 12, 30, 60, (elem, frame) -> {
                        int x = (int) (frame * 300) - 300;
                        sidebar.setLocation(x, 0);
                        content.setLocation(Math.max(x + 305, 155), 0);
                    });
                } else if (sidebar.getX() == 0) {
                    AnimationUtils.animateEaseCubic(sidebar, 12, 30, 60, (elem, frame) -> {
                        int x = -(int) (frame * 300);
                        sidebar.setLocation(x, 0);
                        content.setLocation(Math.min(455 + x, getWidth() - content.getWidth() - 5), 0);
                    });
                }
            }
        });

        return new Sidebar();
    }

    public JLabel _create_notice() {
        JLabel notice = new JLabel("<html><body style='font-family: Inter; font-weight: 100'>Linguise can make mistakes. <b>Consider checking important information!</b></body></html>");
        notice.setSize(notice.getPreferredSize());
        notice.setLocation((content.getWidth() - notice.getWidth()) / 2, getHeight() - 30);
        notice.setForeground(theme.text);
        return notice;
    }

    public void _handle_chat_bar(String message) {
        this.chatBar.startHandling();

        this.content.remove(this.chatOptionButtons);
        this.content.add(this.chatOptionButtons);
        this.content.repaint();
        this.content.revalidate();

        this.messagePanel.createPrompt(message);

        this.messagePanel.createResponse();
        Thread thread = GenerationUtils.fakeGenerationStream(GenerationUtils.ipsum, 0, this.messagePanel::updateLastResponse);
        thread.start();
        new Thread(() -> {
            try {
                thread.join();
                this.messagePanel.completeLastResponse("Done!");
                this.chatBar.stopHandling();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}