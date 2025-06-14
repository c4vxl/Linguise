package de.c4vxl.app.lib.element.model;

import de.c4vxl.app.Theme;
import de.c4vxl.app.config.Config;
import de.c4vxl.app.language.Language;
import de.c4vxl.app.lib.component.Dropdown;
import de.c4vxl.app.model.Model;
import de.c4vxl.app.util.TextUtils;

import javax.swing.*;
import java.util.Arrays;
import java.util.stream.Stream;

public class ModelDropdown extends Dropdown {
    public ModelDropdown() {
        super(Model.current.name, 400, 50);

        this.expandedTitle = Language.current.get("chat.model.dropdown.expanded");

        this.reload();
    }

    public JPanel[] getElements() {
        return Stream.concat(Stream.of(Model.fakeModel), Arrays.stream(Config.getLocalModels())).map(model ->
                createDefaultItem(TextUtils.cutString(model.name, "...", Theme.current.font, getWidth() - 100), () -> {
                    Model.current = model;
                    System.out.println("[ACTION]: Switch to model: " + model.name);
                    this.setTitle(model.name);
                    reload();
                }, false)).toArray(JPanel[]::new);
    }

    public void reload() {
        JPanel[] elements = this.getElements();
        this.container.removeAll();
        for (JPanel element : elements) {
            this.addItem(element);
        }
    }
}