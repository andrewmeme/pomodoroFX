module ancientmeme.pomodoro {
    requires javafx.controls;
    requires javafx.fxml;


    opens ancientmeme.pomodoro to javafx.fxml;
    exports ancientmeme.pomodoro;
    exports ancientmeme.pomodoro.controller;
    opens ancientmeme.pomodoro.controller to javafx.fxml;
    exports ancientmeme.pomodoro.util;
    opens ancientmeme.pomodoro.util to javafx.fxml;
}