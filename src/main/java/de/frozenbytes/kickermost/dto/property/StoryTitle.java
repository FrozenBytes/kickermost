package de.frozenbytes.kickermost.dto.property;

import de.frozenbytes.kickermost.dto.property.basic.StringProperty;

public final class StoryTitle extends StringProperty {

    private static final long serialVersionUID = -1703679184119413877L;

    public static StoryTitle create(String value){
        return value == null ? null : new StoryTitle(value);
    }

    private StoryTitle(String value) {
        super(value);
    }
}
