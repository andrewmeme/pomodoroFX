module ancientmeme.pomodoro {
    requires javafx.controls;
    requires javafx.fxml;


    opens ancientmeme.pomodoro to javafx.fxml;
    exports ancientmeme.pomodoro;
}