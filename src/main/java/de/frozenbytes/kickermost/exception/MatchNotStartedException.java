package de.frozenbytes.kickermost.exception;

import de.frozenbytes.kickermost.dto.property.TickerUrl;

import java.io.IOException;

public final class MatchNotStartedException extends Exception {

    private final TickerUrl tickerUrl;

    public MatchNotStartedException(final TickerUrl tickerUrl) {
        super(String.format("The following match has not been started yet: '%s'!", tickerUrl));
        this.tickerUrl = tickerUrl;
    }

    public TickerUrl getTickerUrl() {
        return tickerUrl;
    }
}
