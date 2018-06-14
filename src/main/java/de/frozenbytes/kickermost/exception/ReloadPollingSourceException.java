package de.frozenbytes.kickermost.exception;

import de.frozenbytes.kickermost.dto.property.TickerUrl;

import java.io.IOException;

public final class ReloadPollingSourceException extends IOException {

    private final TickerUrl tickerUrl;

    public ReloadPollingSourceException(final TickerUrl tickerUrl, final IOException cause) {
        super(String.format("Unable to reload polling source: '%s'!", tickerUrl), cause);
        this.tickerUrl = tickerUrl;
    }

    public TickerUrl getTickerUrl() {
        return tickerUrl;
    }
}
