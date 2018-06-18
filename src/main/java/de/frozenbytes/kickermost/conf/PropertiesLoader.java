package de.frozenbytes.kickermost.conf;

import de.frozenbytes.kickermost.exception.UnableToParsePropertiesFileException;
import de.frozenbytes.kickermost.exception.UnableToSavePropertiesFileException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class PropertiesLoader {

    public static final String USERNAME = "username";
    public static final String CHANNEL = "channel";
    public static final String ICON_URL = "icon_url";
    public static final String WEBHOOK_URL = "webhook_url";
    public static final String POLLING_GLOBAL_INTERVAL_MIN = "polling_global_interval_min";
    public static final String POLLING_GLOBAL_INTERVAL_MAX = "polling_global_interval_max";
    public static final String POLLING_TICKER_INTERVAL_MIN = "polling_ticker_interval_min";
    public static final String POLLING_TICKER_INTERVAL_MAX = "polling_ticker_interval_max";
    public static final String POLLING_RSS_FEED_URL = "polling_rss_feed_url";
    public static final String POLLING_RSS_FEED_URL_CONTAINS = "polling_rss_feed_url_contains";


    private PropertiesLoader() {
    }

    public static PropertiesHolder createPropertiesHolder(final String configFilePath) throws UnableToParsePropertiesFileException {
        try{
            final Properties properties = loadProperties(configFilePath);
            return new PropertiesHolder(properties);
        }catch(RuntimeException e){
            throw new UnableToParsePropertiesFileException(configFilePath, e);
        }
    }

    public static void saveProperties(final String configFilePath, final String username, final String channel, final String iconUrl, final String webhookUrl) throws UnableToParsePropertiesFileException, UnableToSavePropertiesFileException {
        final Properties prop = loadProperties(configFilePath);
        prop.setProperty(USERNAME, username);
        prop.setProperty(CHANNEL, channel);
        prop.setProperty(ICON_URL, iconUrl);
        prop.setProperty(WEBHOOK_URL, webhookUrl);
        try(OutputStream outputStream = new FileOutputStream(new File(configFilePath))) {
            prop.store(outputStream, null);
        } catch (RuntimeException | IOException e){
            throw new UnableToSavePropertiesFileException(configFilePath, e);
        }
    }

    private static Properties loadProperties(final String filePath) throws UnableToParsePropertiesFileException {
        final Properties prop = new Properties();
        try(InputStream inputStream = new FileInputStream(new File(filePath))) {
            prop.load(inputStream);
            return prop;
        } catch (RuntimeException | IOException e) {
            throw new UnableToParsePropertiesFileException(filePath, e);
        }
    }

}
