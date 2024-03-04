package ancientmeme.pomodoro;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class PomodoroLauncher extends Application {
    /**
     * Load the scene with the given fxml files, exits program if fxml
     * cannot be loaded.
     * @param fxmlName The name of the fxml file
     */
    public Scene loadScene(String fxmlName) {
        URL fxmlURL = PomodoroLauncher.class.getResource(fxmlName);
        if (fxmlURL == null) {
            System.err.format("fxml file does not exist: %s%n", fxmlName);
            System.exit(1);
        }

        FXMLLoader fxmlLoader = new FXMLLoader(fxmlURL);
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load(), 320, 240);
        } catch (IOException e) {
            System.err.format("Cannot load fxml file: %s%n", fxmlName);
            System.exit(1);
        }
        return scene;
    }

    /**
     * Loads given stylesheet into the scene, displays error message
     * when css cannot be loaded
     * @param scene The scene to add css styling
     * @param cssName The filename for the css
     */
    public void loadCSS(Scene scene, String cssName) {
        URL cssURL = PomodoroLauncher.class.getResource(cssName);
        if (cssURL == null) {
            System.err.format("css file does not exist: %s%n", cssName);
            return;
        }

        String css = cssURL.toExternalForm();
        scene.getStylesheets().add(css);
    }

    @Override
    public void start(Stage stage) {
        Scene scene = loadScene("clock.fxml");
        loadCSS(scene, "clock.css");
        stage.setTitle("Pomodoro Clock");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}