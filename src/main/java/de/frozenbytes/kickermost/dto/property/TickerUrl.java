package de.frozenbytes.kickermost.dto.property;

import de.frozenbytes.kickermost.dto.property.basic.StringProperty;

public final class TickerUrl extends StringProperty {

    private static final long serialVersionUID = 2533905381781177165L;

    public static TickerUrl create(String value){
        return value == null ? null : new TickerUrl(value);
    }

    private TickerUrl(String value) {
        super(value);
    }

}
