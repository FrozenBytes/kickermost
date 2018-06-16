package de.frozenbytes.kickermost.io.src;

import de.frozenbytes.kickermost.dto.property.TickerUrl;
import de.frozenbytes.kickermost.dto.property.basic.Property;
import de.frozenbytes.kickermost.io.src.rss.RssParser;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public final class PollingSourceFactory {

    public static PollingSource create(TickerUrl url) {
        return new Kicker(url);
    }

    public static List<TickerUrl> parseRssFeed(final String rssUrl, final String rssFeedUrlContains) throws IOException {
        return new RssParser(rssUrl).parse().stream()
                .map(Property::getValue)
                .filter(v -> v.contains(rssFeedUrlContains))
                .map(TickerUrl::create)
                .collect(Collectors.toList());
    }

}
