# Cebolão LotoFácil Generator

## Purpose

Cebolão LotoFácil Generator is an Android application designed to assist users in generating and managing their LotoFácil game combinations. LotoFácil is a popular Brazilian lottery game where players choose 15 to 20 numbers out of 25. This app provides tools to generate random sets, save them, filter results, and manage favorite games, aiming to enhance the user's experience with the LotoFácil lottery.

## Features

### 1. Game Generation

*   **Random Number Generation:** Generate random combinations of numbers within the LotoFácil range (1 to 25).
*   **Customizable Sets:** Choose the number of games to generate at once.
* **Number of Bets:** Choose how many numbers you want to bet (15 to 20).

### 2. Game Management

*   **Saving Games:** Save generated games for future reference.
*   **Favorites:** Mark specific games as favorites for quick access.
*   **Game Listing:** View a list of all saved and generated games.

### 3. Filters

*   **Odd/Even Numbers:** Filter generated games based on the desired number of odd and even numbers.
*   **Prime Numbers:** Filter games based on the inclusion or exclusion of specific prime numbers.
*   **Sum of Numbers:** Filter games based on the sum of the numbers.
* **Custom numbers:** Filter games based on the inclusion of specific numbers.
* **Exclusion numbers:** Filter games based on the exclusion of specific numbers.

### 4. Settings

*   **App Theme:** Choose between light and dark themes for the app.
*   **Game Number Limits:** Set the default and maximum numbers of games to generate.
*   **Clear Data:** Clear all saved games and application settings.

### 5. User Interface

*   **Intuitive Navigation:** Easy-to-use interface with clear navigation between different sections.
*   **Responsive Design:** Optimized for various screen sizes and orientations.
* **Welcome Screen:** Shows an overview of the app's features.

### 6. Data Storage

* **Local Database:** Uses a local database to persist generated games and user preferences.
* **Data Persistence:** Data is retained across app sessions.

### 7. Technical Features

*   **Kotlin:** Developed using the Kotlin programming language for Android.
*   **Jetpack Compose:** Uses Jetpack Compose for building the UI efficiently.
*   **Room:** Employs the Room library for database management.
*   **DataStore:** Uses the DataStore to store preferences.
* **Clean Architecture:** Follows the Clean Architecture guidelines for better code maintainability.
* **ViewModel:** Follows the MVVM guidelines for better data handling and UI updates.

### 8. Utils

* **LotofacilUtils:** Provides useful functions for Lotofacil games.
* **Utils:** Provides useful functions for the entire app.

## Usage Instructions

1.  **Generate Games:**
    *   Navigate to the "Generate Games" section.
    *   Select the desired number of games to generate.
    *   Tap the "Generate" button.

2.  **View Games:**
    *   Go to the "Generated Games" section to see a list of generated games.

3.  **Add to Favorites:**
    *   From the "Generated Games" section, select a game.
    *   Tap the "Favorite" icon to add it to your favorites.

4. **View favorites:**
    * Go to the "Favorites" section to see all your favorite games.

5.  **Apply Filters:**
    *   Go to the "Filters" section.
    *   Select desired filters (e.g., odd/even, prime numbers, sum of numbers).
    *  Select custom and exclusion numbers.
    *   Tap "Apply Filters".

6.  **Access Settings:**
    *   Go to the "Settings" section to customize themes, game limits, and more.

## Contributing

We welcome contributions to enhance this app! If you'd like to contribute:

1.  Fork the repository.
2.  Create a new branch for your feature or bug fix.
3.  Submit a pull request.

## Support

If you encounter any issues or have questions, please contact us via email at [your-email@example.com].

## License

This project is licensed under the [MIT License](LICENSE) - see the `LICENSE` file for details.