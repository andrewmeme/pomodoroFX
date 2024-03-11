
import ancientmeme.pomodoro.PomodoroTimer;
import ancientmeme.pomodoro.util.TimerMode;
import ancientmeme.pomodoro.util.UserSettings;
import org.junit.jupiter.api.*;

public class PomodoroTimerTest {
    private PomodoroTimer timer;
    private UserSettings settings;
    private long savedSessionLength;
    private long savedBreakLength;
    private final long SECOND = 1000;
    private final long MINUTE = 60 * SECOND;
    private final long _defaultSessionLength = 25 * MINUTE;
    private final long _acceptableMargin = 75;

    @BeforeEach
    public void setupTest() {
        timer = new PomodoroTimer();
        settings = new UserSettings();
        savedSessionLength = settings.getSessionLength();
        savedBreakLength = settings.getBreakLength();
        settings.setSessionLength(25, 0);
        settings.setBreakLength(5, 0);

        timer.setSettingsReference(settings);
    }

    @AfterEach
    public void teardownTest() {
        timer.shutdownTimer();
        settings.setSessionLength(savedSessionLength / MINUTE, savedSessionLength % MINUTE);
        settings.setBreakLength(savedBreakLength / MINUTE, savedBreakLength % MINUTE);
    }

    @Test
    public void defaultRemainingTimeTest() {
        Assertions.assertEquals(25 * MINUTE, timer.getRemainingTime());
    }

    @Test
    public void startTimerTest() {
        timer.startTimer();
        sleep(1000);

        assertInRange(_defaultSessionLength, 1000);
    }

    @Test
    public void pauseTimerTest() {
        timer.startTimer();
        sleep(1000);
        timer.pauseTimer();
        sleep(500);

        assertInRange(_defaultSessionLength, 1000);
    }

    @Test
    public void resumeTimerTest() {
        timer.startTimer();
        sleep(2000);
        timer.pauseTimer();
        sleep(1000);
        timer.resumeTimer();
        sleep(1000);

        assertInRange(_defaultSessionLength, 3000);
    }

    @Test
    public void stopTimerTest() {
        timer.startTimer();
        sleep(1000);
        timer.stopTimer();
        Assertions.assertEquals(25 * MINUTE, timer.getRemainingTime());
    }

    @Test
    public void setTimerPropertyTest() {
        timer.setSessionLength(5, 0);
        timer.setBreakLength(2, 0);
        Assertions.assertEquals(5 * MINUTE, timer.getSessionLength());
        Assertions.assertEquals(2 * MINUTE, timer.getBreakLength());

        // GetRemainingTime should change to reflect new settings
        Assertions.assertEquals(5 * MINUTE, timer.getRemainingTime());
    }

    @Test
    public void resetTimerTest() {
        timer.setSessionLength(6, 0);
        timer.setBreakLength(3, 0);
        timer.startTimer();
        sleep(SECOND);
        timer.resetTimer();

        Assertions.assertEquals(_defaultSessionLength, timer.getRemainingTime());
        Assertions.assertEquals(25 * MINUTE, timer.getSessionLength());
        Assertions.assertEquals(5 * MINUTE, timer.getBreakLength());
    }

    @Test
    public void pauseOverSessionLengthTest() {
        timer.setSessionLength(0, 2);
        timer.startTimer();
        timer.pauseTimer();
        sleep(3 * SECOND);
        assertInRange(2 * SECOND, 0);

        // start the timer again and confirm it works correctly
        timer.resumeTimer();
        sleep(SECOND);
        assertInRange(2 * SECOND, SECOND);
    }

    @Test
    public void repeatedRestartTest() {
        for (int i = 0; i < 20; ++i) {
            timer.startTimer();
            sleep(60);
            timer.stopTimer();
        }
        timer.startTimer();
        sleep(SECOND);
        assertInRange(_defaultSessionLength, SECOND);
    }

    @Test
    public void restartDuringPauseTest() {
        timer.startTimer();
        timer.pauseTimer();
        timer.stopTimer();

        timer.startTimer();
        sleep(SECOND);
        assertInRange(_defaultSessionLength, SECOND);
    }

    @Test
    public void stopAfterFinishedTest() {
        timer.setSessionLength(0, 1);
        timer.startTimer();
        sleep(1200);
        try {
            timer.stopTimer();
        } catch (Exception e) {
            Assertions.fail("Stopping after timer finished shouldn't throw exceptions");
        }
    }

    @Test
    public void getTimerModeTest() {
        Assertions.assertEquals(TimerMode.SESSION, timer.getTimerMode());
    }

    @Test
    public void switchToBreakTest() {
        timer.setSessionLength(0, 1);
        timer.startTimer();
        sleep(2 * SECOND);
        Assertions.assertEquals(TimerMode.BREAK, timer.getTimerMode());
    }

    /**
     * Check if the elapsed time is inside the boundary
     * @param sessionLength The total time of the current session
     * @param duration how much time should pass
     */
    private void assertInRange(long sessionLength, long duration) {
        long elapsedTime = sessionLength - timer.getRemainingTime();
        long upperBound = duration + _acceptableMargin;
        long lowerBound = duration - _acceptableMargin;
        Assertions.assertTrue(elapsedTime < upperBound, "Over upper bound");
        Assertions.assertTrue(elapsedTime > lowerBound, "Under lower bound");
    }

    /**
     * Shorthand for when tests need to sleep
     * @param milliseconds duration for the sleep
     */
    private void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Assertions.fail("Test was interrupted, please retry.");
        }
    }
}
