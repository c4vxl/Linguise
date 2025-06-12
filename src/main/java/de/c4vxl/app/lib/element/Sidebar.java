package de.c4vxl.app.lib.element;

import de.c4vxl.app.Theme;
import de.c4vxl.app.lib.component.HR;
import de.c4vxl.app.util.Elements;
import de.c4vxl.app.util.Factory;
import de.c4vxl.app.util.Resource;

import javax.swing.*;
import java.awt.*;

public class Sidebar extends JPanel {
    public Sidebar() {
        this.setSize(300, 800);
        this.setPreferredSize(this.getSize());
        this.setLocation(-300, 0);
        this.setLayout(null);
        this.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Theme.current.background_1));
        this.setBackground(Theme.current.background);

        // logo
        this.add(new Factory<>(Elements.iconButton(Resource.loadIcon("Logo large.png", 200)))
                .posY(30).centerX(this).get());

        // hr underneath logo
        this.add(new HR(getWidth() - 140, 1, Theme.current.text).position(70, 80));

        // history label
        this.add(new Factory<>(Elements.text("Chat History", -1))
                .size(999, 20)
                .pos(20, 120).foreground(Theme.current.text_1)
                .border(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 4, 0, 0, Theme.current.text_1),
                        BorderFactory.createEmptyBorder(0, 5, 0, 0)
                )).font(Theme.current.font.deriveFont(Font.BOLD)).get());
    }

    @Override
    public boolean contains(int x, int y) {
        return false;
    }
}