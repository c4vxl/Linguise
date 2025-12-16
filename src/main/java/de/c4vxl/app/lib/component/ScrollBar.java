package de.c4vxl.app.lib.component;

import de.c4vxl.app.theme.Theme;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

public class ScrollBar extends BasicScrollBarUI {
    public int GAP = 10;
    public Color _thumbColor = Theme.current.background_3;
    public Color _trackColor = Theme.current.background;

    @Override
    protected void configureScrollBarColors() {
        this.thumbColor = this._thumbColor;
        this.trackColor = this._trackColor;
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return createZeroButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return createZeroButton();
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        if (scrollbar.getOrientation() == JScrollBar.VERTICAL) {
            return new Dimension(3 + GAP, super.getPreferredSize(c).height);
        } else {
            return new Dimension(super.getPreferredSize(c).width, 3 + GAP);
        }
    }


    private JButton createZeroButton() {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(0, 0));
        button.setMinimumSize(new Dimension(0, 0));
        button.setMaximumSize(new Dimension(0, 0));
        button.setVisible(false);
        return button;
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        if (!scrollbar.isEnabled() || thumbBounds.width > thumbBounds.height && scrollbar.getOrientation() == JScrollBar.VERTICAL)
            return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color thumbColor = Theme.current.accent;

        g2.setPaint(thumbColor);
        g2.fillRoundRect(thumbBounds.x + GAP, thumbBounds.y, thumbBounds.width, thumbBounds.height, 6, 6);
        g2.dispose();
    }
}