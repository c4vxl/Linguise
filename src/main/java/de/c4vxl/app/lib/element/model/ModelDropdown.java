package de.c4vxl.app.lib.element.model;

import de.c4vxl.app.Theme;
import de.c4vxl.app.config.Config;
import de.c4vxl.app.language.Language;
import de.c4vxl.app.lib.component.Dropdown;
import de.c4vxl.app.lib.component.Elements;
import de.c4vxl.app.model.Model;
import de.c4vxl.app.util.Factory;
import de.c4vxl.app.util.TextUtils;

import javax.swing.*;
import java.util.Arrays;
import java.util.Optional;

public class ModelDropdown extends Dropdown {
    public ModelDropdown() {
        super(Model.current == null ? Language.current.get("chat.model.dropdown.title.no_model") : Model.current.name, 400, 50);

        this.expandedTitle = Language.current.get("chat.model.dropdown.expanded");

        new Factory<>(this).registerKeyboardShortcut("action_model_dropdown_toggle", "control M", () -> {
            if (this.isExpanded()) this.collapse();
            else this.expand();
        });

        this.reload();
    }

    public JPanel[] getElements() {
        return Arrays.stream(Config.getLocalModels()).map(model ->
                createDefaultItem(TextUtils.cutString(model.name, "...", Theme.current.font, getWidth() - 100), () -> {
                    Model.current = model;
                    Config.setConfigValue("app.model", Model.current.path.replace(Config.MODELS_DIRECTORY + "/", ""));
                    System.out.println("[ACTION]: Switch to model: " + model.name);
                    this.setTitle(model.name);
                    reload();
                }, false)).toArray(JPanel[]::new);
    }

    public void reload() {
        JPanel[] elements = this.getElements();
        this.container.removeAll();

        if (elements.length != 0)
            for (JPanel element : elements)
                this.addItem(element);
        else
            this.addItem(createDefaultItem(TextUtils.cutString(
                    Language.current.get("chat.model.dropdown.no_models"), "...", Theme.current.font, getWidth() - 100
            ), () -> {}, true));
    }
}