# AndroidMemory Design Document
AndroidMemory is a game that displays a number of cards on the screen with the visible side hidden. The game can be played in a Practice Mode or in a Challenge Mode. When the player starts a game in the Practice Mode he will be able to try to guess the cards and advances to try the next level when three games in the current level are completed. When the player start a game in the Challenge Mode, this player has a maximum number of tries and a limit in time before the current level is lost. When this happens the player has to redo this current level. For challenges applies the same rule that three games in the current level have to be successfully completed before he may advance to the next level.

To make the game more difficult or change the appearence he can change a number of different settings. There are two kinds of settings: Global settings and Challenge settings. In the Global settings the user can select the theme for the game and the number of cards a set contains to make the games more difficult. In the Challenge settings the player can select a maximum number of guesses and a maximum timelimit before the game is over.

# Supported platform
The AndroidMemory project is designed and build for Android smartphones supported by Android version 5.0 and higher.

### Version
0.1

### Tech
The game consists of the following activities:
* MainMenu
* Gameplay
* Settings

The underlying logic that applies to the gameplay will be handled by a set of models. The design of these models can be found in the classdiagram located at
[GitHub](https://raw.githubusercontent.com/ruben-kruiver/AndroidMemory/master/Documentation/Classdiagram.pdf)

In this design there is chosen to intergrate a factory method in the model class Game that creates the cards that will be used in the current game. This model class has been chosen because it has the most relevant information about the cards that need to be used in the game, aswell as the size and layout of the grid.

A persistence model is intergrated that is able to keep track of the actions of the current game to make it possible to persist its state so that i can be recreated if the game is started after it has stopped in a previous session.

### Styleguide
The following styles are applied in the proprietary code of the HangMan application. The code is fully written in Java in combination with the XML format for the Android resources.

**Java**
- A packagename should be a singular word and always in lowercase
- A classes and variable should be a single word describing it's purpose and written in camelcase
- Class constants should be written fully in uppercase where each space is replaced by an underscore
- Abstract classes and interfaces should be placed inside the basemodel package
- Exceptions should be placed in the exception package
- All model classes should be placed inside the model package
- All view classes should be placed inside the view package
- Dependency classess should be placed inside a new package folder named after it's dependent main class
- The scope of each class attribute should be at least protected with the exception of class constants
- The scope of a method should be protected unless outside access is needed. A method should only be private if it may not be overridden by extension
- Each class and method should be provided with commentary explaining the goal of the method or class, with exception of private classes/methods and overridden methods
- Each level of indentation should be written by using 4 empty spaces
- A method length should not exceed 20 lines of executable code unless the reason is specified in the commentary of the method
- If the number of statements within a method of struct is not larger than 1, these may be placed on a single line

**XML**
- For layout files each tag should be placed on a seperate line then it's attributes. These attributes must be placed on a seperate line with an extra indentation of 4 spaces. Before each opening tag must be one blank line.
- Each node within a parent node must be prefixed with 4 space more that it's parent node

### Packages
AndroidMemory uses only standard Java and Android libraries. The logic side of the application has been built on proprietary code and is therefore save to run on the platform without modifications.

## Rights
The application doesn't require any extra rights to function properly. It also doesn't need an internet connection in order for it to work.

### Screen sketches
The following screens would be visible for the player:

![Menu](https://raw.githubusercontent.com/ruben-kruiver/AndroidMemory/master/Documentation/images/Menu.png "Main Menu")
![Gameplay practice choice](https://raw.githubusercontent.com/ruben-kruiver/AndroidMemory/master/Documentation/images/PracticeChoice.png "Choice screen for practice game")
![Gameplay practice](https://raw.githubusercontent.com/ruben-kruiver/AndroidMemory/master/Documentation/images/Practice.png "Practice game")
![Gameplay challenge](https://raw.githubusercontent.com/ruben-kruiver/AndroidMemory/master/Documentation/images/Challenge.png "Challenge game")
![Settings](https://raw.githubusercontent.com/ruben-kruiver/AndroidMemory/master/Documentation/images/Settings.png "Settings screen")