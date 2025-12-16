package de.c4vxl.app.lib.element.modal;

import de.c4vxl.app.theme.Theme;
import de.c4vxl.app.language.Language;
import de.c4vxl.app.lib.component.Elements;
import de.c4vxl.app.lib.component.Window;
import de.c4vxl.app.lib.element.chatbar.ChatBar;
import de.c4vxl.app.util.Factory;

import javax.swing.*;
import java.util.function.Consumer;

public class TextPromptWindow extends Window {
    public final ChatBar input;

    public TextPromptWindow(String title, String subtitle, Consumer<String> onSubmit) {
        super(title, 500, 200);

        this.undecorated()
                .borderRadius(12)
                .background(Theme.current.background)
                .layout(null);

        this.input = new ChatBar(getWidth() - 200, 40, (value) -> {
            this.close();
            onSubmit.accept(value);
        });
        input.textField.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        input.textField.setSize(input.getWidth() - 20, input.textField.getHeight());
        input.remove(input.getComponent(1));

        this.add(new Factory<>(Elements.title(title, -1))
                        .pos(35, 25)
                .get());

        this.add(new Factory<>(Elements.text(subtitle, -1))
                        .pos(35, 80)
                .get());

        JPanel panel = new JPanel();
        panel.setLocation(25, 50 + 50 + 5);
        panel.setOpaque(false);
        panel.add(this.input);
        panel.add(new Factory<>(Elements.hollowButton().withLabel(Language.current.get("app.global.submit"))).size(130, 40)
                        .onClick(() -> input.textField.postActionEvent()).get());
        panel.setSize(panel.getPreferredSize());
        this.add(panel);
    }
}