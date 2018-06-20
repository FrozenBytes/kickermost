package de.frozenbytes.kickermost.exception;

import java.io.IOException;

public final class UnableToPostToMattermostException extends IOException {

    private static final long serialVersionUID = -65704647358743190L;

    public UnableToPostToMattermostException(final String url, final Throwable cause) {
        super(String.format("Unable to POST to mattermost url: '%s'!", url), cause);
    }

}
