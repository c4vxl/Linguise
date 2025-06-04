package de.c4vxl.app.util;

import java.util.function.Consumer;

public class GenerationUtils {
    public static String ipsum = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.";
    public static Thread fakeGenerationStream(String message, int delay, Consumer<String> handler) {
        String[] parts = message.split("");

        if (delay == 0)
            return new Thread(() -> handler.accept(message));

        return new Thread(() -> {
            String current = "";
            for (String part : parts) {
                current += part;
                handler.accept(current);

                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
