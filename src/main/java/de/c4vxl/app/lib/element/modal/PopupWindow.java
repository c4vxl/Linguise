package de.c4vxl.app.lib.element.modal;

import de.c4vxl.app.lib.component.Elements;
import de.c4vxl.app.lib.component.Window;
import de.c4vxl.app.theme.Theme;
import de.c4vxl.app.util.Factory;

public class PopupWindow extends Window {
    public PopupWindow(String title, String label) {
        super(title, 500, 200);

        this.undecorated()
                .borderRadius(12)
                .background(Theme.current.background)
                .layout(null);

        this.add(new Factory<>(Elements.text(label, -1))
                        .center(this)
                .get());
    }
}