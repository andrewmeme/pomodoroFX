package ancientmeme.pomodoro.util;

import javafx.util.converter.LongStringConverter;

public class SettingsStringConverter extends LongStringConverter {
    @Override
    public Long fromString(String value) {
        return Math.min(super.fromString(value), 60);
    }

    @Override
    public String toString(Long value) {
        return super.toString(value);
    }
}
