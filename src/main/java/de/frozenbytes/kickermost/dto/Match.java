package de.frozenbytes.kickermost.dto;

public final class Match {

    private final Team teamA;
    private final Team teamB;
    private final Story story;

    public Match(final Team teamA, final Team teamB, final Story story) {
        this.teamA = teamA;
        this.teamB = teamB;
        this.story = story;
    }

    public Team getTeamA() {
        return teamA;
    }

    public Team getTeamB() {
        return teamB;
    }

    public Story getStory() {
        return story;
    }
}
