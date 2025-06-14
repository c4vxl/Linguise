package de.c4vxl.app.util;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Consumer;

public class Factory<T extends JComponent> {
    private final T element;

    public Factory(T element) {
        this.element = element;
    }

    /**
     * Returns the final element
     */
    public T get() { return this.element; }

    /**
     * Invoke a method on an element
     * @param method The method to invoke
     * @param args The arguments to the method
     */
    public Factory<T> invoke(String method, Object... args) {
        try {
            Method m = this.element.getClass().getMethod(method, Arrays.stream(args).map(Object::getClass).toArray(Class[]::new));
            m.invoke(this.element, args);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    /**
     * Apply an action to the factories element
     * @param action The action to apply to the element
     */
    public Factory<T> apply(Consumer<T> action) {
        action.accept(this.element);
        return this;
    }

    /**
     * Set the cursor
     * @param cursor The cursor
     */
    @SuppressWarnings("MagicConstant")
    public Factory<T> cursor(int cursor) { return cursor(Cursor.getPredefinedCursor(cursor)); }

    /**
     * Set the cursor
     * @param cursor The cursor
     */
    public Factory<T> cursor(Cursor cursor) { return apply(e -> e.setCursor(cursor)); }

    /**
     * Set the background color of the element
     * @param color The color
     */
    public Factory<T> background(Color color) { return apply(e -> e.setBackground(color)); }

    /**
     * Set the foreground color of the element
     * @param color The color
     */
    public Factory<T> foreground(Color color) { return apply(e -> e.setForeground(color)); }

    /**
     * Set the border
     * @param border The border
     */
    public Factory<T> border(Border border) { return apply(e -> e.setBorder(border)); }

    /**
     * Set the font
     * @param font The font
     */
    public Factory<T> font(Font font) { return apply(e -> e.setFont(font)); }

    /**
     * Set the opacity of the element
     * @param val The value
     */
    public Factory<T> opaque(boolean val) { return apply(e -> e.setOpaque(val)); }

    /**
     * Set the layout of the element
     * @param layout The layout manager
     */
    public Factory<T> layout(LayoutManager layout) { return apply(e -> e.setLayout(layout)); }

    /**
     * Set the position of the element
     * @param x The x coordinate
     * @param y The y coordinate
     */
    public Factory<T> pos(int x, int y) { return apply(e -> e.setLocation(x, y)); }

    /**
     * Set the position of the element
     * @param x The x coordinate
     */
    public Factory<T> posX(int x) { return apply(e -> e.setLocation(x, e.getY())); }

    /**
     * Set the position of the element
     * @param y The y coordinate
     */
    public Factory<T> posY(int y) { return apply(e -> e.setLocation(e.getX(), y)); }

    /**
     * Center the current element in its parent on both axes
     * @param parent The parent
     */
    public Factory<T> center(Component parent) {
        centerY(parent);
        return centerX(parent);
    }

    /**
     * Center the current element in its parent on the x-axis
     * @param parent The parent
     */
    public Factory<T> centerX(Component parent) { return apply(e -> e.setLocation((parent.getWidth() - e.getWidth()) / 2, e.getY())); }

    /**
     * Center the current element in its parent on the y-axis
     * @param parent The parent
     */
    public Factory<T> centerY(Component parent) { return apply(e -> e.setLocation(e.getX(), (parent.getHeight() - e.getHeight()) / 2)); }

    /**
     * Set the size of the element
     * @param width The width
     * @param height The height
     */
    public Factory<T> size(int width, int height) { return this.size(new Dimension(width, height)); }

    /**
     * Set the size of the element
     * @param size The size
     */
    public Factory<T> size(Dimension size) { return apply(e -> {
        e.setSize(size);
        e.setPreferredSize(e.getSize());
        e.setMaximumSize(e.getSize());
        e.setMinimumSize(e.getSize());
    }); }

    /**
     * Set the size to the preferred size
     */
    public Factory<T> toPreferredSize() {
        return size(element.getPreferredSize().width, element.getPreferredSize().height);
    }

    /**
     * Add a click action
     * @param action The action
     */
    public Factory<T> onClick(Runnable action) {
        return apply(e -> e.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { action.run(); }
        }));
    }

    /**
     * Add a hover-enter action
     * @param action The action
     */
    public Factory<T> onHoverEnter(Runnable action) {
        return apply(e -> e.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { action.run(); }
        }));
    }

    /**
     * Add a hover-leave action
     * @param action The action
     */
    public Factory<T> onHoverLeave(Runnable action) {
        return apply(e -> e.addMouseListener(new MouseAdapter() {
            @Override public void mouseExited(MouseEvent e) {
                // Check if element has actually been left
                // Hovering child elements should count as still hovering the actual element!
                Point mousePos = MouseInfo.getPointerInfo().getLocation();
                SwingUtilities.convertPointFromScreen(mousePos, element);
                if (element.contains(mousePos)) return;

                action.run();
            }
        }));
    }

    /**
     * Registers a Keyboard shortcut for the element
     * @param name The name of the shortcut
     * @param keys The keyboard strokes
     * @param onAction The action to happen when pressed
     */
    public Factory<T> registerKeyboardShortcut(String name, String keys, Runnable onAction) {
        this.element.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(keys), name);
        this.element.getActionMap().put(name, new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { onAction.run(); }
        });
        return this;
    }

    /**
     * Creates a hover animation
     * @param normal The default color
     * @param hover The background color when hovered
     * @param isActive If true: normal and hover will be swapped
     */
    public Factory<T> hoverAnimation(Color normal, Color hover, boolean isActive) {
        Color h = isActive ? normal : hover;
        Color n = !isActive ? normal : hover;

        element.setBackground(n);
        onHoverEnter(() -> element.setBackground(h));
        onHoverLeave(() -> element.setBackground(n));
        return this;
    }
}