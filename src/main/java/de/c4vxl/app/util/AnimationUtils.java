package de.c4vxl.app.util;

import javax.swing.*;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class AnimationUtils {
    private static ArrayList<JComponent> animatedComponents = new ArrayList<>();

    /**
     * Animation timer
     * @param component The component to animate
     * @param delay The delay between two frames (in ms)
     * @param nFrames The amount of frames to play until stopping
     * @param animation The action to perform
     */
    public synchronized static void animate(JComponent component, int delay, int nFrames, BiConsumer<JComponent, Integer> animation) {
        animate(component, delay, (a, i) -> {
            animation.accept(component, i);
            return i >= nFrames;
        });
    }

    /**
     * Animation timer
     * @param component The component to animate
     * @param delay The delay between two frames (in ms)
     * @param animation The action to perform. Return true to stop the animation
     */
    public synchronized static void animate(JComponent component, int delay, BiFunction<JComponent, Integer, Boolean> animation) {
        if (animatedComponents.contains(component))
            return;

        animatedComponents.add(component);

        AtomicInteger i = new AtomicInteger();
        Timer[] timer = new Timer[1];
        timer[0] = new Timer(delay, ae -> {
            if (animation.apply(component, i.get())) {
                animatedComponents.remove(component);
                timer[0].stop();
            }
            component.repaint();
            i.getAndIncrement();
        });
        timer[0].start();
    }

    public static void animateEaseCubic(JComponent component, int delay, int duration, int nFrames, BiConsumer<JComponent, Double> animation) {
        AnimationUtils.animate(component, delay, nFrames, (elementRef, frame) -> {
            double t = frame / (double) duration;
            double eased = AnimationUtils.easeInOutCubic(t > 1 ? 1 : t);
            animation.accept(component, eased);
        });
    }

    /**
     * Ease in/out function
     * @param t The current position
     */
    public static double easeInOutCubic(double t) {
        return t < 0.5
                ? 4 * t * t * t
                : 1 - Math.pow(-2 * t + 2, 3) / 2;
    }
}
