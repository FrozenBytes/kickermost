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
    PENALTY_GOAL(":soccer:"),
    PENALTY_FAILURE(":x:"),
    VIDEO_PROOF(":movie_camera:"),

    HALF_TIME_A_START(":arrow_forward:"),
    HALF_TIME_A_END(":pause_button:"),
    HALF_TIME_B_START(":arrow_forward:"),
    HALF_TIME_B_END(":stop_button:"),
    OVERTIME_A_START(":arrow_forward:"),
    OVERTIME_A_END(":pause_button:"),
    OVERTIME_B_START(":arrow_forward:"),
    OVERTIME_B_END(":pause_button:"),
    PENALTIES_START(":arrow_forward:"),
    PENALTIES_END(":stop_button:"),
    GAME_END(":stop_button:"),

    DEFAULT("")
    ;

    final String mattermostCode;

    public static StoryEvent[] startStopEvents = {
            HALF_TIME_A_START,
            HALF_TIME_A_END,
            HALF_TIME_B_START,
            HALF_TIME_B_END,
            OVERTIME_A_START,
            OVERTIME_A_END,
            OVERTIME_B_START,
            OVERTIME_B_END,
            PENALTIES_START,
            PENALTIES_END,
            GAME_END,};

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
