package de.frozenbytes.kickermost.io.src;

import de.frozenbytes.kickermost.dto.property.TickerUrl;
import de.frozenbytes.kickermost.dto.property.basic.Property;
import de.frozenbytes.kickermost.exception.ReloadPollingSourceException;
import de.frozenbytes.kickermost.exception.TickerNotInSourceException;
import de.frozenbytes.kickermost.io.src.rss.RssParser;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public final class PollingSourceFactory {

    public static PollingSource create(TickerUrl url) throws ReloadPollingSourceException, TickerNotInSourceException {
        return new Kicker(url);
    }

    public static List<TickerUrl> parseRssFeed(final String rssUrl) throws IOException {
        return new RssParser(rssUrl).parse().stream()
                .map(Property::getValue)
                .map(PollingSourceFactory::applyKickerRssLinkHacks) //special for kicker.de an ugly like uli h. :(
                .map(TickerUrl::create)
                .collect(Collectors.toList());
    }

    private static String applyKickerRssLinkHacks(String link){
        return link.replace("spielbericht", "spielverlauf")
                .replace("spielvorschau", "spielverlauf")
                .replace("spielinfo", "spielverlauf");
    }

}
