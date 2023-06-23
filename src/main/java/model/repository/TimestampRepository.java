package model.repository;

import log.ConsoleLog;
import log.Log;

public class TimestampRepository {
    private static final String TAG = "TimestampRepository";
    private static final Log log = new ConsoleLog(TAG);
    private static final Long DEFAULT_STEP = 100L;
    private final IncrementThread thread = new IncrementThread();
    private final Long step;
    private boolean running = false;
    private Long current = 0L;

    public TimestampRepository() {
        this.step = DEFAULT_STEP;
    }

    public TimestampRepository(Long step) {
        this.step = step;
    }

    public class IncrementThread extends Thread {
        @Override
        public void run() {
            try {
                while (running) {
                    if (current == Long.MAX_VALUE)
                        current = 0L;
                    else
                        current++;

                    sleep(step);
                }
            } catch (InterruptedException e) {
                log.e("Timestamp clock interrupted!", e);

                reset();
            }
        }
    }

    public boolean isRunning() {
        return running;
    }

    public Long getCurrent() throws IllegalStateException {
        if(running) return current;

        throw new IllegalStateException("Timestamp clock not running");
    }

    public void start() {
        running = true;
        thread.start();
    }

    public void stop() {
        running = false;
    }

    public void reset() {
        running = false;
        current = 0L;
    }
}
