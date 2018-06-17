package de.frozenbytes.kickermost.dto.property;

import de.frozenbytes.kickermost.dto.property.basic.IntegerProperty;

public final class TeamScore extends IntegerProperty {

    private static final long serialVersionUID = 3434317513056586068L;

    public static TeamScore create(Integer value){
        return value == null ? null : new TeamScore(value);
    }

    private TeamScore(Integer value) {
        super(value);
    }
}
