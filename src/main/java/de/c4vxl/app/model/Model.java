package de.c4vxl.app.model;

import com.google.gson.reflect.TypeToken;
import de.c4vxl.app.App;
import de.c4vxl.app.config.Config;
import de.c4vxl.app.language.Language;
import de.c4vxl.app.lib.element.modal.ProgressbarWindow;
import de.c4vxl.app.util.ClassUtils;
import de.c4vxl.app.util.FileUtils;
import de.c4vxl.app.util.GenerationUtils;
import de.c4vxl.models.type.TextGenerationModel;
import de.c4vxl.pipeline.TextGenerationPipeline;
import de.c4vxl.tokenizers.type.Tokenizer;

import javax.swing.*;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.function.Consumer;

public class Model {
    @FunctionalInterface public interface Generator { Thread apply(String prompt, Consumer<String> handler, Runnable onDone); }

    public final File file;
    public final String path;
    public String name;
    public TextGenerationPipeline pipeline;
    public final Generator generator;
    private boolean isInitialized = false;

    public Model(String path, Generator generator) {
        // Set file
        this.path = path;
        this.file = path == null ? null : new File(path);

        // Set name
        String filename = path == null ? null : Path.of(path).getFileName().toString();
        this.name = filename == null ? null :
                filename.substring(0, 1).toUpperCase() + filename.substring(1, filename.length() - Config.MODEL_FILE_EXTENSION.length()).toLowerCase();

        // Set generator
        this.generator = generator != null ? generator : (prompt, handler, onDone) -> new Thread(() -> {
            ArrayList<Integer> tokens = new ArrayList<>();
            try {
                pipeline.forward(prompt, pipeline.newTokens, (token, idx) -> {
                    // Terminate current forward pass by throwing an exception
                    if (Thread.currentThread().isInterrupted())
                        throw new RuntimeException();

                    if (token == pipeline.tokenizer.eosTokenID()) return; // make sure to stop on eos

                    tokens.add(token);
                    handler.accept(pipeline.tokenizer.decode_(tokens.toArray(Integer[]::new)));
                });
            } catch (Exception ignored) {}
            finally {
                onDone.run();
            }
        });
    }

    public static Model current = null;
    public static Model fakeModel = null;

    /**
     * Get a "fake" model which outputs a sequence of Lorem Ipsum text, no matter the prompt
     * @param delay The delay between two characters
     */
    public static Model getFakeModel(int delay) {
        if (fakeModel != null) return fakeModel;
        if (Language.current == null) return new Model(null, null);

        fakeModel = new Model("__fake__",
                (prompt, handler, onDone) ->
                        GenerationUtils.fakeGenerationStream(GenerationUtils.ipsum, delay, handler, onDone)
        );

        fakeModel.name = Language.current.get("app.models.fake_model.name");

        return fakeModel;
    }

    /**
     * Generate from the model
     * @param prompt The prompt to the model
     * @param onUpdate Handler for updates
     * @param onDone Handler for end of generation
     */
    @SuppressWarnings("InstantiatingAThreadWithDefaultRunMethod")
    public Thread generate(String prompt, Consumer<String> onUpdate, Runnable onDone) {
        if (!isInitialized) {
            App.notificationFromKey("danger", 300, "app.notifications.models.error.not_initialized");
            System.out.println("[ERROR]: Tried to generate from uninitialized model!");
            return new Thread() {};
        }

        return generator.apply(prompt, onUpdate, onDone);
    }

