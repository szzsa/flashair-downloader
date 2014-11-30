package ro.szzsa.flashair.logging;

/**
 *
 */
public interface Logger {

    void verbose(String message);

    void debug(String message);

    void info(String message);

    void warn(String message);

    void error(String message);

    void error(String message, Throwable e);

    void fatal(String message);
}
