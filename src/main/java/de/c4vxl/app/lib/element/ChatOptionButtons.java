package de.c4vxl.app.lib.element;

import de.c4vxl.app.Theme;
import de.c4vxl.app.lib.component.Button;
import de.c4vxl.app.util.Resource;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.font.TextAttribute;
import java.util.Collections;
import java.util.function.Consumer;

public class ChatOptionButtons extends JPanel {
    public ChatOptionButtons() {
        this.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));
        this.setOpaque(false);

        this.add(createButton("reload_p.png", "Regenerate", (event) -> {
            System.out.println("Regenerate");
        }));

        this.add(createButton("share_p.png", "Share", (event) -> {
            System.out.println("Share");
        }));

        this.setSize(this.getPreferredSize());
    }

    private Button createButton(String icon, String label, ActionListener l) {
        Button button = new Button()
                .size(200, 40)
                .withLabel(label)
                .withIcon(Resource.loadIcon(icon, 20))
                .withIconTextGap(10)
                .foreground(Theme.current.accent)
                .font(Theme.current.font_2.deriveFont(Collections.singletonMap(TextAttribute.WEIGHT, TextAttribute.WEIGHT_MEDIUM)))
                .withBorderRadius(10)
                .borderStyle();

        button.addActionListener(l);

        return button;
    }
}
