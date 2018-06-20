package de.frozenbytes.kickermost.exception;

import java.io.IOException;

public final class UnableToParseRssFeedException extends IOException {

    private static final long serialVersionUID = 3501171131838799044L;

    public UnableToParseRssFeedException(final String rssFeedUrl, final IOException cause) {
        super(String.format("Unable to parse RSS feed: '%s'!", rssFeedUrl), cause);
    }

}
