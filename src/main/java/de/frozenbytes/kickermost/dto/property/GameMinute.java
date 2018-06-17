package de.frozenbytes.kickermost.dto.property;

import de.frozenbytes.kickermost.dto.property.basic.StringProperty;

public final class GameMinute extends StringProperty {

    private static final long serialVersionUID = 5490087522001118059L;

    public static GameMinute create(String value){
        return value == null ? null : new GameMinute(value);
    }

    private GameMinute(String value) {
        super(value);
    }
}
