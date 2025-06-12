package de.c4vxl.app;

import de.c4vxl.app.lib.component.HR;
import de.c4vxl.app.lib.component.Window;
import de.c4vxl.app.lib.element.chatbar.ChatBar;
import de.c4vxl.app.lib.element.chatbar.ChatOptionButtons;
import de.c4vxl.app.lib.element.messages.MessagePanel;
import de.c4vxl.app.lib.element.model.ModelDropdown;
import de.c4vxl.app.lib.element.sidebar.Sidebar;
import de.c4vxl.app.lib.element.settings.Settings;
import de.c4vxl.app.util.*;

import javax.swing.*;
import java.awt.*;
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
        this.add(new HR(2, getHeight() / 2, Theme.current.background_1)
                .position(7, (getHeight() - (getHeight() / 2)) / 2));

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

        this.settings = new Factory<>(this.settings).center(this).get();
        ModalUtils.createModalBackground(this)
                .add(this.settings);
        ((JPanel) this.getGlassPane()).setComponentZOrder(this.settings, 0);

        this.repaint();
        this.revalidate();
    }

    public void closeSettings() {
        isInSettings = false;
        ((JPanel) this.getGlassPane()).remove(this.settings);
        this.getGlassPane().setVisible(false);
        this.repaint();
        this.revalidate();
    }

    public JLabel _create_settings_button() {
        return new Factory<>(Elements.iconButton(Resource.loadIcon("settings.png", 40)))
                .onClick(this::openSettings).pos(getWidth() - 40 - 10, getHeight() - 40 - 10).get();
    }

    public ChatBar _create_chat_bar() {
        return new Factory<>(new ChatBar(600, 50, this::_handle_chat_bar))
                .posY(this.getHeight() - 90).centerX(this.content).get();
    }

    public ModelDropdown _create_model_dropdown() {
        return new Factory<>(new ModelDropdown()).posY(15).centerX(this.content).get();
    }

    public ChatOptionButtons _create_chat_option_buttons() {
        return new Factory<>(new ChatOptionButtons()).posY(this.content.getHeight() - 150).centerX(this.content).get();
    }

    public MessagePanel _create_message_panel() {
        MessagePanel panel = new MessagePanel(this.content.getWidth(), this.content.getHeight() - 250);
        panel.pane.setLocation(0, 85);
        return panel;
    }

    public JPanel _create_content_pane() {
        return new Factory<>(new JPanel()).layout(null).opaque(false).size(890, getHeight()).centerX(this).get();
    }

    public Sidebar _create_size_bar() {
        JFrame window = this;
        Toolkit.getDefaultToolkit().addAWTEventListener(e -> {
            if (e instanceof MouseEvent event) {
                if (event.getID() != MouseEvent.MOUSE_MOVED) return;
                SwingUtilities.invokeLater(() -> {
                    if (!window.isVisible()) return;
                    Point windowLocation = window.getLocationOnScreen();
                    int wx = event.getXOnScreen() - windowLocation.x, wy = event.getYOnScreen() - windowLocation.y;
                    if (wy <= 200) return;

                    if (wx < 300) {
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
                });
            }
        }, AWTEvent.MOUSE_MOTION_EVENT_MASK);

        return new Sidebar();
    }

    public JLabel _create_notice() {
        return new Factory<>(Elements.text(
                "<p style='font-weight: 100; font-size: 11px'>Linguise can make mistakes. <b>Consider checking important information!</b></p>",
                Integer.MAX_VALUE
        )).posY(getHeight() - 30).centerX(this.content).get();
    }

    public void _handle_chat_bar(String message) {
        this.chatBar.startHandling();

        this.content.remove(this.chatOptionButtons);
        this.content.add(this.chatOptionButtons);
        this.content.repaint();
        this.content.revalidate();

        this.messagePanel.createPrompt(message);

        this.messagePanel.createResponse();
        Thread thread = GenerationUtils.fakeGenerationStream(GenerationUtils.ipsum, 5, this.messagePanel::updateLastResponse);
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