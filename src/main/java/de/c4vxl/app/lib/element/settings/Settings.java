package de.c4vxl.app.lib.element.settings;

import de.c4vxl.app.App;
import de.c4vxl.app.Theme;
import de.c4vxl.app.lib.component.Dropdown;
import de.c4vxl.app.lib.component.Line;
import de.c4vxl.app.lib.component.RoundedPanel;
import de.c4vxl.app.lib.component.Window;
import de.c4vxl.app.util.Factory;
import de.c4vxl.app.util.Resource;

import javax.swing.*;

public class Settings extends RoundedPanel {
    public App app;
    public static int SIDEBAR_WIDTH = 300;

    public SettingsPage container;
    public JPanel sideBar = new JPanel();

    public JLabel titleLabel = new JLabel("SETTINGS");

    private int currentPage = 0;
    private String[] pageNames = new String[]{"About", "Models", "Theme", "Language"};
    private SettingsPage[] pages = new SettingsPage[]{ new SettingsPageAbout(), new SettingsPageModels(), new SettingsPageTheme(), new SettingsPageLanguage() };

    public Settings(App app, int width, int height) {
        super(15);
        this.app = app;

        this.setLayout(null);

        this.setBackground(Theme.current.background_1);

        this.setSize(width, height);
        this.setPreferredSize(this.getSize());

        this.add(new Line(getWidth() - SIDEBAR_WIDTH, 1, Theme.current.background)
                .position(SIDEBAR_WIDTH, 40));

        this.add(new Line(1, getHeight(), Theme.current.background)
                .position(SIDEBAR_WIDTH, 0));

        this.sideBar.setBounds(0, 0, SIDEBAR_WIDTH, getHeight());
        this.sideBar.setOpaque(false);
        this.sideBar.setLayout(new BoxLayout(this.sideBar, BoxLayout.Y_AXIS));

        this.add(this.sideBar);


        JLabel closeButton = Window._create_top_bar_button(Resource.loadIcon("media/cross.png", 15, Theme.current.text_1), "Close", app::closeSettings);

        closeButton.setLocation(getWidth() - 15 - 13, 13);
        this.add(closeButton);

        titleLabel.setForeground(Theme.current.text);
        titleLabel.setFont(Theme.current.font);
        this.add(titleLabel);

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


        titleLabel.setText("Settings - " + pageNames[currentPage]);
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