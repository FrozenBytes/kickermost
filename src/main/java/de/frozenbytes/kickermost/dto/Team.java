package de.frozenbytes.kickermost.dto;

import de.frozenbytes.kickermost.dto.property.TeamName;
import de.frozenbytes.kickermost.dto.property.TeamScore;

public final class Team {

    private final TeamName name;
    private final TeamScore score;

    public Team(final TeamName name, final TeamScore score) {
        this.name = name;
        this.score = score;
    }

    public TeamName getName() {
        return name;
    }

    public TeamScore getScore() {
        return score;
    }
}
