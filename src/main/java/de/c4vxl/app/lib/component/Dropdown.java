package de.c4vxl.app.lib.component;

import de.c4vxl.app.Theme;
import de.c4vxl.app.util.Factory;
import de.c4vxl.app.util.Resource;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Objects;

public class Dropdown extends RoundedPanel {
    private JLabel titleLabel = new JLabel();
    public Line sep;
    public JPanel container = new JPanel();
    public ScrollPane containerPane = new ScrollPane(this.container);
    private Component sep_gap_1, sep_gap_2;
    public int padding, gap, width, height, max_height;
    private boolean isExpanded = false;
    public String title, expandedTitle;

    /**
     * Checks if the content panel is visible
     */
    public boolean isExpanded() {
        return Arrays.stream(this.getComponents())
                .anyMatch(c -> c.equals(this.containerPane));
    }

    /**
     * Create a new Dropdown
     * @param title The title of the closed dropdown
     * @param width The width of the dropdown
     * @param height The height of the closed dropdown
     */
    public Dropdown(String title, int width, int height) {
        this(title, width, height, 400, 10, 5, Theme.current.background_1, Theme.current.text);
    }

    /**
     * Create a new Dropdown
     * @param title The title of the closed dropdown
     * @param width The width of the dropdown
     * @param height The height of the closed dropdown
     * @param max_height The max height of the dropdowns content
     * @param padding The padding of the dropdown
     * @param gap The gap in between two items
     * @param background The background color of the dropdown
     * @param text The color of text
     */
    public Dropdown(String title, int width, int height, int max_height, int padding, int gap, Color background, Color text) {
        super(10);

        this.title = title;
        this.expandedTitle = title;
        this.padding = padding;
        this.gap = gap;
        this.width = width;
        this.height = height;
        this.max_height = max_height;


        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(background);
        this.setBorder(BorderFactory.createCompoundBorder(
                null,
                BorderFactory.createEmptyBorder(padding, padding, padding, padding)
        ));

        this.sep_gap_1 = Box.createRigidArea(new Dimension(getWidth(), 10));
        this.sep = new Line(-1, 1, text);
        this.sep_gap_2 = Box.createRigidArea(new Dimension(getWidth(), 30));

        this.setTitle(title);
        this.titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.titleLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
        this.titleLabel.setSize(width, height);
        this.titleLabel.setPreferredSize(this.titleLabel.getSize());
        this.titleLabel.setForeground(text);
        this.add(this.titleLabel);

        this.container.setOpaque(false);
        this.container.setLayout(new BoxLayout(this.container, BoxLayout.Y_AXIS));
        this.container.setSize(width - padding * 2, 0);

        this.containerPane.vertUI.GAP = 0;
        this.containerPane.setMaximumSize(new Dimension(width - padding * 2, max_height));
        this.containerPane.horizontalScrollBar.setPreferredSize(new Dimension(0, 0));

        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isExpanded)
                    collapse();
                else
                    expand();

                isExpanded = !isExpanded;
            }
        });

        Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
            if (!(event instanceof MouseEvent e) || e.getID() != MouseEvent.MOUSE_PRESSED) return;

            Component clicked = SwingUtilities.getDeepestComponentAt(e.getComponent(), e.getX(), e.getY());
            if (isExpanded && !SwingUtilities.isDescendingFrom(clicked, Dropdown.this)) {
                collapse();
                isExpanded = false;
            }
        }, AWTEvent.MOUSE_EVENT_MASK);

        this.collapse();
    }

    /**
     * Creates a normal item to add to a dropdown
     * @param label The label on the item
     * @param isHighlighted Should the item be highlighted
     * @param onClick The action to be executed when clicked
     */
    public static JPanel createDefaultItem(String label, Runnable onClick, boolean isHighlighted) {
        JPanel panel = new Factory<>(new RoundedPanel(14))
                .layout(new FlowLayout(FlowLayout.CENTER))
                .size(0, 50)
                .border(BorderFactory.createEmptyBorder(5, 15, 5, 15))
                .cursor(Cursor.HAND_CURSOR)
                .opaque(false)
                .hoverAnimation(isHighlighted ? Theme.current.background : new Color(0, 0, 0, 0), Theme.current.background, false)
                .onClick(onClick)
                .get();
        panel.setMaximumSize(null);
        panel.add(Elements.text(label, 0));

        return panel;
    }

    /**
     * Set the title of the dropdown
     * @param title Set the title
     */
    public Dropdown setTitle(String title) {
        if (!Objects.equals(title, this.title) && !Objects.equals(title, this.expandedTitle))
            this.title = title;

        this.titleLabel.setText(title);
        return this;
    }

    /**
     * Add an item to the list
     * @param component The component to add
     */
    public Dropdown addItem(JComponent component) {
        this.container.add(component);
        this.container.add(Box.createRigidArea(new Dimension(getWidth(), gap)));
        this.repaint();
        this.revalidate();
        return this;
    }

    /**
     * Remove an item from the list
     * @param component The component to remove
     */
    public Dropdown removeItem(JComponent component) {
        // Remove next padding element
        Component[] comps = this.container.getComponents();
        int index = -1;
        for (int i = 0; i < comps.length; i++)
            if (Objects.equals(comps[i], component))
                index = i;

        if (index + 1 < comps.length && this.container.getComponent(index + 1) instanceof Box.Filler filler)
            this.container.remove(filler);

        this.container.remove(component);
        this.repaint();
        this.revalidate();
        return this;
    }

    /**
     * Collapse the dropdown menu
     */
    public Dropdown collapse() {
        this.remove(this.sep);
        this.remove(this.containerPane);
        this.remove(this.sep_gap_1);
        this.remove(this.sep_gap_2);
        this.setPreferredSize(new Dimension(this.width, this.height));
        this.setSize(this.getPreferredSize());
        this.titleLabel.setSize(this.getPreferredSize());

        titleLabel.setIcon(Resource.loadIcon("media/dropdown_c.png", 20, Theme.current.text_1));
        setTitle(this.title);
        SwingUtilities.invokeLater(() -> {
            this.repaint();
            this.revalidate();
        });

        return this;
    }

    /**
     * Expand the dropdown menu
     */
    public Dropdown expand() {
        int containerHeight = Arrays.stream(container.getComponents()).map(x -> x.getPreferredSize().height).reduce(Integer::sum).orElse(50);
        this.container.setPreferredSize(new Dimension(width - padding * 2, containerHeight));
        this.container.setMaximumSize(null);

        this.add(this.sep_gap_1);
        this.add(this.sep);
        this.add(this.sep_gap_2);
        this.add(this.containerPane);

        this.setPreferredSize(new Dimension(width, this.getPreferredSize().height));
        this.setSize(this.getPreferredSize());
        int expandedHeight = titleLabel.getPreferredSize().height
                + sep_gap_1.getPreferredSize().height
                + sep.getPreferredSize().height
                + sep_gap_2.getPreferredSize().height
                + containerPane.getPreferredSize().height
                + padding * 2;

        this.setPreferredSize(new Dimension(width, expandedHeight));
        this.setSize(this.getPreferredSize());

        titleLabel.setIcon(Resource.loadIcon("media/dropdown_e.png", 20, Theme.current.text_1));
        setTitle(this.expandedTitle);
        SwingUtilities.invokeLater(() -> {
            this.repaint();
            this.revalidate();
        });

        return this;
    }
}