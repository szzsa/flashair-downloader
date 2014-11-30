package ro.szzsa.flashair.configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Provides configuration values.
 */
public final class Configuration {

    private static final String CONFIG_FILE_PATH = "config.properties";

    private static final String FLASHAIR_URL_BASE = "flashair.url.base";

    private static final String FLASHAIR_PICTURE_DIRECTORY = "flashair.picture.directory";

    private static final String DOWNLOADER_LIST_CHECK_DELAY = "downloader.list.check.delay";

    private static final String DOWNLOADER_CONNECTION_TIMEOUT = "downloader.connection.timeout";

    private static final String DOWNLOADER_READ_TIMEOUT = "downloader.read.timeout";

    private static final String DOWNLOADER_MAX_CONNECTION_RETRY_COUNT = "downloader.max.connection.retry.count";

    private static final String DOWNLOADER_CONNECTION_RETRY_DELAY = "downloader.connection.retry.delay";

    private static final String DOWNLOADER_BUFFER_SIZE = "downloader.buffer.size";

    private static final String DOWNLOADER_DESTINATION_DIRECTORY = "downloader.destination.directory";

    private static final String DOWNLOADER_LOCAL_DIRECTORIES = "downloader.local.directories";

    private static Configuration instance = new Configuration();

    private Properties properties = new Properties();

    private Configuration() {
        try {
            loadProperties();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Configuration getInstance() {
        return instance;
    }

    private void loadProperties() throws IOException {
        properties.load(new FileInputStream(CONFIG_FILE_PATH));
    }

    public String getFlashairUrlBase() {
        return properties.getProperty(FLASHAIR_URL_BASE);
    }

    public String getFlashairPictureDirectory() {
        return properties.getProperty(FLASHAIR_PICTURE_DIRECTORY);
    }

    public int getDownloaderListCheckDelay() {
        return Integer.parseInt(properties.getProperty(DOWNLOADER_LIST_CHECK_DELAY));
    }

    public int getDownloaderConnectionTimeout() {
        return Integer.parseInt(properties.getProperty(DOWNLOADER_CONNECTION_TIMEOUT));
    }

    public int getDownloaderReadTimeout() {
        return Integer.parseInt(properties.getProperty(DOWNLOADER_READ_TIMEOUT));
    }

    public int getDownloaderMaxConnectionRetryCount() {
        return Integer.parseInt(properties.getProperty(DOWNLOADER_MAX_CONNECTION_RETRY_COUNT));
    }

    public int getDownloaderConnectionRetryDelay() {
        return Integer.parseInt(properties.getProperty(DOWNLOADER_CONNECTION_RETRY_DELAY));
    }

    public int getDownloaderBufferSize() {
        return Integer.parseInt(properties.getProperty(DOWNLOADER_BUFFER_SIZE));
    }

    public String getDownloaderDestinationDirectory() {
        return properties.getProperty(DOWNLOADER_DESTINATION_DIRECTORY);
    }

    public String getDownloaderLocalDirectories() {
        return properties.getProperty(DOWNLOADER_LOCAL_DIRECTORIES);
    }
}
