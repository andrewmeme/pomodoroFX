package ancientmeme.pomodoro;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.*;

/**
 * Handles the logic for the clock, supports pausing,
 * stopping, and settings reset
 */
public class PomodoroTimer {
    private final long SECOND = 1000;
    private final long MINUTE = 60 * SECOND;
    /*
    * The scheduler runs a thread that only does periodic updates
    * for the timer
    */
    private final ScheduledExecutorService scheduler;
    private long sessionLength;
    private long breakLength;
    private boolean isInSession;
    /* Timer tracker, a thread that keeps tracks of passage of time */
    private ScheduledFuture<?> timerThread;
    private long currentTime;
    private long endTime;
    private boolean isTimerRunning;
    /* Pause tracker, values only has meaning if isPause is true */
    private long pauseStart;
    private boolean isPause;

    public PomodoroTimer() {
        scheduler = Executors.newScheduledThreadPool(1);
        sessionLength = 25 * MINUTE;
        breakLength = 5 * MINUTE;

        isInSession = true;
        isTimerRunning = false;
        isPause = false;
    }

    public long getSessionLength() {
        return sessionLength;
    }

    public void setSessionLength(long minutes, long seconds) {
        sessionLength = minutes * MINUTE + seconds * SECOND;
    }

    public long getBreakLength() {
        return breakLength;
    }

    public void setBreakLength(long minutes, long seconds) {
        breakLength = minutes * MINUTE + seconds * SECOND;
    }

    public long getRemainingTime() {
        if (!isTimerRunning) {
            return sessionLength;
        }

        return (endTime - currentTime);
    }

    public String getTimerMode() {
        return (isInSession) ? "Session" : "Break";
    }

    public void startTimer() {
        if (isTimerRunning) {
            return;
        }

        isTimerRunning = true;
        isInSession = true;
        currentTime = System.currentTimeMillis();
        endTime = System.currentTimeMillis() + sessionLength;

        // Creates a dedicated thread to keep track of time
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

    public void pauseTimer() {
        if (isPause) {
            return;
        }

        isPause = true;
        pauseStart = System.currentTimeMillis();
    }

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

    public void stopTimer() {
        if (!isTimerRunning) {
            return;
        }

        isTimerRunning = false;
        isPause = false;
        timerThread.cancel(true);
    }

    public void resetTimer() {
        stopTimer();
        setSessionLength(25, 0);
        setBreakLength(5, 0);
    }

    /**
     * switching the timer mode between Session and Break
     */
    private void switchMode() {
        isInSession = !isInSession;
        if (isInSession) {
            endTime = System.currentTimeMillis() + sessionLength;
        } else {
            endTime = System.currentTimeMillis() + breakLength;
        }
    }
}
