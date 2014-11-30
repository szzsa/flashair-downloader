package ro.szzsa.flashair.logging;

/**
 *
 */
public final class LoggerFactory {

    private LoggerFactory() {
        throw new UnsupportedOperationException("Please do not instantiate me");
    }

    public static Logger createLogger(Class tClass) {
        return new ConsoleLogger(tClass);
    }
}
