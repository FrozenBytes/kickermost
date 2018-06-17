package de.frozenbytes.kickermost.dto;

import com.google.common.collect.ImmutableList;

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

    public ImmutableList<StoryPart> getStory() {
        synchronized (story){
            return ImmutableList.copyOf(story);
        }
    }

    public void addStoryPart(final StoryPart storyPart){
        synchronized (story) {
            story.add(storyPart);
        }
    }

}
