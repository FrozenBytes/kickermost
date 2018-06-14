package de.frozenbytes.kickermost;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

        try(InputStream inputStream = PropertiesLoader.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
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

    public static void saveProperties(final String username, final String channel, final String iconUrl, final String webhookUrl){
        Properties prop = loadProperties();
        prop.setProperty(USERNAME, username);
        prop.setProperty(CHANNEL, channel);
        prop.setProperty(ICON_URL, iconUrl);
        prop.setProperty(WEBHOOK_URL, webhookUrl);
        try(OutputStream outputStream = new FileOutputStream(PropertiesLoader.class.getClassLoader().getResource(PROPERTIES_FILE).getFile())) {
            prop.store(outputStream, null);
        } catch (IOException ex){
            ex.printStackTrace();
        }
    }
}
