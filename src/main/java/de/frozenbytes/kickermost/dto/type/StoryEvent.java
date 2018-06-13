package de.frozenbytes.kickermost.dto.type;

public enum StoryEvent {

    GOAL(":soccer:"),
    GOAL_OWN(":soccer:"),
    EXCHANGE(":arrows_clockwise:"),
    YELLOW_CARD(":ledger:"),
    RED_CARD(":closed_book:"),
    YELLOW_RED_CARD(":ledger::closed_book:"),
    PENALTY(":goal_net:"),
    PENALTY_FAILURE(":goal_net:"),
    DEFAULT("")
    ;

    final String mattermostCode;

    StoryEvent(String mattermostCode){
        this.mattermostCode = mattermostCode;
    }

    public String getMattermostCode() {
        return mattermostCode;
    }
}