    /**
     * Initializes the model and loads its pipeline.
     * Only do this as soon as you want to use it as this might take a while due to big file sizes!
     */
    public boolean initialize() {
        if (isInitialized) return true;
        isInitialized = true;

        System.out.println("[INFO]: Initializing model: " + this.name);

        if (this.path.equals("__fake__"))
            return true;

        // Create progressbar window
        ProgressbarWindow progressBar = new ProgressbarWindow(Language.current.get("app.models.popup.loading.reading_label"));
        SwingUtilities.invokeLater(progressBar::open);

        // Read file content and show in bar
        String content = FileUtils.readContent(this.path, null, percentage -> { // Fallback is null
            progressBar.setValue(percentage);
            if (percentage == 100) {
                progressBar.setValue(0);
                progressBar.setLabel(Language.current.get("app.models.popup.loading.interpreting_file"));
            }
        });

        if (content == null || content.isEmpty()) { // If file doesn't exist or isn't readable
            App.notificationFromKey("danger", 300, "app.notifications.models.error.empty_file", this.file.getName());
            System.out.println("[ERROR]: Not a valid model: " + path);
            SwingUtilities.invokeLater(progressBar::close);
            return false;
        }

        // Fake a stream while interpreting file
        Thread progressThread = GenerationUtils.generateFakePercentageStream((int) (Math.min(this.file.getTotalSpace(), 1000) * 0.001), percentage -> {
            progressBar.setValue(percentage);
            if (percentage == 100)
                SwingUtilities.invokeLater(progressBar::close);
        });
        progressThread.start();

        // Get pipeline
        TextGenerationPipeline pipeline = pipelineFromState(FileUtils.fromJSON(content, new TypeToken<>() {}));
        if (pipeline == null) {
            App.notificationFromKey("danger", 300, "app.notifications.models.error.invalid_pipeline", this.file.getName());
            System.out.println("[ERROR]: Invalid pipeline!");
            SwingUtilities.invokeLater(progressBar::close);
            progressThread.interrupt();
            return false;
        }

        this.pipeline = pipeline;
        progressThread.interrupt();

        SwingUtilities.invokeLater(progressBar::close);

        return true;
    }

    /**
     * Get the model pipeline from the state of a model
     * @param state The state
     */
    public static TextGenerationPipeline pipelineFromState(HashMap<String, Object> state) {
        // Get model
        TextGenerationModel model = ClassUtils.getInstance(
                String.valueOf(state.get("modelClass")),
                ((ArrayList<?>) state.getOrDefault("modelConstructorArgs", new ArrayList<>()))
                        .stream().map(x -> {
                            if (x instanceof Double d)
                                return (Integer) d.intValue(); // GSON converts integers to doubles for some reason
                            return x;
                        })
                        .toArray()
        );
        if (model == null) {
            System.out.println("[ERROR]: Couldn't load the model!");
            return null;
        }

        // Load model state
        model.fromJSON(String.valueOf(state.getOrDefault("modelState", "{}")));

        // Get tokenizer
        Tokenizer tokenizer = ClassUtils.getInstance(String.valueOf(state.get("tokenizerClass")));
        if (tokenizer == null) {
            System.out.println("[ERROR]: Couldn't load the tokenizer!");
            return null;
        }

        // Load tokenizer state
        tokenizer.fromJSON(String.valueOf(state.getOrDefault("tokenizerState", "{}")));

        return new TextGenerationPipeline(tokenizer, model);
    }

    /**
     * Load a model from its file
     * @param path The path to the model
     */
    public static Model fromFile(String path) {
        if (!Path.of(path).toFile().isFile())
            return null;

        String filename = Path.of(path).getFileName().toString();
        if (filename.equals("__fake__"))
            return getFakeModel(1);

        // Create model instance
        return new Model(path, null);
    }

    /**
     * Returns the current state of the model
     * @param modelConstructorArgs The hyperparameters for reconstructing the model
     */
    public HashMap<String, Object> state(Object... modelConstructorArgs) {
        return new LinkedHashMap<>() {{
            put("modelClass", pipeline.model.getClass().getName());
            put("modelConstructorArgs", modelConstructorArgs);
            put("modelState", pipeline.model.asJSON());
            put("tokenizerClass", pipeline.tokenizer.getClass().getName());
            put("tokenizerState", pipeline.tokenizer.asJSON());
        }};
    }

    /**
     * Returns the state of the model as json
     * @param isPretty If set to true, pretty printing will be enabled
     * @param modelConstructorArgs The hyperparameters for reconstructing the model
     */
    public String asJSON(boolean isPretty, Object... modelConstructorArgs) { return FileUtils.toJSON(this.state(modelConstructorArgs), isPretty); }

    /**
     * Export the model to a file
     * @param path The path to the file
     * @param modelConstructorArgs The hyperparameters for reconstructing the model
     */
    public Model export(String path, Object... modelConstructorArgs) {
        FileUtils.writeContent(path, this.asJSON(false, modelConstructorArgs));
        return this;
    }
}