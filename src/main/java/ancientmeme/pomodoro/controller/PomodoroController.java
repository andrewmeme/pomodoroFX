package ancientmeme.pomodoro.controller;

import ancientmeme.pomodoro.PomodoroTimer;
import ancientmeme.pomodoro.util.TimerMode;
import javafx.css.PseudoClass;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
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
    // Offset for dragging the window
    private double xOffset;
    private double yOffset;
    private Stage settingsStage;
    @FXML
    private Text modeDisplay;
    @FXML
    private Text timerDisplay;
    @FXML
    private Button startButton;


    /**
     * Initializes the controller with a default
     * PomodoroTimer attached to it
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
        refreshDisplay();
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
            }
        };
        scheduler.scheduleAtFixedRate(refresher, 0, 50, TimeUnit.MILLISECONDS);
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
        settingsStage.close();
        timerStage.close();
    }

    @FXML
    private void handleMousePress(MouseEvent event) {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }

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
            return "Break :)";
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