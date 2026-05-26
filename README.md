# TodoFlow 📝

TodoFlow is a modern, minimalist To-Do List native Android application built entirely using **Kotlin** and **Jetpack Compose**. It features a clean Obsidian Dark Mode aesthetic, real-time data persistence, and a highly interactive Home Screen Widget.

<p align="center">
  <img src="app/src/main/res/drawable/ic_logo.png" width="128" height="128" alt="TodoFlow Logo" />
</p>

---

## Features ✨

### 📱 Main App
- **Obsidian Dark Theme**: A harmonized charcoal and teal-tinted minimalist dark mode.
- **Dynamic Date Header**: Displays the current day's date dynamically.
- **Dialog-driven Task Creation**: Floating Action Button (FAB) triggers a clean dialog to type and add new tasks.
- **Local Persistence**: Tasks are saved locally on internal storage in JSON format, ensuring they survive app clears and reboots.
- **Stats Counter**: Real-time counter showing remaining and completed task counts.

### 📌 Home Screen Widget
- **Interactive Toggles**: Check and uncheck tasks directly from the home screen without opening the main app.
- **Dynamic Progress Bar**: A horizontal progress bar that fills up programmatically as tasks are completed.
- **Real-time Syncing**: State changes made on the widget instantly reflect inside the foreground app and vice versa.

---

## Tech Stack 🛠️

- **Core**: Kotlin
- **UI Framework**: Jetpack Compose (Material 3)
- **Architecture**: MVVM (Model-View-ViewModel) + StateFlow
- **Data Persistence**: JSON-based local repository
- **Widget**: AppWidgetProvider + RemoteViews + RemoteViewsService (for ListViews)

---

## How to Build & Run 🚀

### Prerequisites
- JDK 17+
- Android SDK (API level 34+)

### Build Steps

1. **Clone the repository:**
   ```bash
   git clone https://github.com/rahuljangra396/TodoFlow-Android.git
   cd TodoFlow-Android
   ```

2. **Build the Debug APK:**
   ```bash
   ./gradlew assembleDebug
   ```
   The APK will be generated at `app/build/outputs/apk/debug/app-debug.apk`.

3. **Install and Run on a Device/Emulator:**
   ```bash
   ./gradlew installDebug
   ```

---

## License 📄
This project is open-source and available under the [MIT License](LICENSE).
