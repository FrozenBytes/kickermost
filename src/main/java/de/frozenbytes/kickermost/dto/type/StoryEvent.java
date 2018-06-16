package de.frozenbytes.kickermost.dto.type;

import java.util.stream.Stream;

public enum StoryEvent {

    GOAL(":soccer:"),
    GOAL_OWN(":soccer:"),
    EXCHANGE(":arrows_clockwise:"),
    YELLOW_CARD(":ledger:"),
    RED_CARD(":closed_book:"),
    YELLOW_RED_CARD(":ledger::closed_book:"),
    PENALTY(":goal_net:"),
    PENALTY_FAILURE(":goal_net:"),
    VIDEO_PROOF(":movie_camera:"),

    HALF_TIME_A_START(":arrow_forward:"),
    HALF_TIME_A_END(":pause_button:"),
    HALF_TIME_B_START(":arrow_forward:"),
    HALF_TIME_B_END(":pause_button:"),
    OVERTIME_A_START(":arrow_forward:"),
    OVERTIME_A_END(":pause_button:"),
    OVERTIME_B_START(":arrow_forward:"),
    OVERTIME_B_END(":pause_button:"),
    PENALTIES_TIME(":goal_net:"),
    GAME_END(":pause_button:"),

    DEFAULT("")
    ;

    final String mattermostCode;

    StoryEvent(String mattermostCode){
        this.mattermostCode = mattermostCode;
    }

    public String getMattermostCode() {
        return mattermostCode;
    }

    public static StoryEvent[] getAllowedEvents(){
        return Stream.of(StoryEvent.values())
                .filter(ev -> ev != StoryEvent.DEFAULT)
                .toArray(StoryEvent[]::new);
    }
}
