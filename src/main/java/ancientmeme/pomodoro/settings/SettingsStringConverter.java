package ancientmeme.pomodoro.settings;

import javafx.util.converter.LongStringConverter;

public class SettingsStringConverter extends LongStringConverter {
    @Override
    public Long fromString(String input) {
        return Math.max(Math.min(super.fromString(input), 60), 1);
    }

    @Override
    public String toString(Long value) {
        return super.toString(value);
    }
}
