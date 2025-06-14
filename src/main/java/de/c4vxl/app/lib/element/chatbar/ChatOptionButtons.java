package de.c4vxl.app.lib.element.chatbar;

import de.c4vxl.app.Theme;
import de.c4vxl.app.lib.component.Button;
import de.c4vxl.app.lib.component.Elements;
import de.c4vxl.app.util.Factory;
import de.c4vxl.app.util.Resource;

import javax.swing.*;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.Collections;

public class ChatOptionButtons extends JPanel {
    public ChatOptionButtons() {
        this.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));
        this.setOpaque(false);

        this.add(createButton("media/reload.png", "Regenerate", () -> {
            System.out.println("Regenerate");
        }));

        this.add(createButton("media/share.png", "Share", () -> {
            System.out.println("Share");
        }));

        this.setSize(this.getPreferredSize());
    }

    private Button createButton(String icon, String label, Runnable l) {
        return new Factory<>(Elements.hollowButton().withLabel(label).withIcon(Resource.loadIcon(icon, 20, Theme.current.accent)).withIconTextGap(10))
                .font(Theme.current.font_2.deriveFont(Collections.singletonMap(TextAttribute.WEIGHT, TextAttribute.WEIGHT_MEDIUM)))
                .onClick(l).get();
    }
}
