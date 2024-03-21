# PomodoroFX
---
A pomodoro clock application built in Java 11 with JavaFX.

Pomodoro technique is a time management method to increase productivity.
It employs the 25 minutes work session, 5 minutes break cycle to achieve optimal
concentration during tasks.

![Dark Mode](/src/main/resources/ancientmeme/pomodoro/example/dark-mode.png)
![Light Mode](/src/main/resources/ancientmeme/pomodoro/example/light-mode.png)

## Application Features
PomodoroFX allows the user to:
- Changing the work session and break length
- Optional long break can be toggled for every 4 work sessions
- Light and dark mode support
- Allow the window to always be on top

## Build Project
The repository has a binary JAR file included. However, if you
wish to build the project yourself, simply run this command inside the project folder:
```
mvn package
```
Remember to check if JAVA_HOME on your machine is at least Java 11 or 
later versions if the build failed.

## License
PomodoroJX is under [MIT License](LICENSE).