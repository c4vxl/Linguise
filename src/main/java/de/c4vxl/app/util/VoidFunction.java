package de.c4vxl.app.util;

@FunctionalInterface
public interface VoidFunction<R> {
    void apply(R r);
}