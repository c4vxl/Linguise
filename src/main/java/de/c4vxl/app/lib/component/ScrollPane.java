package de.c4vxl.app.lib.component;

import javax.swing.*;
import java.awt.*;

public class ScrollPane extends JScrollPane {
    private JScrollBar verticalScrollBar = this.getVerticalScrollBar(), horizontalScrollBar = this.getHorizontalScrollBar();

    public ScrollPane(Component view) {
        super(view);

        this.setBorder(null);
        this.getViewport().setOpaque(false);
        this.setOpaque(false);

        verticalScrollBar.setUI(new de.c4vxl.app.lib.component.ScrollBar());
        verticalScrollBar.setUnitIncrement(10);
        verticalScrollBar.setBlockIncrement(50);
        verticalScrollBar.setAutoscrolls(true);
        horizontalScrollBar.setUI(new de.c4vxl.app.lib.component.ScrollBar());
        horizontalScrollBar.setUnitIncrement(10);
        horizontalScrollBar.setBlockIncrement(50);
        horizontalScrollBar.setAutoscrolls(true);
    }

    /**
     * Scroll to the very top of the panel
     */
    public ScrollPane scrollToTop() {
        horizontalScrollBar.setValue(horizontalScrollBar.getMinimum());
        this.repaint();
        return this;
    }

    /**
     * Scroll to the very bottom of the page
     */
    public ScrollPane scrollToBottom() {
        horizontalScrollBar.setValue(horizontalScrollBar.getMaximum());
        this.repaint();
        return this;
    }

    /**
     * Scroll to the very end of the page (right)
     */
    public ScrollPane scrollToEnd() {
        verticalScrollBar.setValue(verticalScrollBar.getMaximum());
        this.repaint();
        return this;
    }

    /**
     * Scroll to the very start of the page (left)
     */
    public ScrollPane scrollToStart() {
        verticalScrollBar.setValue(verticalScrollBar.getMinimum());
        this.repaint();
        return this;
    }
}