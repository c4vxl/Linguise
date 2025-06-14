package de.c4vxl.app;

import de.c4vxl.app.language.Language;
import de.c4vxl.app.lib.component.Elements;
import de.c4vxl.app.util.Factory;
import de.c4vxl.app.lib.component.Line;
import de.c4vxl.app.lib.component.Window;
import de.c4vxl.app.lib.element.chatbar.ChatBar;
import de.c4vxl.app.lib.element.chatbar.ChatOptionButtons;
import de.c4vxl.app.lib.element.messages.MessagePanel;
import de.c4vxl.app.lib.element.model.ModelDropdown;
import de.c4vxl.app.lib.element.settings.Settings;
import de.c4vxl.app.lib.element.sidebar.Sidebar;
import de.c4vxl.app.util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Arrays;

public class App extends Window {
    public Thread generationThread; // Thread of current generation
    public boolean isChatActive = false;

    // Elements on window
    public JPanel content;
    public ChatBar chatBar;
    public ChatOptionButtons chatOptionButtons;
    public Sidebar sidebar;
    public MessagePanel messagePanel;
    public ModelDropdown modelDropdown;
    private Settings settings;
    private boolean isInSettings = false;
    private final JLabel welcomeLogo;

    public App() { this(Theme.current, Language.current); }
    public App(Theme theme, Language language) {
        super(language.get("app.name"), 1200, 800);
        Theme.current = theme;
        Language.current = language;

        // Basic styling
        this.getContentPane().setLayout(null);
        this.background(theme.background)
            .borderRadius(20)
            .layout(null)
            .withButtons();

        this.welcomeLogo = Elements.iconButton(Resource.loadIcon("media/Logo large.png", 400, Theme.current.accent));

        // Keyboard shortcuts
        this.registerKeyboardShortcut("action_chat_new", "control N", this::reset);
        this.registerKeyboardShortcut("action_settings_close", "ESCAPE", this::closeSettings);
        this.registerKeyboardShortcut("action_settings_option", "control K", this::openSettings);


        // Option buttons
        this.add(new Factory<>(Elements.iconButton(Resource.loadIcon("media/settings.png", 45, Theme.current.text)))
                .onClick(this::openSettings).pos(this.getWidth() - 40 - 10, 30).get());
        this.add(new Factory<>(Elements.iconButton(Resource.loadIcon("media/create.png", 45, Theme.current.text)))
                .onClick(this::reset).pos(this.getWidth() - 40 * 2 - 10, 30).get());

        // Load items
        reset();
    }

    /**
     * Opens the settings modal
     */
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

    /**
     * Closes the settings modal
     */
    public void closeSettings() {
        isInSettings = false;
        ((JPanel) this.getGlassPane()).remove(this.settings);
        this.getGlassPane().setVisible(false);
        this.repaint();
        this.revalidate();
    }

    /**
     * Resets the layout and elements of the current window
     */
    public void reset() {
        isChatActive = false;
        if (generationThread != null) generationThread.interrupt();
        if (this.content != null) this.remove(this.content);
        if (this.sidebar != null) this.remove(this.sidebar);
        Arrays.stream(this.getComponents()).filter(x -> x instanceof Line).forEach(this::remove);

        // Create elements
        this.content = new Factory<>(new JPanel()).layout(null).opaque(false).size(890, getHeight()).centerX(this).get();
        this.chatOptionButtons = new Factory<>(new ChatOptionButtons()).posY(this.content.getHeight() - 150).centerX(this.content).get();
        this.sidebar = _create_side_bar();
        this.messagePanel = new MessagePanel(this.content.getWidth(), this.content.getHeight() - 250);
        this.modelDropdown = new Factory<>(new ModelDropdown()).posY(15).centerX(this.content).get();
        this.settings = new Settings(this, getWidth() - 150, getHeight() - 70);
        this.chatBar = new Factory<>(new ChatBar(600, 50, this::_handle_chat_bar))
                .posY((this.getHeight() - 50) / 2 + 70).centerX(this.content).get();

        // Sidebar
        this.add(this.sidebar);
        this.add(new Line(2, getHeight() / 2, Theme.current.background_1).position(7, (getHeight() - (getHeight() / 2)) / 2)); // Indicator on the left

        // Top section
        this.add(new Line(getWidth(), 1, Theme.current.background_1).position(0, 85));
        this.content.add(this.modelDropdown);

        // Center/Content
        this.add(this.content);
        this.content.add(this.messagePanel.pane);
        this.content.add(new Factory<>(this.welcomeLogo).center(this.content).get());
        this.content.add(this.chatBar);
        this.chatBar.focus();

        // Reload
        this.repaint();
        this.revalidate();
    }

    /**
     * Moves the chatbar to the bottom and removes the logo
     */
    public void startChat() {
        if (isChatActive) return;
        isChatActive = true;

        this.chatBar.setLocation(this.chatBar.getX(), this.getHeight() - 85);
        this.content.remove(this.welcomeLogo);

        this.content.add(new Factory<>(Elements.text(
                "<p style='font-weight: 100; font-size: 11px'>" + Language.current.get("chat.info.notice") + "</p>",
                Integer.MAX_VALUE
        )).posY(getHeight() - 30).centerX(this.content).get());
    }

    private Sidebar _create_side_bar() {
        JFrame window = this;
        if (this.sidebar == null) // Create the listener only once
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

    public void _handle_chat_bar(String message) {
        this.chatBar.startHandling();

        startChat();

        this.content.remove(this.chatOptionButtons);
        this.content.add(this.chatOptionButtons);
        this.content.repaint();
        this.content.revalidate();

        this.messagePanel.createPrompt(message);

        this.messagePanel.createResponse();
        generationThread = GenerationUtils.fakeGenerationStream(GenerationUtils.ipsum, 0, this.messagePanel::updateLastResponse, () -> {
            this.messagePanel.completeLastResponse(Language.current.get("chat.message.response.complete.info", "Fake model", "20ms"));
            this.chatBar.stopHandling();
        });
        generationThread.start();
    }
}