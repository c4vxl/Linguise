package de.c4vxl.app;

import de.c4vxl.app.config.Config;
import de.c4vxl.app.language.Language;
import de.c4vxl.app.lib.component.Elements;
import de.c4vxl.app.lib.component.Line;
import de.c4vxl.app.lib.component.NotificationPanel;
import de.c4vxl.app.lib.component.Window;
import de.c4vxl.app.lib.element.chatbar.ChatBar;
import de.c4vxl.app.lib.element.chatbar.ChatOptionButtons;
import de.c4vxl.app.lib.element.messages.MessagePanel;
import de.c4vxl.app.lib.element.model.ModelDropdown;
import de.c4vxl.app.lib.element.settings.Settings;
import de.c4vxl.app.lib.element.sidebar.Sidebar;
import de.c4vxl.app.model.Model;
import de.c4vxl.app.util.AnimationUtils;
import de.c4vxl.app.util.Factory;
import de.c4vxl.app.util.ModalUtils;
import de.c4vxl.app.util.Resource;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.time.Duration;
import java.util.Arrays;

public class App extends Window {
    public static App instance;

    public Thread generationThread; // Thread of current generation
    public boolean isChatActive = false;

    public String chat; // Current chat name

    // Elements on window
    public JPanel content;
    public ChatBar chatBar;
    public ChatOptionButtons chatOptionButtons;
    public Sidebar sidebar;
    public MessagePanel messagePanel;
    public ModelDropdown modelDropdown;
    public NotificationPanel notificationPanel;
    public Settings settings;
    private boolean isInSettings = false;
    private final JLabel welcomeLogo;

    public App() {
        this(Theme.current, Language.current);
    }

    public App(Theme theme, Language language) {
        super(language.get("app.name"), 1200, 800);
        Theme.current = theme;
        Language.current = language;
        App.instance = this;

        // Basic styling
        this.getContentPane().setLayout(null);
        this.background(theme.background)
                .borderRadius(20)
                .layout(null)
                .withButtons();
        this.setIconImage(Resource.loadIcon("media/Logo small.png", 300, Theme.current.accent).getImage());

        this.welcomeLogo = Elements.iconButton(Resource.loadIcon("media/Logo large.png", 400, Theme.current.accent));

        // Keyboard shortcuts
        this.registerKeyboardShortcut("action_chat_new", "control N", this::reset);
        this.registerKeyboardShortcut("action_settings_close", "ESCAPE", this::closeSettings);
        this.registerKeyboardShortcut("action_settings_toggle", "control K", () -> {
            if (isInSettings) this.closeSettings();
            else this.openSettings();
        });

        // Load items
        reset();
    }

