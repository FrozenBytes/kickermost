package de.frozenbytes.kickermost.dto.property;

import de.frozenbytes.kickermost.dto.property.basic.StringProperty;

public final class RssLink extends StringProperty {

    private static final long serialVersionUID = -7940981938790922484L;

    public static RssLink create(final String value){
        return value == null ? null : new RssLink(value);
    }

    private RssLink(String value) {
        super(value);
    }

}
