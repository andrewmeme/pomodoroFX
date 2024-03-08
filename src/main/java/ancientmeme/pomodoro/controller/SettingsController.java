package ancientmeme.pomodoro.controller;

import ancientmeme.pomodoro.PomodoroTimer;
import ancientmeme.pomodoro.util.SettingsStringConverter;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

public class SettingsController implements Initializable {
    private PomodoroTimer timer;
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
    private Button saveSettingsButton;

    @Override
    public void initialize(URL _url, ResourceBundle _rb) {
        setupTextFilter();

        // these symbols cause error for FXMLLoader
        sessionDecreaseButton.setText("<");
        sessionIncreaseButton.setText(">");
        breakDecreaseButton.setText("<");
        breakIncreaseButton.setText(">");
    }

    /**
     * Inject a reference to a PomodoroTimer
     * @param timerRef reference to a PomodoroTimer
     */
    public void setTimerReference(PomodoroTimer timerRef) {
        timer = timerRef;
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
        timer.setSessionLength(sessionFormatter.getValue(), 0);
        timer.setBreakLength(breakFormatter.getValue(), 0);

        Stage stage = (Stage) saveSettingsButton.getScene().getWindow();
        stage.hide();
    }

    private void changeSessionLength(long shift) {
        long sessionLength = Long.parseLong(sessionLengthField.getText());
        long finalValue = Math.min(Math.max(1, sessionLength + shift), 60);
        sessionFormatter.setValue(finalValue);
    }

    private void changeBreakLength(long shift) {
        long breakLength = Long.parseLong(breakLengthField.getText());
        long finalValue = Math.min(Math.max(1, breakLength + shift), 60);
        breakFormatter.setValue(finalValue);
    }

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
}
