package de.frozenbytes.kickermost.io.src;

import de.frozenbytes.kickermost.conf.PropertiesHolder;
import de.frozenbytes.kickermost.dto.property.TickerUrl;
import de.frozenbytes.kickermost.dto.property.basic.Property;
import de.frozenbytes.kickermost.io.src.rss.RssParser;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public final class PollingSourceFactory {

    public static PollingSource create(final TickerUrl url, final PropertiesHolder propertiesHolder) {
        return new Kicker(url, propertiesHolder);
    }

    public static List<TickerUrl> parseRssFeed(final PropertiesHolder propertiesHolder) throws IOException {
        return new RssParser(propertiesHolder).parse().stream()
                .map(Property::getValue)
                .filter(v -> v.contains(propertiesHolder.getPollingFssFeedUrlContains()))
                .map(TickerUrl::create)
                .collect(Collectors.toList());
    }

}
