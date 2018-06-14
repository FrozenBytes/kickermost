package de.frozenbytes.kickermost.dto;

import com.google.common.base.Preconditions;
import de.frozenbytes.kickermost.dto.property.TickerUrl;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ticker ticker = (Ticker) o;
        return Objects.equals(tickerUrl, ticker.tickerUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tickerUrl);
    }
}
