# Byterover Handbook: githubusers

## Layer 1: System Overview

### Project Purpose

This project is an Android application that appears to be a client for viewing GitHub users. Based on the module names (`feature.users`, `feature.users.list`, `feature.users.detail`, `feature.search`), the application likely allows users to search for, view a list of, and see the details of GitHub users.

### Architecture

The application follows a modern Android architecture, characterized by:

*   **Modular Design:** The codebase is split into `core`, `feature`, and `navigation` modules. This promotes separation of concerns, reusability, and maintainability.
*   **MVVM (Model-View-ViewModel):** While not explicitly stated, the use of Jetpack Compose and ViewModel is a strong indicator of an MVVM or a similar state-oriented UI architecture.
*   **Dependency Injection:** Hilt is used for managing dependencies, which helps in creating a loosely coupled and testable application.
*   **Single Activity Architecture:** The presence of a single `MainActivity` and a robust navigation component (`Navigation3`) suggests a single-activity architecture, where different screens are implemented as composable destinations within a single activity.

### Key Technologies

*   **Programming Language:** Kotlin
*   **Build Tool:** Gradle
*   **UI:** Jetpack Compose
*   **Dependency Injection:** Hilt
*   **Asynchronous Programming:** Kotlin Coroutines
*   **Navigation:** Jetpack Navigation 3
*   **Networking:** Ktor
*   **Image Loading:** Coil
*   **Paging:** Jetpack Paging 3
*   **Database:** Jetpack Room
*   **Testing:** JUnit, MockK, Espresso, AndroidX Test

