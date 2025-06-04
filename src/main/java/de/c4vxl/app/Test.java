package de.c4vxl.app;

import de.c4vxl.app.lib.component.Dropdown;
import de.c4vxl.app.lib.component.Window;

public class Test extends Window {
    public Test() {
        super("Testing", 1000, 800);
        Theme.current = Theme.standard;
        this.background(Theme.current.background);
        this.layout(null);

        Dropdown dropdown = new Dropdown("Testing");
        dropdown.setLocation((getWidth() - dropdown.getWidth()) / 2, (getHeight() - dropdown.getHeight()) / 2);

        dropdown.addOption("Hey", null, (String) -> {

        });

        dropdown.addOption("Hey", null, (String) -> {

        });

        this.add(dropdown);
    }

    public static void main(String[] args) {
        new Test().open();
    }
}
