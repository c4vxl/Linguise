package de.c4vxl.app.model;

import de.c4vxl.app.config.Config;
import de.c4vxl.app.language.Language;
import de.c4vxl.app.util.FileUtils;
import de.c4vxl.app.util.GenerationUtils;

import java.io.File;
import java.nio.file.Path;
import java.util.function.Consumer;

public class Model {
    @FunctionalInterface public interface Generator<A, B, C, R> { R apply(A a, B b, C c); }

    public final File file;
    public final String path;
    public final String name;
    public final Generator<String, Consumer<String>, Runnable, Thread> generator;

    public Model(String path, Generator<String, Consumer<String>, Runnable, Thread> generator) {
        this.path = path;
        this.file = new File(path);
        String filename = Path.of(path).getFileName().toString();
        this.name = filename.substring(0, 1).toUpperCase() + filename.substring(1, filename.length() - Config.MODEL_FILE_EXTENSION.length()).toLowerCase();
        this.generator = generator;
    }

    public static Model current = null;
    public static Model fakeModel = null;

    /**
     * Get a "fake" model which outputs a sequence of Lorem Ipsum text, no matter the prompt
     * @param delay The delay between two characters
     */
    public static Model getFakeModel(int delay) {
        if (fakeModel != null) return fakeModel;

        fakeModel = new Model(Language.current.get("app.models.fake_model.name") + Config.MODEL_FILE_EXTENSION,
                (prompt, handler, onDone) ->
                        GenerationUtils.fakeGenerationStream(GenerationUtils.ipsum, delay, handler, onDone)
        );
        return fakeModel;
    }

    /**
     * Generate from the model
     * @param prompt The prompt to the model
     * @param onUpdate Handler for updates
     * @param onDone Handler for end of generation
     */
    public Thread generate(String prompt, Consumer<String> onUpdate, Runnable onDone) {
        return generator.apply(prompt, onUpdate, onDone);
    }


    /**
     * Load a model from its file
     * @param path The path to the model
     */
    public static Model fromFile(String path) {
        if (Path.of(path).getFileName().toString().equals("__fake__"))
            return Model.fakeModel;

        String content = FileUtils.readContent(path, null); // Fallback is null
        if (content == null || content.isEmpty()) { // If fallback is triggered (file doesn't exist or isn't readable)
            System.out.println("[ERROR]: Not a valid model: " + path);
            return null;
        }

        // TODO: Implement generator
        return new Model(path, null);
    }
}