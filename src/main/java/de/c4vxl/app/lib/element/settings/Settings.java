package de.c4vxl.app.lib.element.settings;

import de.c4vxl.app.App;
import de.c4vxl.app.Theme;
import de.c4vxl.app.language.Language;
import de.c4vxl.app.lib.component.*;
import de.c4vxl.app.util.Factory;
import de.c4vxl.app.util.Resource;

import javax.swing.*;

public class Settings extends RoundedPanel {
    public App app;
    public static int SIDEBAR_WIDTH = 300;

    public SettingsPage container;
    public JPanel sideBar;

    public JLabel titleLabel = Elements.text(Language.current.get("app.settings.title", ""), -1);

    private int currentPage = 0;
    private String[] pageNames = new String[]{
            Language.current.get("app.settings.tabs.about"),
            Language.current.get("app.settings.tabs.models"),
            Language.current.get("app.settings.tabs.themes"),
            Language.current.get("app.settings.tabs.language"),
    };
    private SettingsPage[] pages = new SettingsPage[]{ new SettingsPageAbout(), new SettingsPageModels(), new SettingsPageTheme(), new SettingsPageLanguage() };

    public Settings(App app, int width, int height) {
        super(15);
        this.app = app;

        this.setLayout(null);
        this.setBackground(Theme.current.background_1);
        this.setSize(width, height);
        this.setPreferredSize(this.getSize());

        // Borders
        this.add(new Line(getWidth() - SIDEBAR_WIDTH, 1, Theme.current.background)
                .position(SIDEBAR_WIDTH, 40));
        this.add(new Line(1, getHeight(), Theme.current.background)
                .position(SIDEBAR_WIDTH, 0));

        this.sideBar = new Factory<>(new JPanel())
                .size(SIDEBAR_WIDTH, getHeight()).opaque(false)
                .apply(e -> e.setLayout(new BoxLayout(e, BoxLayout.Y_AXIS))).get();

        this.add(this.sideBar);

        JLabel closeButton = Window._create_top_bar_button(Resource.loadIcon("media/cross.png", 15, Theme.current.text_1), "Close", app::closeSettings);

        closeButton.setLocation(getWidth() - 15 - 13, 13);
        this.add(closeButton);

        this.add(titleLabel);

        Factory<Settings> factory = new Factory<>(this);
        for (int i = 0; i < pageNames.length; i++) {
            if (i > 9) continue;
            int finalI = i;
            factory.registerKeyboardShortcut("action_settings_tab_" + pageNames[i], "control " + (i + 1), () -> openPage(finalI));
        }

        openPage(0);
    }

    public void reload() {
        this.sideBar.removeAll();

        this.sideBar.add(Box.createVerticalGlue());

        for (int i = 0; i < pageNames.length; i++) {
            int finalI = i;
            JPanel panel = new Factory<>(Dropdown.createDefaultItem(pageNames[i], () -> openPage(finalI), currentPage == i))
                    .size(SIDEBAR_WIDTH - 20, 50).get();

            this.sideBar.add(panel);
            if (i != pageNames.length - 1)
                this.sideBar.add(Box.createVerticalStrut(4));
        }
        this.sideBar.add(Box.createVerticalGlue());


        titleLabel.setText(Language.current.get("app.settings.title", pageNames[currentPage]));
        titleLabel.setSize(titleLabel.getPreferredSize());
        titleLabel.setLocation((getWidth() - SIDEBAR_WIDTH - titleLabel.getWidth()) / 2 + SIDEBAR_WIDTH, (40 - titleLabel.getHeight()) / 2);

        this.repaint();
        this.revalidate();
    }

    public void openPage(int page) {
        currentPage = page;

        if (this.container != null)
            this.remove(this.container);
        this.container = pages[page];
        this.container.setBounds(SIDEBAR_WIDTH, 40, getWidth() - SIDEBAR_WIDTH, getHeight() - 40);
        this.container._init();
        this.add(this.container);

        reload();
    }
}