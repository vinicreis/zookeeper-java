package io.github.vinicreis.log;

public interface Log {
    /**
     * Set the debug flag to indicate if the debug should be enabled.
     * @param enable flag to indicate if {@code d} messages will be enabled.
     */
    void setDebug(boolean enable);

    /**
     * Indicates if debug is enabled on this logger instance.
     * @return true if debug is enabled, false otherwise.
     */
    boolean isDebug();

    /**
     * Insert a log with an error message.
     * @param msg message to inserted
     */
    void e(String msg);

    /**
     * Insert a log with a message and an exception.
     * @param msg message to be inserted
     * @param e thrown/caught exception to be logged.
     */
    void e(String msg, Throwable e);

    /**
     * Insert a debug message.
     * @param msg message to be inserted.
     */
    void d(String msg);

    /**
     * Insert a log warning message.
     * @param msg warning message to be logged.
     */
    void w(String msg);

    /**
     * Insert a verbose message.
     * @param msg verbose message to be inserted.
     */
    void v(String msg);
}
