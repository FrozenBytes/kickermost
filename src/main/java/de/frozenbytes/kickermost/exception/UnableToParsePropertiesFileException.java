package de.frozenbytes.kickermost.exception;

import java.io.IOException;

public final class UnableToParsePropertiesFileException extends IOException {

    private static String buildMessage(final String filePath){
        return String.format("Unable to parse the properties file '%s'!", filePath);
    }

    public UnableToParsePropertiesFileException(final String filePath, final Exception cause) {
        super(buildMessage(filePath), cause);
    }

}
