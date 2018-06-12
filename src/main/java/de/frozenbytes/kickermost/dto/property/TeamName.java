package de.frozenbytes.kickermost.dto.property;

import de.frozenbytes.kickermost.dto.property.basic.StringProperty;

public final class TeamName extends StringProperty {

    public static TeamName create(String value){
        return value == null ? null : new TeamName(value);
    }

    private TeamName(String value) {
        super(value);
    }

}
