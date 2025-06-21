package de.c4vxl.app.lib.element.sidebar;

import de.c4vxl.app.App;
import de.c4vxl.app.Theme;
import de.c4vxl.app.config.Config;
import de.c4vxl.app.language.Language;
import de.c4vxl.app.lib.component.Elements;
import de.c4vxl.app.lib.component.Line;
import de.c4vxl.app.lib.component.RoundedPanel;
import de.c4vxl.app.lib.component.ScrollPane;
import de.c4vxl.app.lib.element.messages.MessagePanel;
import de.c4vxl.app.util.Factory;
import de.c4vxl.app.util.Resource;
import de.c4vxl.app.util.TextUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class Sidebar extends JPanel {
    public JPanel history;
    public ScrollPane hPane;

    public Sidebar() {
        this.setSize(300, 800);
        this.setPreferredSize(this.getSize());
        this.setLocation(-300, 0);
        this.setLayout(null);
        this.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 0, 1, Theme.current.background_1),
                        BorderFactory.createEmptyBorder(15, 0, 0, 0)
                )
        );
        this.setBackground(Theme.current.background);

        // logo
        this.add(new Factory<>(Elements.iconButton(Resource.loadIcon("media/Logo large.png", 200, Theme.current.accent)))
                .posY(30).centerX(this).get());

        // hr underneath logo
        this.add(new Line(getWidth() - 140, 1, Theme.current.text).position(70, 80));

        // history label
        this.add(new Factory<>(Elements.text(Language.current.get("app.sidebar.history.title"), -1))
                .size(999, 20)
                .pos(20, 120).foreground(Theme.current.text_1)
                .border(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 4, 0, 0, Theme.current.text_1),
                        BorderFactory.createEmptyBorder(0, 5, 0, 0)
                )).font(Theme.current.font.deriveFont(Font.BOLD)).get());


        this.history = new Factory<>(new JPanel()).size(getWidth() - 60, getHeight() - 160).pos(10, 150)
                .opaque(false).get();

        this.hPane = new ScrollPane(this.history);
        this.hPane.setBounds(this.history.getX(), this.history.getY(), this.getWidth() - 20, this.history.getHeight());
        this.add(this.hPane);

        reload();
    }

    /**
     * Reload the item list
     */
    public void reload() {
        this.history.removeAll();
        String[] histories = Config.getLocalChats();
        for (String name : histories) {
            this.history.add(createEntry(name, name.equals(App.instance.chat)));
        }

        if (histories.length == 0) {
            this.history.add(Box.createVerticalStrut(550));
            this.history.add(Elements.text("<p style='text-align: center; width: " + (this.history.getWidth() - 35) + "px'>" +
                    Language.current.get("app.sidebar.history.no_elements") + "</p>", -1));
        }

        this.history.setPreferredSize(new Dimension(this.history.getWidth(), 55 * histories.length));

        this.repaint();
        this.revalidate();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public JPanel createEntry(String name, boolean isSelected) {
        String displayName = MessagePanel.getDisplayNameFromFile(name);
        JPanel panel = new Factory<>(new RoundedPanel(10))
                .size(this.history.getWidth(), 50)
                .hoverAnimation(isSelected ? Theme.current.background_1 : Theme.current.background, isSelected ? Theme.current.background_1 : Theme.current.background_3, false)
                .layout(null).cursor(Cursor.HAND_CURSOR)
                .border(BorderFactory.createEmptyBorder(0, 10, 0, 0))
                .onClick(() -> new Thread(() -> App.instance.setChat(name)).start())
                .get();

        JLabel deleteButton = new Factory<>(Elements.iconButton(Resource.loadIcon("media/trash.png", 25, Theme.current.danger)))
                .centerY(panel).posX(panel.getWidth() - 25 - 5).cursor(Cursor.HAND_CURSOR)
                .onClick(() -> {
                    File file = new File(Config.HISTORIES_DIRECTORY + "/" + name + Config.HISTORY_FILE_EXTENSION);
                    if (file.isFile())
                        file.delete();

                    // Reset app layout if current chat has been removed
                    if (name.equals(App.instance.chat))
                        App.instance.setChat(null);

                    // Logging
                    System.out.println("[ACTION]: Deleted chat: " + displayName);
                    App.notificationFromKey("accent", 200, "app.notifications.chat.info.deleted", displayName);

                    reload();
                })
                .get();

        String normal = TextUtils.cutString(displayName, "...", Theme.current.font, panel.getWidth());
        String cut = TextUtils.cutString(displayName, "...", Theme.current.font, panel.getWidth() - 40);
        JLabel label = new Factory<>(Elements.text(isSelected ? cut : normal, -1)).centerY(panel).posX(5).get();

        new Factory<>(panel).onHoverEnter(() -> {
            label.setText(cut);
            panel.add(deleteButton);
            panel.repaint();
        }).onHoverLeave(() -> {
            label.setText(normal);
            panel.remove(deleteButton);
            panel.repaint();
        });

        panel.add(label);
        return panel;
    }
}