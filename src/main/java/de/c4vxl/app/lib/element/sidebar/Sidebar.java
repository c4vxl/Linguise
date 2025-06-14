package de.c4vxl.app.lib.element.sidebar;

import de.c4vxl.app.Theme;
import de.c4vxl.app.lib.component.Line;
import de.c4vxl.app.lib.component.RoundedPanel;
import de.c4vxl.app.lib.component.ScrollPane;
import de.c4vxl.app.lib.component.Elements;
import de.c4vxl.app.util.Factory;
import de.c4vxl.app.util.Resource;
import de.c4vxl.app.util.TextUtils;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Sidebar extends JPanel {
    public JPanel history;
    public ScrollPane hPane;
    public ArrayList<String> histories = new ArrayList<>(List.of(
            "What is a giraffe?",
            "Who is peter griffin?",
            "What da dog doin?",
            "Why is the sky blue?",
            "How do birds fly?",
            "Can a penguin ride a bicycle?",
            "What happens if you microwave metal?",
            "Who let the dogs out?",
            "Is cereal a soup?",
            "Why do cats knock things over?",
            "Do fish get thirsty?",
            "Can you sneeze with your eyes open?",
            "Why is yawning contagious?",
            "What if the moon was made of cheese?",
            "Can plants hear music?"
    ));

    public Sidebar() {
        this.setSize(300, 800);
        this.setPreferredSize(this.getSize());
        this.setLocation(-300, 0);
        this.setLayout(null);
        this.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 0, 1, Theme.current.background_1),
                        BorderFactory.createEmptyBorder(15, 0, 0, 0)
                )
        );
        this.setBackground(Theme.current.background);

        // logo
        this.add(new Factory<>(Elements.iconButton(Resource.loadIcon("media/Logo large.png", 200, Theme.current.accent)))
                .posY(30).centerX(this).get());

        // hr underneath logo
        this.add(new Line(getWidth() - 140, 1, Theme.current.text).position(70, 80));

        // history label
        this.add(new Factory<>(Elements.text("Chat History", -1))
                .size(999, 20)
                .pos(20, 120).foreground(Theme.current.text_1)
                .border(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 4, 0, 0, Theme.current.text_1),
                        BorderFactory.createEmptyBorder(0, 5, 0, 0)
                )).font(Theme.current.font.deriveFont(Font.BOLD)).get());


        this.history = new Factory<>(new JPanel()).size(getWidth() - 40, getHeight() - 160).pos(10, 150)
                .opaque(false).get();

        this.hPane = new ScrollPane(this.history);
        this.hPane.setBounds(this.history.getX(), this.history.getY(), this.getWidth() - 20, this.history.getHeight());
        this.add(this.hPane);

        reload();
    }

    /**
     * Reload the item list
     */
    public void reload() {
        boolean isSelected = true;
        for (String name : histories) {
            this.history.add(createEntry(name, isSelected));
            isSelected = false;
        }

        this.history.setPreferredSize(new Dimension(this.history.getWidth(), 55 * this.histories.size()));

        this.repaint();
        this.revalidate();
    }

    public JPanel createEntry(String name, boolean isSelected) {
        JPanel panel = new Factory<>(new RoundedPanel(10))
                .size(this.history.getWidth(), 50)
                .hoverAnimation(isSelected ? Theme.current.background_1 : Theme.current.background, isSelected ? Theme.current.background_1 : Theme.current.background_3, false)
                .layout(null).cursor(Cursor.HAND_CURSOR)
                .border(BorderFactory.createEmptyBorder(0, 10, 0, 0))
                .get();

        JLabel moreButton = new Factory<>(Elements.iconButton(Resource.loadIcon("media/more.png", 20, Theme.current.text_1)))
                .centerY(panel).posX(panel.getWidth() - 20 - 5).cursor(Cursor.HAND_CURSOR)
                .get();

        String normal = TextUtils.cutString(name, "...", Theme.current.font, panel.getWidth());
        String cut = TextUtils.cutString(name, "...", Theme.current.font, panel.getWidth() - 40);
        JLabel label = new Factory<>(Elements.text(isSelected ? cut : normal, -1)).centerY(panel).posX(5).get();

        new Factory<>(panel).onHoverEnter(() -> {
            label.setText(cut);
            panel.add(moreButton);
            panel.repaint();
        }).onHoverLeave(() -> {
            label.setText(normal);
            panel.remove(moreButton);
            panel.repaint();
        });

        panel.add(label);
        return panel;
    }
}