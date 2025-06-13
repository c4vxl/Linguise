package de.c4vxl.app.lib.component;

import de.c4vxl.app.Theme;
import de.c4vxl.app.util.Resource;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

@SuppressWarnings("deprecation")
public class Window extends JFrame {
    public Window(String title, int width, int height) {
        this.setTitle(title);
        this.setSize(width, height);
        this.setPreferredSize(this.getPreferredSize());
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null); // open in center of screen
    }

    public static JLabel _create_top_bar_button(ImageIcon icon, String tooltip, Runnable onClick) {
        JLabel jl = new JLabel(null, icon, JLabel.CENTER) {
            @Override
            public JToolTip createToolTip() {
                Tooltip tip = new Tooltip(Theme.current.text, Theme.current.background_1);
                tip.setComponent(this);
                return tip;
            }
        };
        jl.setToolTipText(tooltip);
        jl.setSize(jl.getPreferredSize());
        jl.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { onClick.run(); }
        });
        jl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        return jl;
    }

    /**
     * Adds a close and minimize button to the top right of the window
     */
    public Window withButtons() {
        JPanel buttonPanel = new JPanel();

        buttonPanel.setLayout(new GridLayout(1, 2, 10, 10));

        JLabel minim = _create_top_bar_button(Resource.loadIcon("media/minus.png", 20), "Minumize", () -> this.setState(JFrame.ICONIFIED));
        JLabel close = _create_top_bar_button(Resource.loadIcon("media/cross.png", 15), "Close", this::close);

        buttonPanel.add(minim);
        buttonPanel.add(close);

        buttonPanel.setOpaque(false);
        buttonPanel.setBounds((getWidth() - 60), 0, 50, 30);

        this.add(buttonPanel);

        return this;
    }

    /**
     * Registers a Keyboard shortcut for this Window
     * @param name The name of the shortcut
     * @param keys The keyboard strokes
     * @param onAction The action to happen when pressed
     */
    public void registerKeyboardShortcut(String name, String keys, Runnable onAction) {
        this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(keys), name);
        this.getRootPane().getActionMap().put(name, new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { onAction.run(); }
        });
    }

    /**
     * Set the background color of the window
     * @param color The color
     */
    public Window background(Color color) {
        this.setBackground(color);
        this.getContentPane().setBackground(color);
        return this;
    }

    /**
     * Set the layout of the window
     */
    public Window layout(LayoutManager layout) {
        this.setLayout(layout);
        return this;
    }

    /**
     * Set the border radius of the window
     * @param arcw Radius width
     * @param arch Radius height
     */
    public Window borderRadius(double arcw, double arch) {
        this.undecorated().setShape(new RoundRectangle2D.Double(0, 0, this.getWidth(), this.getHeight(), arcw, arch));
        return this;
    }

    /**
     * Set the border radius of the window
     * @param radius The radius
     */
    public Window borderRadius(double radius) { return this.borderRadius(radius, radius); }

    /**
     * Remove the title bar from the window
     */
    public Window undecorated() {
        this.setUndecorated(true);

        // custom movement handler
        final int[] x = new int[1], y = new int[1];
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                x[0] = e.getX();
                y[0] = e.getY();

                setCursor(Cursor.HAND_CURSOR);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                setCursor(Cursor.DEFAULT_CURSOR);
            }
        });

        this.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                setLocation(
                        getLocation().x + e.getX() - x[0],
                        getLocation().y + e.getY() - y[0]
                );
            }
        });

        return this;
    }

    /**
     * Open the window
     */
    public Window open() {
        this.setVisible(true);
        return this;
    }

    /**
     * Close the window
     */
    public Window close() {
        this.setVisible(false);
        this.dispose();
        return this;
    }
}
