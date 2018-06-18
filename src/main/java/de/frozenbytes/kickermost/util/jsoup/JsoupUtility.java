package de.frozenbytes.kickermost.util.jsoup;

import com.google.common.base.Preconditions;
import de.frozenbytes.kickermost.conf.PropertiesHolder;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public final class JsoupUtility {

    private JsoupUtility(){
    }

    public static Document requestDocument(final String url, final PropertiesHolder propertiesHolder) throws IOException {
        Preconditions.checkNotNull(url, "url should not be null!");
        Preconditions.checkArgument(!url.trim().isEmpty(), "url should not be empty!");
        Preconditions.checkNotNull(propertiesHolder, "propertiesHolder should not be null!");

        Connection connection = Jsoup.connect(url);
        if(propertiesHolder.isPollingProxyActive()){
            connection = connection.proxy(propertiesHolder.getPollingProxyIP(), propertiesHolder.getPollingProxyPort());
        }
        return connection.get();
    }

}
