package ancientmeme.pomodoro.controller;

import ancientmeme.pomodoro.PomodoroTimer;
import ancientmeme.pomodoro.util.TimerMode;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.util.Duration;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Controller for the Pomodoro Interface, interacts with a
 * PomodoroTimer and updates the interface accordingly.
 * The controller updates the current timer by receiving data from
 * PomodoroTimer and pass any user actions to it.
 */
public class PomodoroController implements Initializable {
    private static final PseudoClass CAN_PAUSE = PseudoClass.getPseudoClass("can_pause");
    private ScheduledExecutorService scheduler;
    private PomodoroTimer timer;
    private Stage timerStage;
    private Stage settingsStage;
    private MediaPlayer mediaPlayer;
    private TimerMode currentMode;
    // Offset for dragging the window
    private double xOffset;
    private double yOffset;
    @FXML
    private Text modeDisplay;
    @FXML
    private Text timerDisplay;
    @FXML
    private Button startButton;


    /**
     * Initializes the controller
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        scheduler = Executors.newScheduledThreadPool(1);
        startButton.pseudoClassStateChanged(CAN_PAUSE, false);
    }

    /**
     * Inject a reference to a PomodoroTimer, and perform initialization
     * that requires the timer
     * @param timerRef reference to a PomodoroTimer
     */
    public void setTimerReference(PomodoroTimer timerRef) {
        timer = timerRef;
        currentMode = timer.getTimerMode();
        refreshDisplay();
    }

    /**
     * Inject a reference of a media player to play the alarm sound
     * @param player the media player
     */
    public void setMediaPlayerReference(MediaPlayer player) {
        mediaPlayer = player;
    }

    /**
     * Inject a reference to the settings window
     * @param settingsStageRef reference to the settings window
     */
    public void setSettingsStage(Stage settingsStageRef) {
        settingsStage = settingsStageRef;
    }

    /**
     * Inject a reference to the timer window (self)
     * @param timerStageRef reference to the timer window
     */
    public void setTimerStage(Stage timerStageRef) {
        timerStage = timerStageRef;
    }

    /**
     * Application should call this method to shut down
     * the refresher thread gracefully
     */
    public void shutdownController() {
        scheduler.shutdownNow();
    }

    /**
     * Initialize a dedicated thread to refresh the display
     * every 50ms
     */
    private void refreshDisplay() {
        Runnable refresher = new Runnable() {
            @Override
            public void run() {
                if (timer.isPause()) {
                    return;
                }
                modeDisplay.setText(getModeText());
                timerDisplay.setText(getFormattedTime());
                playAlarm();
            }
        };
        scheduler.scheduleAtFixedRate(refresher, 0, 50, TimeUnit.MILLISECONDS);
    }

    /**
     * Plays an alarm when the timer reaches zero
     */
    private void playAlarm() {
        if (timer.getTimerMode() != currentMode) {
            mediaPlayer.stop();
            mediaPlayer.play();
            currentMode = timer.getTimerMode();
        }
    }

    /**
     * Start the timer if user pressed start, if the timer
     * has started, pause instead.
     */
    @FXML
    private void handleStart() {
        if (!timer.isTimerRunning()) {
            timer.startTimer();
            startButton.pseudoClassStateChanged(CAN_PAUSE, true);
            return;
        }

        // Only available during a session
        if (timer.isPause()) {
            timer.resumeTimer();
            startButton.pseudoClassStateChanged(CAN_PAUSE, true);
        } else {
            timer.pauseTimer();
            startButton.pseudoClassStateChanged(CAN_PAUSE, false);
        }
    }

    /**
     * Stops the timer if user pressed stop
     */
    @FXML
    private void handleStop() {
        timer.stopTimer();
        startButton.pseudoClassStateChanged(CAN_PAUSE, false);
    }

    /**
     * Opens up the settings menu
     */
    @FXML
    private void handleSettings() {
        settingsStage.setX(timerStage.getX());
        settingsStage.setY(timerStage.getY());
        settingsStage.show();
    }

    /**
     * Closes the application
     */
    @FXML
    private void handleClose() {
        timerStage.close();
    }

    /**
     * Records where the mouse clicked
     * @param event the mouse event
     */
    @FXML
    private void handleMousePress(MouseEvent event) {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }

    /**
     * Move the stage according to the mouse drag
     * @param event the mouse event
     */
    @FXML
    private void handleMouseDrag(MouseEvent event) {
        timerStage.setX(event.getScreenX() - xOffset);
        timerStage.setY(event.getScreenY() - yOffset);
    }

    /**
     * Get display text for the mode of the timer
     * @return the current mode of the timer
     */
    private String getModeText() {
        TimerMode mode = timer.getTimerMode();
        if (mode == TimerMode.SESSION) {
            return "Work Session";
        } else {
            return String.format("Break %d :)", timer.getBreakCount());
        }
    }

    /**
     * Get the remaining time in a readable format for display
     * purpose
     * @return remaining time in "mm:ss" format
     */
    private String getFormattedTime() {
        // getRemainingTime returns in milliseconds
        long remainingTime = timer.getRemainingTime();
        long remainingSeconds = Math.floorDiv(remainingTime, 1000);
        long minutes = remainingSeconds / 60;
        long seconds = remainingSeconds % 60;

        return String.format("%02d:%02d", minutes, seconds);
    }
}