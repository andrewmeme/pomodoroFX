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
    /*
     * Timer tracker, a thread that keeps tracks of passage of time
     */
    private ScheduledFuture<?> timerThread;
    private boolean isInSession;
    private long currentTime;
    private long endTime;

    /*
     * Pause tracker, values only has meaning if isPause is true
     */
    private boolean isPause;
    private long pauseStart;

    public PomodoroTimer() {
        scheduler = Executors.newScheduledThreadPool(1);
        sessionLength = 25 * MINUTE;
        breakLength = 5 * MINUTE;

        isInSession = false;
        isPause = false;
    }

    public void setSessionLength(long minutes, long seconds) {
        sessionLength = minutes * MINUTE + seconds * SECOND;
    }

    public long getSessionLength() {
        return sessionLength;
    }

    public void setBreakLength(long minutes, long seconds) {
        breakLength = minutes * MINUTE + seconds * SECOND;
    }

    public long getBreakLength() {
        return breakLength;
    }

    public void startTimer() {
        if (isInSession) {
            return;
        }

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
                if (currentTime > endTime) {
                    timerThread.cancel(true);
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
        if (!isInSession) {
            return;
        }

        isInSession = false;
        timerThread.cancel(true);
    }

    public void resetTimer() {
        stopTimer();
        setSessionLength(25, 0);
        setBreakLength(5, 0);
    }

    public long getRemainingTime() {
        if (!isInSession) {
            return sessionLength;
        }

        return (endTime - currentTime);
    }
}
