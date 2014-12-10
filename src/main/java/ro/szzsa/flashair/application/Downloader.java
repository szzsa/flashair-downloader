package ro.szzsa.flashair.application;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.util.TextUtils;
import org.apache.log4j.Logger;

import ro.szzsa.flashair.configuration.Configuration;
import ro.szzsa.flashair.connector.ConnectorException;
import ro.szzsa.flashair.connector.HttpConnector;

/**
 *
 */
public class Downloader {

    private static final String LIST_URL_PATH = "/command.cgi?op=100&DIR=";

    static Logger log;

    private Configuration config;

    private HttpConnector connector;

    private Parser parser;

    private boolean stopped;

    public Downloader() {
        log = Logger.getLogger(getClass().getName());
        config = Configuration.getInstance();
        connector = new HttpConnector();
        connector.setBufferSize(config.getDownloaderBufferSize());
        connector.setConnectionTimeout(config.getDownloaderConnectionTimeout());
        connector.setReadTimeout(config.getDownloaderReadTimeout());
        connector.setConnectionRetryDelay(config.getDownloaderConnectionRetryDelay());
        connector.setMaxConnectionRetryCount(config.getDownloaderMaxConnectionRetryCount());
        parser = new Parser();
    }

    public void start() {
        log.info("Starting FlashAir Downloader");
        if (connector.dirExists(config.getDownloaderDestinationDirectory())) {
            while (!stopped) {
                try {
                    String listUrl = config.getFlashairUrlBase() + LIST_URL_PATH + config.getFlashairPictureDirectory();
                    log.info("Retrieving the list of pictures");
                    for (Picture picture : parser.parseList(connector.doRequest(listUrl))) {
                        if (!fileExists(picture.getName())) {
                            String downloadUrl =
                                config.getFlashairUrlBase() + config.getFlashairPictureDirectory() + "/" + picture.getName();
                            connector.downloadFile(downloadUrl, picture.getName(), config.getDownloaderDestinationDirectory());
                        }
                    }
                    log.info("The list of pictures was parsed");
                } catch (ConnectorException e) {
                    log.error("Cannot connect to the server " + e.getMessage());
                } catch (Exception e) {
                    log.error("Unexpected exception", e);
                    stopped = true;
                } finally {
                    delayListCheck();
                }
            }
        } else {
            log.error("Destination directory doesn't exist");
        }
    }

    private void delayListCheck() {
        if (!stopped) {
            try {
                int delay = config.getDownloaderListCheckDelay();
                log.info("Waiting " + String.valueOf(delay) + " milliseconds for the next request");
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                log.error("Delay interrupted");
            }
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
}
