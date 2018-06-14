package de.frozenbytes.kickermost.dto.property;

import de.frozenbytes.kickermost.dto.property.basic.StringProperty;

public final class RssLink extends StringProperty {

    public static RssLink create(final String value){
        return value == null ? null : new RssLink(value);
    }

    private RssLink(String value) {
        super(value);
    }

}
