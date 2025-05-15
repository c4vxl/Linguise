package de.c4vxl.app;

import de.c4vxl.app.lib.component.Window;
import de.c4vxl.app.lib.element.ChatBar;
import de.c4vxl.app.lib.element.MessagePrompt;
import de.c4vxl.app.lib.element.MessageResponse;
import de.c4vxl.app.lib.element.Sidebar;
import de.c4vxl.app.util.AnimationUtils;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class App extends Window {
    public Theme theme;

    public JPanel content = new JPanel();
    public final ChatBar chatBar;
    public final Sidebar sidebar;

    public App() { this(Theme.standard); }
    public App(Theme theme) {
        super("Linguise", 1200, 800);
        this.theme = theme;
        Theme.current = theme;

        this    .background(theme.background)
                .borderRadius(20)
                .layout(null);


        content.setLayout(null);
        content.setBounds((1200 - 890) / 2, 0, 890, getHeight());
        content.setOpaque(false);
        this.add(content);

        this.chatBar = new ChatBar(this, System.out::println);
        this.content.add(this.chatBar);

        JLabel notice = new JLabel("<html><body style='font-family: Inter; font-weight: 100'>Linguise can make mistakes. <b>Consider checking important information!</b></body></html>");
        notice.setSize(notice.getPreferredSize());
        notice.setLocation((content.getWidth() - notice.getWidth()) / 2, getHeight() - 30);
        notice.setForeground(theme.text);
        this.content.add(notice);

        this.sidebar = new Sidebar();
        this.add(this.sidebar);

        MessagePrompt msgp = new MessagePrompt("What is a dolphins favorite food?");
        msgp.setLocation((content.getWidth() - msgp.getWidth()) / 2, 150);
        this.content.add(msgp);

        MessageResponse msg = new MessageResponse();
        msg.updateMessage("Hey this is a really cool lorem ipsum text. I don't know what I am supposed to wrifg,hmdfhgjfdghlk jfdlkghjdflkhjfdklhjfdklhjfhkdfhjkdfghj");
        msg.complete("Base Model · Took 12ms to generate");
        msg.setLocation((content.getWidth() - msg.getWidth()) / 2, 300);
        this.content.add(msg);


        // handle sidebar animation
        this.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (e.getX() < 400) {
                    if (sidebar.getX() != -300) return;

                    AnimationUtils.animateEaseCubic(sidebar, 16, 40, 60, (elem, frame) -> {
                        int x = (int) (frame * 300) - 300;
                        sidebar.setLocation(x, 0);
                        content.setLocation(Math.max(x + 305, 155), 0);
                    });
                } else if (sidebar.getX() == 0) {
                    AnimationUtils.animateEaseCubic(sidebar, 16, 40, 60, (elem, frame) -> {
                        int x = -(int) (frame * 300);
                        sidebar.setLocation(x, 0);
                        content.setLocation(Math.min(455 + x, getWidth() - content.getWidth() - 5), 0);
                    });
                }
            }
        });
    }
}