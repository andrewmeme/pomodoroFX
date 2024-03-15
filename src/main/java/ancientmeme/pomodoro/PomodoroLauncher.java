package ancientmeme.pomodoro;

import ancientmeme.pomodoro.controller.PomodoroController;
import ancientmeme.pomodoro.controller.SettingsController;
import ancientmeme.pomodoro.util.Loader;
import ancientmeme.pomodoro.settings.UserSettings;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * The starting point of the application, loads all the windows required
 * and user preferences.
 */
public class PomodoroLauncher extends Application {
    private Stage timerStage;
    private Stage settingsStage;
    private Scene timerScene;
    private Scene settingsScene;
    private PomodoroController timerController;
    private SettingsController settingsController;
    private UserSettings userSettings;
    private PomodoroTimer timer;

    /**
     * Load Scenes and Controllers from fxml files.
     */
    private void getSceneAndController() {
        FXMLLoader timerLoader = new FXMLLoader();
        timerScene = Loader.loadFXMLFile(timerLoader, "clock.fxml", 320, 360);
        timerController = timerLoader.getController();

        FXMLLoader settingsLoader = new FXMLLoader();
        settingsScene = Loader.loadFXMLFile(settingsLoader, "settings.fxml", 320, 360);
        settingsController = settingsLoader.getController();
    }

    /**
     * Inject dependency for the controllers
     */
    private void injectDependency() {
        // inject user settings into timer and settingController
        userSettings = new UserSettings();
        timer = new PomodoroTimer();
        timer.setSettingsReference(userSettings);
        settingsController.setSettingsReference(userSettings);

        // inject references to Controllers
        timerController.setTimerReference(timer);
        timerController.setSettingsReference(userSettings);
        timerController.setMediaPlayerReference(Loader.loadMedia("audio/alarm.mp3"));

        // Inject Stage references into TimerController
        settingsStage = new Stage();
        settingsStage.setScene(settingsScene);
        settingsStage.initStyle(StageStyle.UNDECORATED);

        timerController.setSettingsStage(settingsStage);
        timerController.setTimerStage(timerStage);
    }

    private void setupSettingsListeners() {
        userSettings.addListener(timerController);
        userSettings.addListener(settingsController);
    }

    private void setupPrimaryStage() {
        timerStage.setOnHidden(e -> {
            settingsStage.close();
        });
        timerStage.initStyle(StageStyle.UNDECORATED);
        timerStage.setResizable(false);
        timerStage.setTitle("Pomodoro Clock");
        timerStage.setScene(timerScene);
        timerStage.show();
    }

    private void applyStageSettings() {
        timerStage.setOnShown(e -> {
            timerController.settingsChanged();
        });

        settingsStage.setOnShown(e -> {
            settingsController.settingsChanged();
        });
    }


    @Override
    public void start(Stage primaryStage) {
        timerStage = primaryStage;

        // Loads the scenes and controller
        getSceneAndController();

        // Injects dependencies into the controllers
        injectDependency();

        // Add controllers as observers for settings change
        setupSettingsListeners();

        // Load settings after window is shown
        applyStageSettings();

        // Setup and display the main window
        setupPrimaryStage();
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