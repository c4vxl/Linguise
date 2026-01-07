package de.c4vxl.app.util;

import java.lang.reflect.Constructor;
import java.util.BitSet;
import java.util.HashMap;

public class ClassUtils {
    /**
     * Map from normal classes to primitives
      */
    public static HashMap<Class<?>, Class<?>> PRIMITIVES = new HashMap<>() {{
        put(Integer.class, int.class);
        put(Double.class, double.class);
        put(Float.class, float.class);
        put(Long.class, long.class);
        put(Byte.class, byte.class);
        put(Boolean.class, boolean.class);
    }};

    /**
     * Tries to find a constructor by trying all possible combinations of normal and primitive versions of the args
     * @param clazz The class to find the constructor for
     * @param args The arguments to the constructor
     */
    public static <T> Constructor<T> findConstructor(Class<T> clazz, Object... args) {
        // Calculate amount of possible combinations
        int total = 2 << args.length; // 2 * (2^len)

        // Try all possible combinations
        for (int i = 0; i < total; i++) {
            // Generate combination
            BitSet bitSet = BitSet.valueOf(new long[]{i});
            Class<?>[] combination = new Class<?>[args.length];
            for (int j = 0; j < args.length; j++)
                combination[j] = bitSet.get(j) ? PRIMITIVES.getOrDefault(args[j].getClass(), args[j].getClass()) : args[j].getClass();

            // Try combination
            try { return clazz.getConstructor(combination); } catch (Exception ignored) {}
        }

        return null;
    }

    /**
     * Creates a new instance of a constructor ignoring errors.
     * @param constructor The constructor
     * @param args The arguments to the constructor
     */
    public static <T> T newInstance(Constructor<T> constructor, Object... args) {
        if (constructor == null) return null;

        try { return constructor.newInstance(args); }
        catch (Exception e) { return null; }
    }

    /**
     * Gets a new instance of a class from its name
     * @param className The name of the class
     * @param args The arguments to the constructor
     */
    @SuppressWarnings("unchecked")
    public static <T> T getInstance(String className, Object... args) {
        Class<T> clazz;
        try { clazz = (Class<T>) Class.forName(className); }
        catch (Exception e) { return null; }

        return newInstance(findConstructor(clazz, args), args);
    }
}
