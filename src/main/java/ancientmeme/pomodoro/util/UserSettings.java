package ancientmeme.pomodoro.util;

import java.util.prefs.Preferences;
import static ancientmeme.pomodoro.PomodoroTimer.MINUTE;
import static ancientmeme.pomodoro.PomodoroTimer.SECOND;

public class UserSettings {
    private final String SESSION_KEY = "SESSION_LENGTH";
    private final String BREAK_KEY = "BREAK_LENGTH";
    private final String LONG_BREAK_KEY = "LONG_BREAK";
    private final String LIGHT_MODE_KEY = "LIGHT_MODE";
    // User preference for the application
    private final Preferences pref;
    private long sessionLength;
    private long breakLength;
    private boolean isLongBreakEnabled;
    private boolean isLightModeEnabled;

    public UserSettings() {
        pref = Preferences.userNodeForPackage(ancientmeme.pomodoro.util.UserSettings.class);
        sessionLength = pref.getLong(SESSION_KEY, 25);
        breakLength = pref.getLong(BREAK_KEY, 5);
        isLongBreakEnabled = pref.getBoolean(LONG_BREAK_KEY, false);
        isLightModeEnabled = pref.getBoolean(LIGHT_MODE_KEY, false);
    }

    public void setSessionLength(long minutes, long seconds) {
        sessionLength = minutes * MINUTE + seconds * SECOND;
        pref.putLong(SESSION_KEY, sessionLength);
    }

    public long getSessionLength() {
        return sessionLength;
    }

    public void setBreakLength(long minutes, long seconds) {
        breakLength = minutes * MINUTE + seconds * SECOND;
        pref.putLong(BREAK_KEY, breakLength);
    }

    public long getBreakLength() {
        return breakLength;
    }

    public void setIsLongBreakEnabled(boolean value) {
        isLongBreakEnabled = value;
        pref.putBoolean(LONG_BREAK_KEY, isLongBreakEnabled);
    }

    public boolean isLongBreakEnabled() {
        return isLongBreakEnabled;
    }

    public void setIsLightModeEnabled(boolean value) {
        isLightModeEnabled = value;
        pref.putBoolean(LIGHT_MODE_KEY, isLightModeEnabled);
    }

    public boolean isLightModeEnabled() {
        return isLightModeEnabled;
    }

    public void resetDefaultSettings() {
        setSessionLength(25, 0);
        setBreakLength(5, 0);
        setIsLongBreakEnabled(false);
        setIsLightModeEnabled(false);
    }
}
