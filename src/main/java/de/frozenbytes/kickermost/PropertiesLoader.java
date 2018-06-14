package de.frozenbytes.kickermost;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {
    private static final String PROPERTIES_FILE = "config.properties";

    public static final String USERNAME = "username";
    public static final String CHANNEL = "channel";
    public static final String ICON_URL = "icon_url";
    public static final String WEBHOOK_URL = "webhook_url";

    private PropertiesLoader() {
    }

    public static Properties loadProperties(){
        Properties prop = new Properties();

        try(InputStream inputStream = new FileInputStream(PROPERTIES_FILE)) {
            // load a properties file
            prop.load(inputStream);
            return prop;

        } catch (IOException ex) {
            // error during loading of properties, use default
            return loadDefaultProperties();
        }
    }

    public static Properties loadDefaultProperties(){
        Properties prop = new Properties();
        prop.put(USERNAME, "WM-Ticker");
        prop.put(CHANNEL, "town-square");
        prop.put(ICON_URL, "http://mediadb.kicker.de/2018/fussball/ligen/l/101_20151028668.png");
        prop.put(WEBHOOK_URL, "");

        return prop;
    }
}
