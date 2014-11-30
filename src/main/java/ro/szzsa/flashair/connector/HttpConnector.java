package ro.szzsa.flashair.connector;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import ro.szzsa.flashair.configuration.Configuration;
import ro.szzsa.flashair.logging.Logger;
import ro.szzsa.flashair.logging.LoggerFactory;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 *
 */
public class HttpConnector {

    private Logger log = LoggerFactory.createLogger(getClass());

    private Configuration config = Configuration.getInstance();

    public String doRequest(final String url) throws Exception {
        return handleConnection(new Connection() {
            @Override
            public String connect() throws Exception {
                return sendRequest(url);
            }
        });
    }

    public void downloadFile(final String url, final String fileName) throws Exception {
        handleConnection(new Connection() {
            @Override
            public String connect() throws Exception {
                download(url, fileName);
                return null;
            }
        });
    }

    private String sendRequest(String url) throws Exception {
        log.info("Sending request to " + url);
        final RequestConfig requestConfig = RequestConfig.custom()
                                                .setConnectTimeout(config.getDownloaderConnectionTimeout())
                                                .setSocketTimeout(config.getDownloaderReadTimeout())
                                                .build();
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build()) {
            HttpGet httpGet = new HttpGet(url);
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            try (InputStream inputStream = entity.getContent()) {
                return new String(IOUtils.toByteArray(inputStream));
            }
        } catch (Exception e) {
            throw new ConnectorException(e);
        }
    }

    public void download(String url, String fileName) throws Exception {
        log.info("Downloading file from " + url);
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(config.getDownloaderConnectionTimeout());
            connection.setReadTimeout(config.getDownloaderReadTimeout());
            connection.connect();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (InputStream input = connection.getInputStream();
                     OutputStream output = new FileOutputStream(config.getDownloaderDestinationDirectory() + "/" + fileName)) {
                    byte buffer[] = new byte[config.getDownloaderBufferSize()];
                    int count;
                    while ((count = input.read(buffer)) != -1) {
                        output.write(buffer, 0, count);
                    }
                }
                log.info(fileName + " downloaded");
            } else {
                log.error("Cannot download file: status " + connection.getResponseCode());
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private String handleConnection(Connection connection) throws Exception {
        while (connection.canRetry()) {
            try {
                return connection.connect();
            } catch (ConnectException | SocketTimeoutException e) {
                log.error(e.getMessage());
                connection.retry();
                Thread.sleep(config.getDownloaderConnectionRetryDelay());
                log.debug("Retrying...");
            }
        }
        log.error("Maximum retry count reached");
        throw new ConnectorException("Maximum retry count reached");
    }

    private abstract class Connection {

        private int retriesLeft = config.getDownloaderMaxConnectionRetryCount();

        public abstract String connect() throws Exception;

        public void retry() {
            retriesLeft--;
        }

        public boolean canRetry() {
            return retriesLeft >= 0;
        }
    }
}
