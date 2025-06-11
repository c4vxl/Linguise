package de.c4vxl.app.lib.settings;

import javax.swing.*;

public abstract class SettingsPage extends JPanel {
    public SettingsPage() {
        this.setBorder(BorderFactory.createEmptyBorder(30, 20, 10, 20));
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setOpaque(false);
    }

    public void _init() {
        this.removeAll();
        this.init();
    }

    public abstract void init();
}