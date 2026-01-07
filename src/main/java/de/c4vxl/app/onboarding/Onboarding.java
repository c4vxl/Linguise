package de.c4vxl.app.onboarding;

import de.c4vxl.Main;
import de.c4vxl.app.config.Config;
import de.c4vxl.app.language.Language;
import de.c4vxl.app.lib.component.Button;
import de.c4vxl.app.lib.component.Dropdown;
import de.c4vxl.app.lib.component.Elements;
import de.c4vxl.app.lib.component.Window;
import de.c4vxl.app.lib.element.modal.PopupWindow;
import de.c4vxl.app.lib.element.settings.SettingsPageTheme;
import de.c4vxl.app.theme.Theme;
import de.c4vxl.app.util.Factory;
import de.c4vxl.app.util.FileUtils;
import de.c4vxl.app.util.Resource;

import javax.swing.*;

public class Onboarding extends Window {
    public static String DEFAULT_CHATBOT = "https://cdn.c4vxl.de/Linguise/models/Chatty.mdl";

    public JPanel page;

    public Onboarding() {
        this(Language.current, Theme.current);
    }

    public Onboarding(Language language, Theme theme) {
        super(language.get("app.onboarding.name"), 700, 400);
        Theme.current = theme;
        Language.current = language;

        // Basic styling
        this.getContentPane().setLayout(null);
        this.background(theme.background)
                .borderRadius(20)
                .layout(null);



        this.setIconImage(Resource.loadIcon("media/Logo small.png", 300, Theme.current.accent).getImage());
        page(0);
    }

    private Factory<JLabel> title(String id) {
        return new Factory<>(Elements.text(
                "<p style='font-size: 18px;'>" +
                        Language.current.get(id) +
                        "</p>"
                , -1))
                .pos(20, 20);
    }

    private Factory<JLabel> text(String id) {
        return new Factory<>(Elements.text(
                "<p style='font-size: 11px;'>" +
                        Language.current.get(id) +
                        "</p>"
                , -1))
                .foreground(Theme.current.text_1);
    }

    private Factory<Button> button(String id, int page, boolean hollow) {
        Button button = new Button(Theme.current.accent, Theme.current.text)
                .withLabel(Language.current.get(id))
                .withBorderRadius(15);

        if (hollow)
            button.borderStyle();

        if (page != -1)
            button.withAction((e) -> page(page));

        return new Factory<>(button)
                .size(300, 50)
                .centerX(this);
    }

    public JPanel page(Integer page) {
        JPanel panel = new Factory<>(new JPanel())
                .layout(null)
                .size(this.getSize())
                .background(Theme.current.background)
                .get();

        switch (page) {
            case 0:
                panel.add(new Factory<>(Elements.iconButton(Resource.loadIcon("media/Logo large.png", 400, Theme.current.accent)))
                                .centerX(this).posY(100).get());
                panel.add(title("app.onboarding.0.title").posY(190).centerX(this).get());
                panel.add(text("app.onboarding.0.text").centerX(this).posY(230).get());
                panel.add(button("app.onboarding.0.button", 1, false).posY(300).get());

                break;

            case 1:
                panel.add(title("app.onboarding.1.title").get());
                panel.add(text("app.onboarding.1.text").pos(20, 50).get());

                Dropdown dropdown = new Dropdown(Language.current.get("app.onboarding.1.dropdown.title"), 300, 50, 290, 2, 2, Theme.current.background_1, Theme.current.text);

                for (String language : Resource.listResources("languages/")) {
                    language = language.replace(".lang", "");
                    language = language.toUpperCase();

                    String finalLanguage = language;
                    dropdown.addItem(Dropdown.createDefaultItem(language, () -> {
                        Language.current = Language.fromResource(finalLanguage.toLowerCase());
                        page(1);
                    }, false));
                }

                panel.add(
                        new Factory<>(dropdown)
                                .centerX(this)
                                .posY(100)
                                .get()
                );

                panel.add(button("app.onboarding.1.button", 2, false).posY(310).get());

                break;

            case 2:
                panel.add(title("app.onboarding.2.title").get());
                panel.add(text("app.onboarding.2.text").pos(20, 50).get());

                int i = 0;
                for (Theme theme : new Theme[]{
                        Theme.fromResource("dark"),
                        Theme.fromResource("moonlight"),
                        Theme.fromResource("coralbloom"),
                }) {

                    panel.add(new Factory<>(SettingsPageTheme.entry(theme, () -> {
                        if (Theme.current.name.equals(theme.name)) return;
                        Config.setTheme(theme);
                        page(2);
                    }))
                            .pos(150 + i * 140, 110)
                            .get());

                    panel.add(button("app.onboarding.2.button", 3, false).posY(310).get());

                    i++;
                }

                break;

            case 3:
                panel.add(title("app.onboarding.3.title").get());
                panel.add(text("app.onboarding.3.text").pos(20, 50).get());

                panel.add(new Factory<>(Elements.iconButton(Resource.loadIcon("media/bot.png", 150, Theme.current.accent)))
                        .centerX(this).posY(100).get());

                panel.add(
                        button("app.onboarding.3.button1", -1, true)
                                .onClick(this::complete)
                                .size(200, 50)
                                .pos(150, 300)
                                .get()
                );

                panel.add(
                        button("app.onboarding.3.button2", -1, false)
                                .onClick(() -> {
                                    PopupWindow window = new PopupWindow(Language.current.get("app.onboarding.3.popup.title"),
                                            Language.current.get("app.onboarding.3.popup.label"));

                                    new Thread(() -> {
                                        this.setVisible(false);
                                        window.open();
                                        FileUtils.downloadFile(DEFAULT_CHATBOT, Config.MODELS_DIRECTORY + "/" + "Chatty.mdl", (p) -> {});
                                        this.setVisible(true);
                                        window.close();
                                        complete();
                                    }).start();
                                })
                                .size(200, 50)
                                .pos(150 + 200 + 10, 300)
                                .get()
                );

                break;
        }

        if (this.page != null)
            this.remove(this.page);

        this.page = panel;
        this.add(panel);
        this.repaint();

        return panel;
    }

    public void complete() {
        this.close();
        System.out.println("[Onboarding]: Onboarding complete!");
        Config.setConfigValue("app.onboarding", true);
        Main.open();
    }
}