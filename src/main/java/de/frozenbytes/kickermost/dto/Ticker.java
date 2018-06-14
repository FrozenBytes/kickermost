package de.frozenbytes.kickermost.dto;

import com.google.common.base.Preconditions;
import de.frozenbytes.kickermost.dto.property.TickerUrl;

public final class Ticker {

    private final TickerUrl tickerUrl;
    private final Match match;

    public Ticker(final TickerUrl tickerUrl, final Match match) {
        Preconditions.checkNotNull(tickerUrl, "tickerUrl should not be null!");
        this.tickerUrl = tickerUrl;
        Preconditions.checkNotNull(match, "match should not be null!");
        this.match = match;
    }

    public TickerUrl getTickerUrl() {
        return tickerUrl;
    }

    public Match getMatch() {
        return match;
    }
}
