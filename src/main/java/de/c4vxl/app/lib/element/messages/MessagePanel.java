package de.c4vxl.app.lib.element.messages;

import com.google.gson.reflect.TypeToken;
import de.c4vxl.app.App;
import de.c4vxl.app.Theme;
import de.c4vxl.app.config.Config;
import de.c4vxl.app.language.Language;
import de.c4vxl.app.lib.component.Line;
import de.c4vxl.app.lib.component.ScrollPane;
import de.c4vxl.app.util.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MessagePanel extends JPanel {
    public ArrayList<JPanel> messages = new ArrayList<>();
    public HashMap<Long, List<String>> chat = new HashMap<>();
    public Long lastChatEntry = -1L;
    public ScrollPane pane;
    public int nextY;

    public final int gap;
    public final int outerGap;

    public int width;
    public int height;

    // If set to true, chat won't save automatically
    public boolean isLoading = false;

    public MessagePanel(int width, int height) { this(width, height, 10, 50); }

    public MessagePanel(int width, int height, int gap, int outerGap) {
        this.gap = gap;
        this.outerGap = outerGap;
        this.width = width;
        this.height = height;

        this.setOpaque(false);
        this.setLayout(null);

        this.pane = new ScrollPane(this);
        this.pane.setLocation(0, 85);
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
    public <T> T getLastItem(Class<T> clazz) {
        List<JPanel> elements = messages.stream().filter(clazz::isInstance).toList();
        if (!elements.isEmpty()) return (T) elements.getLast();
        return null;
    }

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
                this.add(new Line(getWidth() - 50, 1, Theme.current.accent).position(15, nextY));
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

        if (!isLoading) {
            this.lastChatEntry = System.currentTimeMillis();
            this.chat.put(this.lastChatEntry, List.of(prompt, ""));
        }

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
        MessageResponse r = this.getLastResponse();
        if (r == null) return this;
        r.updateMessage(response);

        if (!isLoading) {
            String prompt = this.chat.getOrDefault(this.lastChatEntry, List.of("", "")).getFirst();
            this.chat.put(this.lastChatEntry, List.of(prompt, response));
        }

        return this;
    }

    /**
     * Run MessageResponse#complete on the last response element
     * @param modelName The name of the model used for the generation
     * @param time The time needed for the generation
     */
    public MessagePanel completeLastResponse(String modelName, Long time) {
        MessageResponse r = this.getLastResponse();
        if (r == null) return this;
        r.complete(Language.current.get("chat.message.response.complete.info", modelName, String.valueOf(time)));
        this.reload();

        if (!isLoading) {
            List<String> chatData = this.chat.getOrDefault(this.lastChatEntry, List.of("", ""));
            this.chat.put(this.lastChatEntry, List.of(chatData.getFirst(), chatData.get(1), modelName, time.toString()));
            export(Config.HISTORIES_DIRECTORY + "/" + getName() + Config.HISTORY_FILE_EXTENSION);
        }

        return this;
    }

    /**
     * Generates a name for this chat
     */
    public String getName() {
        // Get prompt
        String prompt = this.chat.getOrDefault(-1L, List.of("")).getFirst();

        // Generate prompt if none was found
        if (prompt.isEmpty()) {
            prompt = this.getLastPrompt().getText();
            if (prompt.length() > 30)
                prompt = prompt.substring(0, 20);
            prompt = System.currentTimeMillis() + "-" + prompt;
        }

        // Remove special characters
        prompt = prompt.replaceAll("[\\\\/:*?\"<>|]", "").trim();

        this.chat.put(-1L, List.of(prompt));

        return prompt;
    }

    /**
     * Export this chat to a file
     * @param path The path to the file to export to
     */
    public void export(String path) {
        FileUtils.writeContent(path, FileUtils.toJSON(this.chat, false));
    }

    /**
     * Load the chat history from a file
     * @param path The path to the file
     */
    public void load(String path) {
        HashMap<Long, List<String>> data = FileUtils.fromJSON(FileUtils.readContent(path, "{}"), new TypeToken<>() {});
        if (data == null) return;
        this.isLoading = true;

        this.chat = data;
        data.keySet().stream().sorted().forEach(time -> {
            if (time == -1L) return; // Reserved for caches

            try {
                List<String> genData = data.get(time);
                String prompt = genData.get(0);
                String response = genData.get(1);
                String model = genData.get(2);
                long timeUsed = Integer.valueOf(genData.get(3)).longValue();

                this.createPrompt(prompt);
                this.createResponse();
                this.updateLastResponse(response);
                this.completeLastResponse(model, timeUsed);
            } catch (Exception ignored) {
                App.notificationFromKey("danger", 100, "app.notifications.chat.error.load_failed", time.toString());
                System.out.println("[ERROR]: Couldn't load chat element: " + time);
            }
        });

        this.isLoading = false;

        if (App.instance != null)
            App.instance.startChat();
    }

    /**
     * Returns the name of a chat by stripping the time data from it
     * @param filename The actual filename
     */
    public static String getDisplayNameFromFile(String filename) {
        String[] nameParts = filename.split("-");
        return String.join("-", Arrays.copyOfRange(nameParts, 1, nameParts.length));
    }
}