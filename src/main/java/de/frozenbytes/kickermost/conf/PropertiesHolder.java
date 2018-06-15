package de.frozenbytes.kickermost.conf;

import com.google.common.base.Preconditions;
import de.frozenbytes.kickermost.PropertiesLoader;

import java.util.Properties;

public final class PropertiesHolder {

    private final String mattermostUsername;
    private final String mattermostChannelName;
    private final String mattermostIconUrl;
    private final String mattermostWebhookUrl;

    private final int pollingGlobalIntervalMin;
    private final int pollingGlobalIntervalMax;
    private final int pollingTickerIntervalMin;
    private final int pollingTickerIntervalMax;

    private final String pollingRssFeedUrl;


    public PropertiesHolder(final Properties properties) {
        this.mattermostUsername = getStringProperty(properties, PropertiesLoader.USERNAME);
        this.mattermostChannelName = getStringProperty(properties, PropertiesLoader.CHANNEL);
        this.mattermostIconUrl = getStringProperty(properties, PropertiesLoader.ICON_URL);
        this.mattermostWebhookUrl = getStringProperty(properties, PropertiesLoader.WEBHOOK_URL);
        this.pollingGlobalIntervalMin = getIntProperty(properties, PropertiesLoader.POLLING_GLOBAL_INTERVAL_MIN);
        this.pollingGlobalIntervalMax = getIntProperty(properties, PropertiesLoader.POLLING_GLOBAL_INTERVAL_MAX);
        this.pollingTickerIntervalMin = getIntProperty(properties, PropertiesLoader.POLLING_TICKER_INTERVAL_MIN);
        this.pollingTickerIntervalMax = getIntProperty(properties, PropertiesLoader.POLLING_TICKER_INTERVAL_MAX);
        this.pollingRssFeedUrl = getStringProperty(properties, PropertiesLoader.POLLING_RSS_FEED_URL);
    }

    public String getMattermostUsername() {
        return mattermostUsername;
    }

    public String getMattermostChannelName() {
        return mattermostChannelName;
    }

    public String getMattermostIconUrl() {
        return mattermostIconUrl;
    }

    public String getMattermostWebhookUrl() {
        return mattermostWebhookUrl;
    }

    public int getPollingGlobalIntervalMin() {
        return pollingGlobalIntervalMin;
    }

    public int getPollingGlobalIntervalMax() {
        return pollingGlobalIntervalMax;
    }

    public int getPollingTickerIntervalMin() {
        return pollingTickerIntervalMin;
    }

    public int getPollingTickerIntervalMax() {
        return pollingTickerIntervalMax;
    }

    public String getPollingRssFeedUrl() {
        return pollingRssFeedUrl;
    }

    private String getStringProperty(final Properties properties, final String key){
        validatePropertiesKey(properties, key);
        validatePropertiesStringValue(properties, key);
        return properties.getProperty(key);
    }

    private int getIntProperty(final Properties properties, final String key){
        validatePropertiesKey(properties, key);
        validatePropertiesIntegerValue(properties, key);
        return Integer.parseInt(properties.getProperty(key));
    }

    private void validatePropertiesKey(final Properties properties, final String key){
        Preconditions.checkState(properties.contains(key), String.format("Expected the properties file to contain the key '%s'!", key));
    }

    private void validatePropertiesStringValue(final Properties properties, final String key){
        final String value = properties.getProperty(key);
        Preconditions.checkState(!value.trim().isEmpty(), String.format("Expected the string value for the key '%s' to be filled!", key));
    }

    private void validatePropertiesIntegerValue(final Properties properties, final String key){
        final String value = properties.getProperty(key);
        Preconditions.checkState(!value.trim().isEmpty(), String.format("Expected the int value for the key '%s' to be filled!", key));
        Preconditions.checkState(value.matches("^\\d+$"), String.format("Expected the int value for the key '%s' to match the pattern '^\\d+$', but was '%s'!", key, value));
    }

}
