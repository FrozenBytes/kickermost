package de.frozenbytes.kickermost.dto.property;

import de.frozenbytes.kickermost.dto.property.basic.StringProperty;

public final class StoryTitle extends StringProperty {

    public static StoryTitle create(String value){
        return value == null ? null : new StoryTitle(value);
    }

    private StoryTitle(String value) {
        super(value);
    }
}
