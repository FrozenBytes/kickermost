package de.frozenbytes.kickermost.exception;

import java.io.IOException;

public final class UnableToSavePropertiesFileException extends IOException {

    private static String buildMessage(final String filePath){
        return String.format("Unable to save the properties file '%s'!", filePath);
    }

    public UnableToSavePropertiesFileException(final String filePath, final Exception cause) {
        super(buildMessage(filePath), cause);
    }

}
