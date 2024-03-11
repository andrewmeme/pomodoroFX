import ancientmeme.pomodoro.util.UserSettings;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static ancientmeme.pomodoro.PomodoroTimer.MINUTE;
import static ancientmeme.pomodoro.PomodoroTimer.SECOND;

public class UserSettingsTest {
    private UserSettings settings;

    @BeforeEach
    public void setup() {
        settings = new UserSettings();
    }

    @Test
    public void setSessionLengthTest() {
        settings.setSessionLength(4, 0);
        Assertions.assertEquals(4 * MINUTE, settings.getSessionLength());
    }

    @Test
    public void setBreakLengthTest() {
        settings.setBreakLength(2, 5);
        Assertions.assertEquals(2 * MINUTE + 5 * SECOND, settings.getBreakLength());
    }

    @Test
    public void setIsLongBreakEnabledTest() {
        settings.setIsLongBreakEnabled(true);
        Assertions.assertTrue(settings.getIsLongBreakEnabled());
    }

    @Test
    public void setIsLightModeEnabledTest() {
        settings.setIsLightModeEnabled(true);
        Assertions.assertTrue(settings.getIsLightModeEnabled());
    }
}
