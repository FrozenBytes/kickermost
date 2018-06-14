package de.frozenbytes.kickermost.dto.property;

import de.frozenbytes.kickermost.dto.property.basic.IntegerProperty;

public final class TeamScore extends IntegerProperty {

    public static TeamScore create(Integer value){
        return value == null ? null : new TeamScore(value);
    }

    private TeamScore(Integer value) {
        super(value);
    }
}
