package de.c4vxl.app.lib.element.messages;

import de.c4vxl.app.Theme;
import de.c4vxl.app.lib.component.HR;
import de.c4vxl.app.lib.component.ScrollPane;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class MessagePanel extends JPanel {
    public ArrayList<JPanel> messages = new ArrayList<>();
    public ScrollPane pane;
    public int nextY;

    public final int gap;
    public final int outerGap;

    public int width;
    public int height;

    public MessagePanel(int width, int height) { this(width, height, 10, 50); }

    public MessagePanel(int width, int height, int gap, int outerGap) {
        this.gap = gap;
        this.outerGap = outerGap;
        this.width = width;
        this.height = height;

        this.setOpaque(false);
        this.setLayout(null);

        this.pane = new ScrollPane(this);
        this.pane.setSize(width, height);

        this.pane.getViewport().addChangeListener((e) -> {
            for (Component component : this.getComponents()) {
                if (!(component instanceof MessageResponse)) continue;

                int scrollY = this.pane.getVerticalScrollBar().getValue();
                int compY = component.getY();
                int overlapTop = Math.max(0, Math.min(compY + component.getHeight(), scrollY + this.getHeight()) - Math.max(compY, scrollY));
                ((MessageResponse) component).opacity = (float) overlapTop / component.getHeight();
            }
        });
    }

    /**
     * Get the last item of a type from the elements stack
     * @param clazz The class of the item to look for
     */
    @SuppressWarnings("unchecked")
    public <T> T getLastItem(Class<T> clazz) { return (T) messages.stream().filter(clazz::isInstance).toList().getLast(); }

    /**
     * Get the last MessagePrompt item in the elements stack
     */
    public MessagePrompt getLastPrompt() { return getLastItem(MessagePrompt.class); }

    /**
     * Get the last MessageResponse item in the elements stack
     */
    public MessageResponse getLastResponse() { return getLastItem(MessageResponse.class); }

    /**
     * Reload all elements
     */
    public void reload() {
        if (this.messages == null)
            this.messages = new ArrayList<>();

        nextY = 10;
        int last = messages.size() - 1;
        this.removeAll();
        for (int i = 0; i < this.messages.size(); i++) {
            Component component = this.messages.get(i);
            boolean isOuterElement = !(i % 2 == 0);

            component.setLocation(0, nextY);
            nextY += component.getHeight() + (i == last ? 0 : isOuterElement ? (outerGap / 2) : gap);

            if (isOuterElement && i != last) {
                this.add(new HR(getWidth() - 50, 1, Theme.current.accent).position(15, nextY));
                nextY += outerGap / 2;
            }

            this.add(component);
        }

        if (this.pane != null) {
            this.pane.repaint();
            this.setSize(this.pane.getWidth() - 20, nextY);
            SwingUtilities.invokeLater(this.pane::scrollToBottom);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(this.getWidth(), nextY);
    }

    /**
     * Create a MessagePrompt and display it in the panel
     * @param prompt The prompt
     */
    public MessagePanel createPrompt(String prompt) {
        this.messages.add(new MessagePrompt(prompt, width - 15));
        this.repaint();

        return this;
    }

    /**
     * Create a MessageResponse and display it in the panel
     */
    public MessagePanel createResponse() {
        this.messages.add(new MessageResponse(width - 15));
        this.reload();
        return this;
    }

    /**
     * Edit the text of the last MessageResponse
     * @param response The new response
     */
    public MessagePanel updateLastResponse(String response) {
        this.getLastResponse().updateMessage(response);
        this.reload();
        return this;
    }

    /**
     * Run MessageResponse#complete on the last response element
     * @param info The Info to pass
     */
    public MessagePanel completeLastResponse(String info) {
        this.getLastResponse().complete(info);
        this.reload();
        return this;
    }
}