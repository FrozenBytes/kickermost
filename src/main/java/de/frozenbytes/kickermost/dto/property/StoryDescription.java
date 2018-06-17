package de.frozenbytes.kickermost.dto.property;

import de.frozenbytes.kickermost.dto.property.basic.StringProperty;

public final class StoryDescription extends StringProperty {

    private static final long serialVersionUID = 5542142574855829389L;

    public static StoryDescription create(String value){
        return value == null ? null : new StoryDescription(value);
    }

    private StoryDescription(String value) {
        super(value);
    }
}
