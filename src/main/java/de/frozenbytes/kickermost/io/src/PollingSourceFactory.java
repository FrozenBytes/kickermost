package de.frozenbytes.kickermost.io.src;

import de.frozenbytes.kickermost.dto.property.TickerUrl;
import de.frozenbytes.kickermost.exception.ReloadPollingSourceException;
import de.frozenbytes.kickermost.exception.TickerNotInSourceException;

public final class PollingSourceFactory {

    public static PollingSource create(TickerUrl url) throws ReloadPollingSourceException, TickerNotInSourceException {
        return new Kicker(url);
    }

}
