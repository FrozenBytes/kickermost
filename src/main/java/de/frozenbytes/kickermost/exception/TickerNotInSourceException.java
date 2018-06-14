package de.frozenbytes.kickermost.exception;

import de.frozenbytes.kickermost.dto.property.TickerUrl;

public final class TickerNotInSourceException extends Exception {

    private final TickerUrl tickerUrl;

    public TickerNotInSourceException(final TickerUrl tickerUrl) {
        super(String.format("No ticker could be found in document: '%s'!", tickerUrl));
        this.tickerUrl = tickerUrl;
    }

    public TickerUrl getTickerUrl() {
        return tickerUrl;
    }

}
