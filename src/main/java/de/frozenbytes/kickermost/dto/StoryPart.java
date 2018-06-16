package de.frozenbytes.kickermost.dto;

import de.frozenbytes.kickermost.dto.property.GameMinute;
import de.frozenbytes.kickermost.dto.property.StoryDescription;
import de.frozenbytes.kickermost.dto.property.StoryTitle;
import de.frozenbytes.kickermost.dto.type.StoryEvent;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.Objects;

public final class StoryPart implements Serializable {

    private final LocalTime time;
    private final GameMinute gameMinute;
    private final StoryEvent event;
    private final StoryTitle title;
    private final StoryDescription description;


    public StoryPart(final LocalTime time,
                     final GameMinute gameMinute,
                     final StoryEvent event,
                     final StoryTitle title,
                     final StoryDescription description) {
        this.time = time;
        this.gameMinute = gameMinute;
        this.event = event;
        this.title = title;
        this.description = description;
    }

    public LocalTime getTime() {
        return time;
    }

    public GameMinute getGameMinute() {
        return gameMinute;
    }

    public StoryEvent getEvent() {
        return event;
    }

    public StoryTitle getTitle() {
        return title;
    }

    public StoryDescription getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return String.format("%s%s - %s",
                time.toString(),
                gameMinute == null ? "" : "|" + gameMinute.getValue(),
                (title == null ? "" : title.getValue()) + " " + (description == null ? "" : description.getValue())
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StoryPart storyPart = (StoryPart) o;
        return Objects.equals(time, storyPart.time) &&
                event == storyPart.event;
    }

    @Override
    public int hashCode() {

        return Objects.hash(time, event);
    }
}
