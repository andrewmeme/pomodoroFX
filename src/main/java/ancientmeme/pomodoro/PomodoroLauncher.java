package ancientmeme.pomodoro;

import ancientmeme.pomodoro.controller.PomodoroController;
import ancientmeme.pomodoro.controller.SettingsController;
import ancientmeme.pomodoro.util.UserSettings;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;

public class PomodoroLauncher extends Application {
    private Scene timerScene;
    private Scene settingsScene;
    private Stage settingsStage;
    private PomodoroController timerController;
    private SettingsController settingsController;
    private UserSettings userSettings;
    private PomodoroTimer timer;

    /**
     * Load Scenes and Controllers from fxml files.
     */
    private void getSceneAndController() {
        FXMLLoader timerLoader = new FXMLLoader();
        timerScene = loadFXMLFile(timerLoader, "clock.fxml", 320, 360);
        timerController = timerLoader.getController();

        FXMLLoader settingsLoader = new FXMLLoader();
        settingsScene = loadFXMLFile(settingsLoader, "settings.fxml", 320, 360);
        settingsController = settingsLoader.getController();
    }

    /**
     * Inject dependency for the controllers
     */
    private void injectDependency(Stage timerStage) {
        // inject user settings into timer and settingController
        timer = new PomodoroTimer();
        userSettings = new UserSettings();
        timer.setSettingsReference(userSettings);
        settingsController.setSettingsReference(userSettings);

        // inject timer references to Controllers
        timerController.setTimerReference(timer);

        // Inject Stage references into TimerController
        settingsStage = new Stage();
        settingsStage.setScene(settingsScene);
        settingsStage.initStyle(StageStyle.UNDECORATED);

        timerController.setSettingsStage(settingsStage);
        timerController.setTimerStage(timerStage);
    }

    /**
     * Loads the FXML file requested and returns a Scene object
     * in the dimension specified. Failure to load would result in
     * process exiting.
     *
     * @param loader FXMLLoader for loading fxml files
     * @param fileName the name of the file
     * @param width width of the scene
     * @param height height of the scene
     * @return a Scene object with specified dimensions
     */
    private Scene loadFXMLFile(FXMLLoader loader, String fileName, double width, double height) {
        Scene scene = null;
        URL fileURL = PomodoroLauncher.class.getResource(fileName);

        try {
            loader.setLocation(fileURL);
            scene = new Scene(loader.load(), width, height);
        } catch (IOException e) {
            System.err.format("Cannot load FXML file: %s%n", fileName);
            System.exit(1);
        }
        return scene;
    }

    private void setupPrimaryStage(Stage primaryStage) {
        primaryStage.setOnHidden(e -> {
            settingsStage.close();
        });
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Pomodoro Clock");
        primaryStage.setScene(timerScene);
        primaryStage.show();
    }

    @Override
    public void start(Stage primaryStage) {
        // Loads the scenes and controller
        getSceneAndController();

        // Injects dependencies into the controllers
        injectDependency(primaryStage);

        // Setup and display the main window
        setupPrimaryStage(primaryStage);
    }

    @Override
    public void stop() {
        // Stop all threads before shutting down the application
        timerController.shutdownController();
        timer.shutdownTimer();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch();
    }
}