package ro.szzsa.flashair.logging;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Provides methods for logging.
 */
public class ConsoleLogger implements Logger {

    private String tag;

    public ConsoleLogger(Class loggingClass) {
        tag = loggingClass.getSimpleName();
    }

    public void verbose(String message) {
        print("VERBOSE", message);
    }

    public void debug(String message) {
        print("DEBUG", message);
    }

    public void info(String message) {
        print("INFO", message);
    }

    public void warn(String message) {
        print("WARNING", message);
    }

    public void error(String message) {
        print("ERROR", message);
    }

    public void error(String message, Throwable e) {
        print("ERROR", message);
        e.printStackTrace();
    }

    public void fatal(String message) {
        print("FATAL", message);
    }

    private void print(String level, String message) {
        String date = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS").format(new Date());
        System.out.println(date + " " + level + " [" + tag + "] " + message);
    }
}
