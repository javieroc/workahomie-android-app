# Workahomie

![Workahomie Logo](app/src/main/res/drawable/logo.xml)

**Find your next home office, away from home. Workahomie is a modern Android application designed to connect digital nomads, remote workers, and travelers with unique hosts offering accommodation and a productive workspace.**

---

## Core Concept

In an era of remote work, people are no longer tied to a single location. Workahomie bridges the gap between the desire for travel and the need for a reliable work environment. It's a mobile-first platform where hosts can list their available spaces (a room, an apartment, a desk) and users ("Workahomies") can discover and book these unique locations for a combined living and working experience.

The app fosters a community of like-minded individuals, enabling a lifestyle that blends work, travel, and local experiences.

## Key Features

Workahomie is packed with features to ensure a seamless and enjoyable user experience from discovery to booking.

*   **🗺️ Discover & Explore:**
    *   **Dual View:** Browse host listings in a traditional list format or on a dynamic, interactive map.
    *   **Advanced Search:** Find the perfect spot by searching for specific locations or addresses.
    *   **Smart Filtering:** Narrow down your options by filtering based on available facilities like Wi-Fi, parking, coffee, and more.

*   **🏡 Detailed Listings:**
    *   **Immersive Galleries:** View high-quality photo galleries for each host location.
    *   **Comprehensive Details:** Get all the information you need, including a detailed description of the space, a list of all included facilities, and a profile of the host.
    *   **Host Profiles:** Learn more about your potential host, including their occupation and a short bio.

*   **❤️ Personalized Wishlists:**
    *   Keep track of interesting places by adding them to your personal wishlist.
    *   Easily access and manage your saved listings in a dedicated screen.

*   **✍️ Seamless Booking & Requests:**
    *   **Request to Stay:** Effortlessly send a booking request to a host by selecting your desired check-in and check-out dates and adding a personal message.
    *   **Direct Contact:** Use the integrated WhatsApp link to quickly communicate with hosts for any immediate questions.

*   **🔔 Real-time Notifications:**
    *   Stay informed with push notifications about the status of your booking requests, powered by Firebase Cloud Messaging.

## Design & Architecture

This application is built from the ground up using the latest standards and best practices in modern Android development. The architecture is intentionally designed to be robust, scalable, and easy to maintain.

*   **📱 100% Kotlin & Jetpack Compose:**
    *   The entire application is written in Kotlin, leveraging its conciseness and safety features.
    *   The UI is built declaratively with **Jetpack Compose**, Google's modern UI toolkit. This results in a more reactive, less complex, and highly maintainable UI codebase.

*   **🏗️ MVVM Architecture (Model-View-ViewModel):**
    *   The app follows a strict **MVVM** pattern, ensuring a clean separation of concerns.
    *   **View (`Composable` Screens):** The UI layer is purely responsible for displaying data and forwarding user events. It is stateless and reacts to changes from the ViewModel.
    *   **ViewModel:** Manages and holds the UI state. It handles business logic, prepares data for the UI, and survives configuration changes.
    *   **Model:** Represents the data layer, handling data retrieval from the network and managing the application's data sources.

*   **🌐 State-Driven & Reactive UI:**
    *   The UI is built as a function of state. As the data in the ViewModels changes (e.g., hosts are loaded, a filter is applied), the Composable UI automatically and efficiently updates to reflect the new state. This is achieved using `StateFlow` and Compose's state management system.

*   **⚡ Asynchronous Operations with Coroutines:**
    *   All background tasks, such as network requests and database access, are handled using **Kotlin Coroutines**. This ensures the main thread is never blocked, resulting in a smooth and responsive user experience.

*   **📡 Networking & Data:**
    *   **Retrofit:** Used as the type-safe HTTP client for communicating with the backend API.
    *   **Coil:** A modern, coroutine-based image loading library used to efficiently load and display images throughout the app.
    *   **Jetpack Navigation:** All navigation between screens is handled using the Navigation component for Compose, providing a consistent and predictable navigation flow.

---

This project serves as a strong example of a full-featured, modern Android application built with a focus on quality, user experience, and developer-friendly architecture.