    /**
     * Displays a notification in the most recent instance of App
     * @param colorName The background color of the message box; Pass the name of the color in Theme
     * @param time The duration the notification should stay
     * @param translationKey The key to the translation for the message
     * @param translationArgs The arguments to the translation
     */
    public static void notificationFromKey(String colorName, int time, String translationKey, String... translationArgs) {
        // Handle edge cases
        if (Language.current == null || Theme.current == null || App.instance == null || App.instance.notificationPanel == null)
            return;

        // Get color
        Color color;
        try { color = (Color) Theme.current.getClass().getField(colorName).get(Theme.current); }
        catch (Exception e) { color = Theme.current.accent; }

        String message = Arrays.stream(new String[]{
                "app.notifications.language.error.no_translation" // would cause stack overflow if not present in a language
        }).anyMatch(x -> Arrays.asList(translationArgs).contains(x)) ? translationKey // check for "banned" keys
                        : Language.current.get(translationKey, translationArgs);

        App.instance.notificationPanel.addMessage(message, color, time);
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
        // Reset
        isChatActive = false;
        if (generationThread != null) generationThread.interrupt();
        if (this.content != null) this.remove(this.content);
        if (this.sidebar != null) this.remove(this.sidebar);
        Arrays.stream(this.getComponents()).filter(x -> x instanceof Line).forEach(this::remove);

        if (this.sidebar == null) createMouseHandler(); // Register mouse handler (only once)

        // Create elements
        this.content = new Factory<>(new JPanel()).layout(null).opaque(false).size(890, getHeight()).centerX(this).get();
        this.chatOptionButtons = new Factory<>(new ChatOptionButtons()).posY(this.content.getHeight() - 150).centerX(this.content).get();
        this.sidebar = new Sidebar();
        this.messagePanel = new MessagePanel(this.content.getWidth(), this.content.getHeight() - 250);
        this.modelDropdown = new Factory<>(new ModelDropdown()).posY(15).centerX(this.content).get();
        this.settings = new Settings(this, getWidth() - 150, getHeight() - 70);
        this.chatBar = new Factory<>(new ChatBar(600, 50, this::_handle_chat_bar))
                .posY((this.getHeight() - 50) / 2 + 70).centerX(this.content).get();
        this.notificationPanel = new Factory<>(new NotificationPanel())
                .size((int) (getWidth() / 1.5), -1)
                .centerX(this).posY(10)
                .get();

        this.add(this.notificationPanel);

        // Sidebar
        this.add(this.sidebar);
        this.add(new Line(2, getHeight() / 2, Theme.current.background_1).position(7, (getHeight() - (getHeight() / 2)) / 2)); // Indicator on the left

        // Top section
        this.add(new Line(getWidth(), 1, Theme.current.background_1).position(0, 85));
        modelDropdown.setLocation(modelDropdown.getX() - 45 - 30, modelDropdown.getY());
        this.content.add(this.modelDropdown);
        this.content.add(new Line(1, 45, Theme.current.accent).position(modelDropdown.getX() + modelDropdown.getWidth() + 40, 18));
        this.content.add(new Factory<>(Elements.iconButton(Resource.loadIcon("media/create.png", 45, Theme.current.text_1)))
                .tooltip(Language.current.get("app.tooltip.new_chat"))
                .onClick(this::reset).pos(modelDropdown.getX() + modelDropdown.getWidth() + 45 + 30, 17).get());
        this.content.add(new Factory<>(Elements.iconButton(Resource.loadIcon("media/settings.png", 45, Theme.current.text_1)))
                        .tooltip(Language.current.get("app.tooltip.settings_open"))
                        .onClick(this::openSettings).pos(modelDropdown.getX() + modelDropdown.getWidth() + 45 + 45 + 30, 17).get());

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

        // Reset option buttons
        this.content.remove(this.chatOptionButtons);
        this.content.add(this.chatOptionButtons);

        // Move chatbar
        this.chatBar.setLocation(this.chatBar.getX(), this.getHeight() - 85);

        // Remove logo
        this.content.remove(this.welcomeLogo);

        // Create notice
        this.content.add(new Factory<>(Elements.text(
                "<p style='font-weight: 100; font-size: 11px'>" + Language.current.get("chat.info.notice") + "</p>",
                Integer.MAX_VALUE
        )).posY(getHeight() - 30).centerX(this.content).get());

        this.repaint();
        this.revalidate();
    }

    /**
     * Registers the mouse listener for the sidebar
     */
    public void createMouseHandler() {
        JFrame window = this;
        Toolkit.getDefaultToolkit().addAWTEventListener(e -> {
            if (e instanceof MouseEvent event) {
                if (event.getID() != MouseEvent.MOUSE_MOVED) return;
                SwingUtilities.invokeLater(() -> {
                    if (!window.isVisible()) return;
                    Point windowLocation = window.getLocationOnScreen();
                    int wx = event.getXOnScreen() - windowLocation.x;
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
    }

    public void _handle_chat_bar(String message) {
        // Return if no model is selected
        Model model = Model.current;
        if (model == null) {
            App.notificationFromKey("danger", 300, "app.notifications.models.error.none_selected");
            System.out.println("[ERROR]: No model selected");
            return;
        }

        // Start handler
        this.chatBar.startHandling();
        this.chatOptionButtons.startGeneration();
        startChat();

        // Display prompt
        this.messagePanel.createPrompt(message);

        // Generate response
        this.messagePanel.createResponse();
        long start = System.nanoTime();
        String[] total = new String[1];
        generationThread = model.generate(message, (generated) -> {
            this.messagePanel.updateLastResponse(generated);
            total[0] = generated;
        }, () -> {
            if (total[0] != null)
                this.messagePanel.updateLastResponse(total[0]);

            try { this.messagePanel.completeLastResponse(model.name, Duration.ofNanos(System.nanoTime() - start).getSeconds()); }
            catch (Exception ignored) {}

            this.chat = this.messagePanel.getName();
            this.sidebar.reload();
            this.chatOptionButtons.stopGeneration();
            this.chatBar.stopHandling();
        });
        generationThread.start();
    }

    /**
     * Opens a new instance of App and closes the current one
     * @param theme The new theme
     * @param language The new language
     */
    public static void reopen(Theme theme, Language language) {
        if (theme == null || language == null)
            return;

        App old = App.instance;

        new App(theme, language).open();

        if (old == null) return;

        SwingUtilities.invokeLater(() -> {
            old.close();
            old.dispose();
        });
    }

    /**
     * Set the current chat
     * @param name The name of the chat
     */
    public void setChat(String name) {
        this.chat = name;
        this.reset();
        if (name == null) return;
        this.messagePanel.load(Config.HISTORIES_DIRECTORY + "/" + name + Config.HISTORY_FILE_EXTENSION);
    }
}