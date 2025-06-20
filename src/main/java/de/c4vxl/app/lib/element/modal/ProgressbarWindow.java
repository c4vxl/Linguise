package de.c4vxl.app.lib.element.modal;

import de.c4vxl.app.Theme;
import de.c4vxl.app.lib.component.Elements;
import de.c4vxl.app.lib.component.Window;
import de.c4vxl.app.util.Factory;
import de.c4vxl.app.util.Resource;

import javax.swing.*;

public class ProgressbarWindow extends Window {
    public JPanel progressBar = new Factory<>(new JPanel())
            .background(Theme.current.accent)
            .size(0, 10)
            .get();

    public JLabel label;

    public ProgressbarWindow(String title) {
        super(title, 500, 200);

        this.undecorated()
                .borderRadius(12)
                .background(Theme.current.background)
                .layout(null);

        this.add(new Factory<>(Elements.iconButton(Resource.loadIcon("media/Logo large.png", 300, Theme.current.accent)))
                .centerX(this).posY(40).get());

        label = new Factory<>(Elements.text(title, -1))
                .centerX(this).posY(110)
                .get();

        this.add(label);

        this.progressBar.setLocation(10, 140 + 20 + 10);
        this.add(this.progressBar);
    }

    /**
     * Set the label
     * @param label The new label
     */
    public ProgressbarWindow setLabel(String label) {
        this.label.setText(label);
        this.label.setSize(this.label.getPreferredSize());
        new Factory<>(this.label).centerX(this);
        return this;
    }

    /**
     * Set the value of the progressbar
     * @param percent The percentage
     */
    public ProgressbarWindow setValue(int percent) {
        this.progressBar.setSize(((this.getWidth() - 20) / 100) * percent, this.progressBar.getHeight());
        return this;
    }

    public static void main(String[] args) {
        Theme.current = Theme.fromFile("appdata/themes/moonlight.theme");
        new ProgressbarWindow("hey")
                .setValue(25)
                .open();
    }
}