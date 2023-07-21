package model.repository.thread;

import log.ConsoleLog;
import log.Log;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Thread started to keep incrementing the timestamp on background based on the {@code step} time.
 * Note that the {@code step} parameter denotes the time between each timestamp increment.
 */
public class TimestampIncrementThread extends Thread {
    private static final String TAG = "TimestampIncrementThread";
    private static final Log log = new ConsoleLog(TAG);
    private final Long step;
    private boolean running = false;
    private final AtomicLong current = new AtomicLong(0L);

    public TimestampIncrementThread(Long step) {
        this.step = step;
    }

    @Override
    public void run() {
        try {
            running = true;

            while (running) {
                if (current.get() == Long.MAX_VALUE)
                    current.set(0L);
                else
                    current.incrementAndGet();

                sleep(step);
            }
        } catch (InterruptedException e) {
            log.e("Timestamp clock interrupted!", e);

            reset();
        }
    }

    @Override
    public void interrupt() {
        running = false;

        super.interrupt();
    }

    /**
     * Get the current timestamp value registered.
     * @return a {@code Long} value with the current timestamp
     * @throws IllegalStateException in case the method is called while the repository thread is not running.
     */
    public Long getCurrent() throws IllegalStateException {
        if(running) return current.get();

        throw new IllegalStateException("Timestamp clock not running");
    }

    /**
     * Stops the increment and sets the current value to zero.
     */
    public void reset() {
        running = false;
        current.set(0L);
    }
}
