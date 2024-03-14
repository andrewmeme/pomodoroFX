package ancientmeme.pomodoro.controller;

import ancientmeme.pomodoro.PomodoroTimer;
import ancientmeme.pomodoro.settings.SettingsListener;
import ancientmeme.pomodoro.settings.SettingsStringConverter;
import ancientmeme.pomodoro.settings.UserSettings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

public class SettingsController implements Initializable, SettingsListener {
    private UserSettings settings;
    private TextFormatter<Long> sessionFormatter;
    private TextFormatter<Long> breakFormatter;

    @FXML
    private TextField sessionLengthField;
    @FXML
    private TextField breakLengthField;
    @FXML
    private Button sessionDecreaseButton;
    @FXML
    private Button sessionIncreaseButton;
    @FXML
    private Button breakDecreaseButton;
    @FXML
    private Button breakIncreaseButton;
    @FXML
    private ToggleButton longBreakButton;
    @FXML
    private ToggleButton lightModeButton;
    @FXML
    private ToggleButton onTopButton;

    @Override
    public void initialize(URL _url, ResourceBundle _rb) {
        setupTextFilter();

        // these symbols cause error for FXMLLoader
        sessionDecreaseButton.setText("<");
        sessionIncreaseButton.setText(">");
        breakDecreaseButton.setText("<");
        breakIncreaseButton.setText(">");
    }

    @Override
    public void settingsChanged() {
        System.out.println("settings got signal");
        // Check if always on top is true
        setAlwaysOnTop();
    }

    /**
     * Inject the user preference for the application
     */
    public void setSettingsReference(UserSettings settingsRef) {
        settings = settingsRef;
        loadUserSettings();
    }

    /**
     * Load the saved timer settings after getting the reference for the current timer
     */
    private void loadUserSettings() {
        sessionFormatter.setValue(settings.getSessionLength() / PomodoroTimer.MINUTE);
        breakFormatter.setValue(settings.getBreakLength() / PomodoroTimer.MINUTE);
        longBreakButton.setSelected(settings.isLongBreakEnabled());
        lightModeButton.setSelected(settings.isLightModeEnabled());
        onTopButton.setSelected(settings.isAlwaysOnTop());
    }

    @FXML
    private void handleSessionDecrease() {
        changeSessionLength(-1);
    }

    @FXML
    private void handleSessionIncrease() {
        changeSessionLength(1);
    }

    @FXML
    private void handleBreakDecrease() {
        changeBreakLength(-1);
    }

    @FXML
    private void handleBreakIncrease() {
        changeBreakLength(1);
    }

    @FXML
    private void handleSaveSettings() {
        settings.setSessionLength(sessionFormatter.getValue(), 0);
        settings.setBreakLength(breakFormatter.getValue(), 0);
        settings.setIsLongBreakEnabled(longBreakButton.isSelected());
        settings.setIsLightModeEnabled(lightModeButton.isSelected());
        settings.setIsAlwaysOnTop(onTopButton.isSelected());
        settings.notifySettingsUpdate();
    }

    @FXML
    private void handleResetSettings() {
        settings.resetDefaultSettings();
        loadUserSettings();
    }

    @FXML
    private void handleCloseSettings() {
        // Reset the interface to the saved settings
        loadUserSettings();
        Stage settingsStage = (Stage) lightModeButton.getScene().getWindow();
        settingsStage.hide();
    }

    /**
     * Shift the length of a session according to the value. The value
     * has to be between 1 and 60
     * @param shift the value to shift session length
     */
    private void changeSessionLength(long shift) {
        long sessionLength = Long.parseLong(sessionLengthField.getText());
        long finalValue = Math.min(Math.max(1, sessionLength + shift), 60);
        sessionFormatter.setValue(finalValue);
    }

    /**
     * Shift the length of a break according to the value. The value
     * has to be between 1 and 60
     * @param shift the value to shift break length
     */
    private void changeBreakLength(long shift) {
        long breakLength = Long.parseLong(breakLengthField.getText());
        long finalValue = Math.min(Math.max(1, breakLength + shift), 60);
        breakFormatter.setValue(finalValue);
    }

    /**
     * Set up the text filters for the TextField in the setting menu to
     * only allow valid inputs
     */
    private void setupTextFilter() {
        UnaryOperator<TextFormatter.Change> integerFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("([1-9][0-9]?)?")) {
                return change;
            }
            return null;
        };

        // Each TextField requires its own instance of TextFormatter
        sessionFormatter =
                new TextFormatter<>(new SettingsStringConverter(), 25L, integerFilter);
        sessionLengthField.setTextFormatter(sessionFormatter);

        breakFormatter =
                new TextFormatter<>(new SettingsStringConverter(), 25L, integerFilter);
        breakLengthField.setTextFormatter(breakFormatter);
    }

    private void setAlwaysOnTop() {
        Stage settingsStage = (Stage) lightModeButton.getScene().getWindow();
        settingsStage.setAlwaysOnTop(settings.isAlwaysOnTop());
    }
}
