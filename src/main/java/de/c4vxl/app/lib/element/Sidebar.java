package de.c4vxl.app.lib.element;

import de.c4vxl.app.Theme;
import de.c4vxl.app.lib.component.HR;
import de.c4vxl.app.util.Elements;
import de.c4vxl.app.util.Resource;

import javax.swing.*;

public class Sidebar extends JPanel {
    public Sidebar() {
        this.setSize(300, 800);
        this.setPreferredSize(this.getSize());
        this.setLocation(-300, 0);
        this.setLayout(null);
        this.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Theme.current.background_1));
        this.setBackground(Theme.current.background);

        JLabel logo = new JLabel(Resource.loadIcon("Logo large.png", 200));
        logo.setSize(logo.getPreferredSize());
        logo.setLocation((getWidth() - logo.getWidth()) / 2, 30);
        this.add(logo);

        this.add(
                new HR(getWidth() - 140, 1, Theme.current.text)
                        .position(70, 80)
        );


        JLabel historyLabel = new JLabel("<html><body style='font-family: Inter; font-size: 130%; font-weight: 700'>History</body></html>");
        historyLabel.setForeground(Theme.current.text_1);
        historyLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 5, 0, 0, Theme.current.text_1),
                BorderFactory.createEmptyBorder(0, 3, 0, 0)
        ));
        historyLabel.setSize(historyLabel.getPreferredSize());
        historyLabel.setLocation(20, 120);
        this.add(historyLabel);
    }

    @Override
    public boolean contains(int x, int y) {
        return false;
    }
}