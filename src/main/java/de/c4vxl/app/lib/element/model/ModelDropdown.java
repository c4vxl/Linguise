package de.c4vxl.app.lib.element.model;

import de.c4vxl.app.lib.component.Dropdown;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ModelDropdown extends Dropdown {
    public String current;
    public ArrayList<String> models = new ArrayList<>();

    public ModelDropdown() {
        super("Base model", 400, 50);

        this.expandedTitle = "Select a model";
        this.current = title;

        models.add(current);
        models.add("Another one1");
        models.add("Another one2");
        models.add("Another one3");
        models.add("Another one4");
        models.add("Another one5");
        models.add("Another one6");
        models.add("Another one7");

        this.reload();
    }

    public JPanel[] getElements() {
        return models.stream().map(name -> createDefaultItem(name, () -> {
            this.current = name;
            System.out.println("Switch to model: " + current);
            this.setTitle(current);
            reload();
        }, name.equals(current))).toArray(JPanel[]::new);
    }

    public void reload() {
        JPanel[] elements = this.getElements();
        this.container.removeAll();
        for (JPanel element : elements) {
            this.container.add(element);
            this.container.add(Box.createRigidArea(new Dimension(getWidth(), 10)));
        }
    }
}