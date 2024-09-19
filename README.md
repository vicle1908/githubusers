
# GitHub Users Android App

This project is a sample Android app that demonstrates the following technologies:

- Kotlin
- Jetpack Compose for UI
- MVI Architecture
- Room for local caching
- Ktor for network calls
- Hilt for dependency injection
- Kotlin Flow for reactive programming

## Features

1. Fetch and display a list of GitHub users.
2. Scroll to load more users, with 20 users per fetch.
3. Display user details on clicking an item.
4. Cache data locally using Room.
5. Display data from the cache when offline or on subsequent app launches.

## Project Structure

- **data**: Contains network, DAO, Repository, and Database classes.
- **domain**: Data model representing GitHub users.
- **presentation**: Composables for displaying GitHub users in a list and details view.
- **viewmodel**: The ViewModel that follows the MVI pattern to manage state.

## Instructions

1. Clone the repository.
2. Open the project in Android Studio.
3. Build and run the app on an emulator or physical device.
4. Ensure internet connectivity for fetching GitHub users.

## License

This project is licensed under the MIT License.
    