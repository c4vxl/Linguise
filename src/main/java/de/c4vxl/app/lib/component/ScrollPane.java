package de.c4vxl.app.lib.component;

import javax.swing.*;
import java.awt.*;

public class ScrollPane extends JScrollPane {
    public JScrollBar verticalScrollBar = this.getVerticalScrollBar(), horizontalScrollBar = this.getHorizontalScrollBar();
    public de.c4vxl.app.lib.component.ScrollBar vertUI = new de.c4vxl.app.lib.component.ScrollBar(),
                                                horUI = new de.c4vxl.app.lib.component.ScrollBar();

    public ScrollPane(Component view) {
        super(view);

        this.setBorder(null);
        this.getViewport().setOpaque(false);
        this.setOpaque(false);

        verticalScrollBar.setUI(vertUI);
        verticalScrollBar.setUnitIncrement(10);
        verticalScrollBar.setBlockIncrement(50);
        verticalScrollBar.setAutoscrolls(true);
        horizontalScrollBar.setUI(horUI);
        horizontalScrollBar.setUnitIncrement(10);
        horizontalScrollBar.setBlockIncrement(50);
        horizontalScrollBar.setAutoscrolls(true);
    }

    /**
     * Scroll to the very top of the panel
     */
    public ScrollPane scrollToTop() {
        verticalScrollBar.setValue(verticalScrollBar.getMinimum());
        this.repaint();
        return this;
    }

    /**
     * Scroll to the very bottom of the page
     */
    public ScrollPane scrollToBottom() {
        verticalScrollBar.setValue(verticalScrollBar.getMaximum());
        this.repaint();
        return this;
    }

    /**
     * Scroll to the very end of the page (right)
     */
    public ScrollPane scrollToEnd() {
        horizontalScrollBar.setValue(horizontalScrollBar.getMaximum());
        this.repaint();
        return this;
    }

    /**
     * Scroll to the very start of the page (left)
     */
    public ScrollPane scrollToStart() {
        horizontalScrollBar.setValue(horizontalScrollBar.getMinimum());
        this.repaint();
        return this;
    }
}