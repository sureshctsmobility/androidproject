# SkycastProject

A professional-grade, scalable Android ecosystem featuring a modern client-side application and a robust Kotlin-based backend.

## 🏗 Architecture
This project is built with **Clean Architecture** principles to ensure modularity, scalability, and ease of testing.

* **Android Client:** Built using **Jetpack Compose** for a modern, reactive UI and **Kotlin Coroutines** for asynchronous operations.
* **Backend:** A scalable Kotlin service handling data persistence and business logic.
* **Dependency Management:** Utilizes a central **Version Catalog (`libs.versions.toml`)** to keep dependencies synchronized across all modules.

## 🚀 Key Features
* **Modern UI:** High-performance layouts using Jetpack Compose (Surface, Box, Scaffold).
* **Efficient Networking:** Implementation of the **Repository Pattern** for clean data fetching and caching.
* **Lifecycle Management:** Optimized resource handling using side-effects (e.g., `onDispose`) to prevent memory leaks in Services and UI components.
* **Advanced Kotlin Usage:** Robust logic using `sealed classes`, `data classes`, and compile-time exhaustive `when` blocks.
* **Animations:** Integration of high-quality **Lottie** animations for an enhanced user experience.

## 🛠 Tech Stack
* **Language:** Kotlin
* **UI Framework:** Jetpack Compose (Material 3)
* **Database:** Firebase integration with support for relational SQL structures.
* **Background Tasks:** Android Services and Broadcast Receivers with a focus on lifecycle safety.
* **Build System:** Gradle (Kotlin DSL) with Version Catalogs.

## 📥 Getting Started
1.  **Clone the repository:**
    ```bash
    git clone https://github.com/sureshctsmobility/AndroidProject.git
    ```
2.  **Open the project:**
    Open the root folder `SkycastProject` in **Android Studio** or **IntelliJ IDEA**.
3.  **SDK Setup:**
    Ensure your `local.properties` is correctly configured with your Android SDK path.
4.  **Build:**
    Sync Gradle and build the `:app` module.

## 📄 License
This project is for professional development and portfolio purposes.