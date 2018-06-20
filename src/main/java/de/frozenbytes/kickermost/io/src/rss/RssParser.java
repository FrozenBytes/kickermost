package de.frozenbytes.kickermost.io.src.rss;

import de.frozenbytes.kickermost.conf.PropertiesHolder;
import de.frozenbytes.kickermost.dto.property.RssLink;
import de.frozenbytes.kickermost.exception.UnableToParseRssFeedException;
import de.frozenbytes.kickermost.util.jsoup.JsoupUtility;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public final class RssParser {

    private static final String CSS_LINKS = "channel item link";

    private final Document document;


    public RssParser(final PropertiesHolder propertiesHolder) throws UnableToParseRssFeedException {
        final String rssFeedUrl = propertiesHolder.getPollingRssFeedUrl();
        try {
            this.document = JsoupUtility.requestDocument(rssFeedUrl, propertiesHolder);
        } catch (IOException e) {
            throw new UnableToParseRssFeedException(rssFeedUrl, e);
        }
    }

    public List<RssLink> parse(){
        return document.select(CSS_LINKS).stream()
                .map(Element::html)
                .map(RssLink::create)
                .collect(Collectors.toList());
    }

}
