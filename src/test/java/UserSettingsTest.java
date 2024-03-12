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
        UserSettings loadedSettings = new UserSettings();
        Assertions.assertEquals(4 * MINUTE, loadedSettings.getSessionLength());
    }

    @Test
    public void setBreakLengthTest() {
        settings.setBreakLength(2, 5);
        UserSettings loadedSettings = new UserSettings();
        Assertions.assertEquals(2 * MINUTE + 5 * SECOND, loadedSettings.getBreakLength());
    }

    @Test
    public void setIsLongBreakEnabledTest() {
        settings.setIsLongBreakEnabled(true);
        UserSettings loadedSettings = new UserSettings();
        Assertions.assertTrue(loadedSettings.getIsLongBreakEnabled());
    }

    @Test
    public void setIsLightModeEnabledTest() {
        settings.setIsLightModeEnabled(true);
        UserSettings loadedSettings = new UserSettings();
        Assertions.assertTrue(loadedSettings.getIsLightModeEnabled());
    }
}
