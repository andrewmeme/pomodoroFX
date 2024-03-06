package ancientmeme.pomodoro;

import ancientmeme.pomodoro.util.TimerMode;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Handles the logic for the clock, supports pausing,
 * stopping, and resetting timer settings.
 */
public class PomodoroTimer {
    private final long SECOND = 1000;
    private final long MINUTE = 60 * SECOND;
    //The scheduler runs a thread that updates for the timer
    private final ScheduledExecutorService scheduler;
    // Timer tracker, a thread that keeps tracks of passage of time
    private ScheduledFuture<?> timerThread;
    // Length of a session in milliseconds
    private long sessionLength;
    // Length of a break in milliseconds
    private long breakLength;
    // Timer cursor to track current time using System Clock
    private long currentTime;
    // Timer cursor to indicate when the timer should end
    private long endTime;
    // Pause cursor, values only has meaning if isPause is true
    private long pauseStart;
    private boolean isInSession;
    private boolean isTimerRunning;
    private boolean isPause;

    /**
     * Constructs a pomodoro timer for the application to use.
     * the session and break length is set to the default pomodoro
     * technique recommendation: 25 minutes / 5 minutes
     */
    public PomodoroTimer() {
        scheduler = Executors.newScheduledThreadPool(1);
        sessionLength = 25 * MINUTE;
        breakLength = 5 * MINUTE;

        isInSession = true;
        isTimerRunning = false;
        isPause = false;
    }

    /**
     * Gets the current session length
     * @return The current length for a session in milliseconds
     */
    public long getSessionLength() {
        return sessionLength;
    }

    /**
     * Set the length of a session
     * @param minutes how many minutes in a session
     * @param seconds how many seconds in a session
     */
    public void setSessionLength(long minutes, long seconds) {
        sessionLength = minutes * MINUTE + seconds * SECOND;
    }

    /**
     * Gets the current break length
     * @return The current length for a break in milliseconds
     */
    public long getBreakLength() {
        return breakLength;
    }

    /**
     * Set the length for a break
     * @param minutes how many minutes in a session
     * @param seconds how many seconds in a session
     */
    public void setBreakLength(long minutes, long seconds) {
        breakLength = minutes * MINUTE + seconds * SECOND;
    }

    /**
     * Gets the remaining time left for the current
     * session or break in milliseconds
     * @return remaining milliseconds of current session or break
     */
    public long getRemainingTime() {
        if (!isTimerRunning) {
            return sessionLength;
        }

        return (endTime - currentTime);
    }

    /**
     * Gets the mode the timer is currently on
     * @return TimerMode.Session or TimerMode.Break
     */
    public TimerMode getTimerMode() {
        return (isInSession) ? TimerMode.SESSION : TimerMode.BREAK;
    }

    /**
     * Gets information on is the timer running
     * @return is the timer in a session
     */
    public boolean isTimerRunning() {
        return isTimerRunning;
    }

    /**
     * Gets information on is the current timer paused
     * @return is the timer paused
     */
    public boolean isPause() {
        return isPause;
    }

    /**
     * Starting a dedicated Runnable that updates the current time
     * cursor every 50ms. The Runnable responses to timer pausing and
     * switches mode when a session or a break ends.
     */
    public void startTimer() {
        if (isTimerRunning) {
            return;
        }

        isTimerRunning = true;
        isInSession = true;
        currentTime = System.currentTimeMillis();
        endTime = System.currentTimeMillis() + sessionLength;

        Runnable timer = new Runnable() {
            @Override
            public void run() {
                if (isPause) {
                    return;
                }

                currentTime = System.currentTimeMillis();
                if (currentTime >= endTime) {
                    switchMode();
                }
            }
        };
        timerThread = scheduler.scheduleAtFixedRate(timer, 0, 50, MILLISECONDS);
    }

    /**
     * Pausing the timer if it is currently active
     */
    public void pauseTimer() {
        if (isPause) {
            return;
        }

        isPause = true;
        pauseStart = System.currentTimeMillis();
    }

    /**
     * Resume the timer if it is currently active
     */
    public void resumeTimer() {
        if (!isPause) {
            return;
        }

        // Order matters, update the time cursors before unpause
        long pauseDuration = System.currentTimeMillis() - pauseStart;
        endTime += pauseDuration;
        currentTime = System.currentTimeMillis();
        isPause = false;
    }

    /**
     * Stops the timer completely and reset its mode
     */
    public void stopTimer() {
        if (!isTimerRunning) {
            return;
        }

        isTimerRunning = false;
        isPause = false;
        timerThread.cancel(true);
    }

    /**
     * Performs the same task as stopTimer, additionally resets
     * all settings back to default
     */
    public void resetTimer() {
        stopTimer();
        setSessionLength(25, 0);
        setBreakLength(5, 0);
    }

    /**
     * Shutdowns the scheduler for updating the clock, should only
     * be called when application is preparing to exit
     */
    public void shutdownTimer() {
        scheduler.shutdownNow();
    }

    /* Should only be used by timer thread */
    private void switchMode() {
        isInSession = !isInSession;
        if (isInSession) {
            endTime = System.currentTimeMillis() + sessionLength;
        } else {
            endTime = System.currentTimeMillis() + breakLength;
        }
    }
}
