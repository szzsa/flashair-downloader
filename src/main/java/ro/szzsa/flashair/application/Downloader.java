package ro.szzsa.flashair.application;

import org.apache.http.util.TextUtils;
import ro.szzsa.flashair.configuration.Configuration;
import ro.szzsa.flashair.connector.ConnectorException;
import ro.szzsa.flashair.connector.HttpConnector;
import ro.szzsa.flashair.logging.Logger;
import ro.szzsa.flashair.logging.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class Downloader {

    private Logger log = LoggerFactory.createLogger(getClass());

    private Configuration config = Configuration.getInstance();

    private HttpConnector connector = new HttpConnector();

    private Parser parser = new Parser();

    private boolean stopped = false;

    public void start() {
        log.info("Starting FlashAir Downloader");
        if (dirExists()) {
            while (!stopped) {
                try {
                    String listUrl = config.getFlashairUrlBase() + "/command.cgi?op=100&DIR=" + config.getFlashairPictureDirectory();
                    log.info("Retrieving the list of pictures");
                    for (Picture picture : parser.parseList(connector.doRequest(listUrl))) {
                        if (!fileExists(picture.getName())) {
                            String downloadUrl =
                                config.getFlashairUrlBase() + config.getFlashairPictureDirectory() + "/" + picture.getName();
                            connector.downloadFile(downloadUrl, picture.getName());
                        }
                    }
                } catch (ConnectorException e) {
                    log.error("Cannot connect to the server " + e.getMessage());
                } catch (Exception e) {
                    log.error("Unexpected exception", e);
                    stopped = true;
                } finally {
                    if (!stopped) {
                        try {
                            Thread.sleep(config.getDownloaderListCheckDelay());
                        } catch (InterruptedException e) {
                            log.error("Delay interrupted");
                        }
                    }
                }
            }
        } else {
            log.error("Destination directory doesn't exist");
        }
    }

    private boolean fileExists(String fileName) {
        List<String> folders = new ArrayList<>();
        folders.add(config.getDownloaderDestinationDirectory());
        String localFolders = config.getDownloaderLocalDirectories();
        if (!TextUtils.isEmpty(localFolders)) {
            if (localFolders.contains(";")) {
                Collections.addAll(folders, localFolders.split(";"));
            } else {
                folders.add(localFolders);
            }
        }
        for (String folder : folders) {
            File file = new File(folder + "/" + fileName);
            if (file.exists() && !file.isDirectory()) {
                return true;
            }
        }
        return false;
    }

    private boolean dirExists() {
        boolean dirExists = false;
        File destinationDir = new File(config.getDownloaderDestinationDirectory());
        if (!destinationDir.exists() || !destinationDir.isDirectory()) {
            try {
                log.info("Creating destination directory");
                dirExists = destinationDir.mkdir();
                dirExists = true;
            } catch (SecurityException e) {
                log.error("Cannot create destination directory", e);
            }
        } else {
            dirExists = true;
        }
        return dirExists;
    }
}
