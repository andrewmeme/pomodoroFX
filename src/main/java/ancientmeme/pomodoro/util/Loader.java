package ancientmeme.pomodoro.util;

import ancientmeme.pomodoro.PomodoroLauncher;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * A loader that contains all the methods of loading required files
 * for the application
 */

public class Loader {
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
    public static Scene loadFXMLFile(FXMLLoader loader, String fileName, double width, double height) {
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

    /**
     * Load media with the given file
     * @param filename the file name for the media
     * @return a media player ready to play the loaded file
     */
    public static MediaPlayer loadMedia(String filename) {
        MediaPlayer player = null;
        try {
            String resource = PomodoroLauncher.class.getResource(filename).toURI().toString();
            Media media = new Media(resource);
            player = new MediaPlayer(media);
        } catch (URISyntaxException | NullPointerException e) {
            System.err.format("Cannot load audio file");
        }
        return player;
    }
}
