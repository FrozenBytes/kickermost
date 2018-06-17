package de.frozenbytes.kickermost.dto;

import com.google.common.base.Preconditions;
import de.frozenbytes.kickermost.dto.property.TeamName;
import de.frozenbytes.kickermost.dto.property.TeamScore;

public final class Team {

    private final TeamName name;
    private TeamScore score;

    public Team(final TeamName name, final TeamScore score) {
        Preconditions.checkNotNull(name, "name should not be null!");
        this.name = name;
        setScore(score);
    }

    public TeamName getName() {
        return name;
    }

    public TeamScore getScore() {
        return score;
    }

    public void setScore(TeamScore score){
        Preconditions.checkNotNull(score, "score should not be null!");
        this.score = score;
    }

}
