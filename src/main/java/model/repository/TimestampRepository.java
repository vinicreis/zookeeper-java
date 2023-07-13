package model.repository;

import log.ConsoleLog;
import log.Log;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Repository used to track the timestamp inside the Controller server.
 */
public class TimestampRepository {
    private static final String TAG = "TimestampRepository";
    private static final Log log = new ConsoleLog(TAG);
    private static final Long DEFAULT_STEP = 100L;
    private final IncrementThread thread = new IncrementThread();
    private final Long step;
    private boolean running = false;
    private final AtomicLong current = new AtomicLong(0L);

    public TimestampRepository() {
        this.step = DEFAULT_STEP;
    }

    /**
     * Thread started to keep incrementing the timestamp on background based on the {@code step} time.
     * Note that the {@code step} parameter denotes the time between each timestamp increment.
     */
    public class IncrementThread extends Thread {
        @Override
        public void run() {
            try {
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
     * Starts the timestamp increment by starting the {@code IncrementThread}.
     */
    public void start() {
        running = true;
        thread.start();
    }

    /**
     * Stops the timestamp increment by starting the {@code IncrementThread}.
     * Note that only by setting {@code running} as {@code false} finishes the
     * {@code IncrementThread} instance.
     */
    public void stop() {
        running = false;
    }

    /**
     * Stops the increment and sets the current value to zero.
     */
    public void reset() {
        running = false;
        current.set(0L);
    }
}
