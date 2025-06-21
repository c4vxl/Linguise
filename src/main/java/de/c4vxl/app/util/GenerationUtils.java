package de.c4vxl.app.util;

import java.util.function.Consumer;

public class GenerationUtils {
    public static String ipsum = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.";
    public static Thread fakeGenerationStream(String message, int delay, Consumer<String> handler, Runnable onDone) {
        String[] parts = message.split("");

        if (delay == 0)
            return new Thread(() -> {
                handler.accept(message);
                onDone.run();
            });

        return new Thread(() -> {
            String current = "";
            try {
                for (String part : parts) {
                    current += part;
                    handler.accept(current);

                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) { return; }
                }
            } catch (Exception ignored) {}
            finally { onDone.run(); }
        });
    }

    /**
     * Generates a fake progress bar stream
     * @param totalTime The total time it should take (in ms)
     * @param onUpdate The update handler
     */
    public static Thread generateFakePercentageStream(int totalTime, Consumer<Integer> onUpdate) {
        return new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                onUpdate.accept(i);

                // Sleep
                int sleepTime = (int) (totalTime * (AnimationUtils.easeInOutCubic(i / 100.0) -
                        AnimationUtils.easeInOutCubic((i + 1) / 100.0)));
                try { Thread.sleep(sleepTime); }
                catch (InterruptedException ignored) { }
            }
        });
    }

}
