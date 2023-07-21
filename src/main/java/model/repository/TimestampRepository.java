package model.repository;

import model.repository.thread.TimestampIncrementThread;

/**
 * Repository used to track the timestamp inside the Controller server.
 */
public class TimestampRepository {
    private static final Long DEFAULT_STEP = 100L;
    private final TimestampIncrementThread thread = new TimestampIncrementThread(DEFAULT_STEP);

    /**
     * Get the current timestamp value registered.
     * @return a {@code Long} value with the current timestamp
     * @throws IllegalStateException in case the method is called while the repository thread is not running.
     */
    public Long getCurrent() throws IllegalStateException {
        return thread.getCurrent();
    }

    /**
     * Starts the timestamp increment by starting the {@code IncrementThread}.
     */
    public void start() {
        thread.start();
    }

    /**
     * Stops the timestamp increment by starting the {@code IncrementThread}.
     * Note that only by setting {@code running} as {@code false} finishes the
     * {@code IncrementThread} instance.
     */
    public void stop() {
        thread.interrupt();
    }

    /**
     * Stops the increment and sets the current value to zero.
     */
    public void reset() {
        thread.reset();
    }
}
