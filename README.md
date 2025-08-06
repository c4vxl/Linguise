<p align="center">
  <a href="https://github.com/c4vxl/Linguise/">
    <img src="https://cdn.c4vxl.de/Linguise/logo_large.svg" alt="Logo">
  </a>
</p>

<h2 align="center">AI Chatbot</h2>

<p align="center">A fully offline, from-scratch, Java-based chatbot application featuring a custom neural networking engine.</p>

<br>

<p align="center">
  <img src="https://img.shields.io/badge/Programming%20Language:-Java-blue" alt="Java Badge" />
  <img src="https://img.shields.io/badge/Type:-User App-red" alt="Java Badge" />
  <img src="https://img.shields.io/badge/Topic:-AI-orange" alt="Java Badge" />
</p>

---

<br>

### Table of contents (TOC)
- [About Linguise](#about-linguise)
- [Build your own AI Models](#build-your-own-ai-models)
- [Create your own custom languages and themes](#create-your-own-custom-themes-and-languages)
- [Installation](#installation)
    - [Compile yourself](#build-linguise-yourself)
    - [Requirements](#requirements)
- [Versions](#versions)
- [Resources](#resources)

<br>

# About Linguise
![home preview](https://cdn.c4vxl.de/Linguise/prev/home.png)

**Linguise** is a Java-based AI chatbot application designed to run completely offline. It was build completely from scratch, with no external ML libraries involved.

At its core, Linguise uses **jNN**, a custom-build neural network engine which I developed specifically for this project.
**jNN** handles the low-level math and model logic, while this repo focuses on providing a user-friendly interface.

Said user app supports custom languages, themes and most notably, custom AI models, which users can either import from a local file or download directly from a shared server.

This makes **Linguise** highly adaptable to user preverences.

<br>

<details>
<summary>View screenshots</summary>

![chat preview](https://cdn.c4vxl.de/Linguise/prev/chat.png)
![settings preview](https://cdn.c4vxl.de/Linguise/prev/settings.png)
</details>

<br>

# Build your own AI Models
Due to **Linguises** nature of allowing, and relying, on third parts models, it is made very easy to build and train your own models:

- You can either train your own models directly using the [jNN library](https://github.com/c4vxl/jNN)
- Alternatively, if you prefer working in a Python ecosystem, Linguise provides a small Python library that allows you to export PyTorch models into a format compatible with jNN. Said library can be found [[here]](https://github.com/c4vxl/jNN-python).

Note that **Linguise** needs a second wrapper around the jNN model. This wrapper can be created with the following steps:
1. Create a new Java project and bind the `Linguise` JAR file as a dependency.
2. Import the jNN library in the new project. [[jNN Documentation]](https://github.com/c4vxl/jNN/tree/main?tab=readme-ov-file#installation)
3. Create a new Java-class
4. Use the following example code to wrap a jNN model in a Linguise model:
    ```java
    // Create new Linguise-model wrapper
    Model model = new Model(null, null); // make sure this is imported from de.c4vxl.app.model

    // Create a jNN text generation pipeline
    model.pipeline = new TextGenerationPipeline(
        /* Pass your models tokenizer (e.g. new GPT2Tokenizer()) */,
        /* Pass your **jNN** model */.load("/path/to/python/exported/model/")
    );

    // Export linguise-wrapped model
    model.export(
        "/path/to/export/final/model.mdl",
        /* Pass the hyperparameters used to construct a new instance of your jNN-models class */
    );
    ```
5. Execute the java project with your created class.

-> The final Linguise-compatible model file will be outputted at your specified path.

> [!CAUTION]
> Ensure the user executing the snippet has write permissions for the output path.

<br>

# Create your own custom themes and languages
Themes and languages are defined using simple JSON files, making it easy to create your own.

To get started, just copy one of the templates below into a new `.theme` or `.lang` file, then customize the properties to your liking:

- [[Theme template]](/src/main/resources/themes/dark.theme)
- [[Language template]](/src/main/resources/languages/english.lang)

<br>

# Installation
> [!NOTE]
> Linguise was build using a JDK of version 21.

<br>

Here's a step by step guide to installing **Linguise**:

1. Download the `Linguise-{version}.jar` from the [[latest release]](https://github.com/c4vxl/Linguise/releases/latest) in this repository.
2. Execute it using Java 21: `java -jar Linguise-{version}.jar`.

<br>

# Build Linguise yourself
To compile **Linguise** youself, simply follow these steps:
1. Clone this repository
2. Build it using gradle: `./gradlew clean build`
3. The compiled `.jar` file can be found at /build/libs/

<br>

# Requirements
Linguise doesn't rely on many external libraries. However, its Text-to-Speech (TTS) functionality depends on your operating system's native TTS capabilities.
To use TTS features, ensure the necessary engine is installed and available.

*Required TTS tool by OS:*
| Operating System | Dependency | Source |
| ----------- | ----------- | -----------  |
| Windows | `System.Speech` | *Usually preinstalled* |
| MacOS | `say` | *Usually preinstalled* |
| Linux | `espeak-ng` | [Espeak-NG on GitHub](https://github.com/espeak-ng/espeak-ng) |

<br>

> [!IMPORTANT]
> If TTS isn’t working, double-check that the appropriate tool is installed and accessible in your environment.

<br>

# Versions
| Version | Description | Release |
| ----------- | ----------- | -----------  |
| **1.0.0** (_latest_) | Full version of Linguise. | [v. 1.0.0](https://github.com/c4vxl/Linguise/releases/tag/1.0.0) |

# Resources
- [jNN Library](https://github.com/c4vxl/jNN)
- [Python library for exporting PyTorch models](https://github.com/c4vxl/jNN-python)
- [Collection of pretrained models](https://github.com/Linguise/Models)

---

> [!IMPORTANT]
> This project was built by a single person as a fun and educational endeavor.
>
> **Linguise** is **not** built for production environments and is not optimized for performance or scalability.
> The quality of the chatbots responses depends entirely on the models you choose to use.
>
> That said, it’s definitely fun to play around with!

<br>

A project by [c4vxl](https://info.c4vxl.de/)