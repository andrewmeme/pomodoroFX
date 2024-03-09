package ancientmeme.pomodoro;

import ancientmeme.pomodoro.controller.PomodoroController;
import ancientmeme.pomodoro.controller.SettingsController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class PomodoroLauncher extends Application {
    private Scene timerScene;
    private Scene settingsScene;
    private PomodoroController timerController;
    private SettingsController settingsController;
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
    private void injectDependency() {
        // inject timer references to Controllers
        timer = new PomodoroTimer();
        timerController.setTimerReference(timer);
        settingsController.setTimerReference(timer);

        // Inject the settings Stage into TimerController
        Stage settingsStage = new Stage();
        settingsStage.setScene(settingsScene);
        timerController.setSettingsStage(settingsStage);
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
        injectDependency();

        // Run functions that requires dependency injections
        timerController.refreshDisplay();
        settingsController.loadTimerSettings();

        // Setup and display the main window
        setupPrimaryStage(primaryStage);
    }

    @Override
    public void stop() {
        timerController.shutdownController();
        timer.shutdownTimer();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch();
    }
}