package de.frozenbytes.kickermost.dto.property;

import de.frozenbytes.kickermost.dto.property.basic.StringProperty;

public final class TickerUrl extends StringProperty {

    public static TickerUrl create(String value){
        return value == null ? null : new TickerUrl(value);
    }

    private TickerUrl(String value) {
        super(value);
    }

}
