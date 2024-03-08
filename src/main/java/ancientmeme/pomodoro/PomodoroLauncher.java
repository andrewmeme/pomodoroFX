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
    private PomodoroController timerController;
    private SettingsController settingsController;
    private PomodoroTimer timer;

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

    private void setPrimaryStageProperties(Stage primaryStage) {
        primaryStage.setResizable(false);
        primaryStage.setTitle("Pomodoro Clock");
    }


    @Override
    public void start(Stage primaryStage) {
        // Loads the scenes
        FXMLLoader timerLoader = new FXMLLoader();
        Scene timerScene = loadFXMLFile(timerLoader, "clock.fxml", 320, 360);
        timerController = timerLoader.getController();

        FXMLLoader settingsLoader = new FXMLLoader();
        Scene settingsScene = loadFXMLFile(settingsLoader, "settings.fxml", 320, 360);
        settingsController = settingsLoader.getController();

        // inject timer references to Controllers
        timer = new PomodoroTimer();
        timerController.setTimerReference(timer);
        settingsController.setTimerReference(timer);

        Stage settingsStage = new Stage();
        settingsStage.setScene(settingsScene);
        timerController.setSettingsStage(settingsStage);
        timerController.refreshDisplay();

        setPrimaryStageProperties(primaryStage);
        primaryStage.setScene(timerScene);
        primaryStage.show();
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