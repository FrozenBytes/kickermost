package de.frozenbytes.kickermost.dto.type;

public enum StoryEvent {

    GOAL(":ball:"),
    GOAL_OWN(":red_ball:"),
    EXCHANGE(":arrow:"),
    YELLOW_CARD(":yellow:"),
    RED_CARD(":red:"),
    YELLOW_RED_CARD(":rellow-red:"),
    PENALTY(":critical:"),
    PENALTY_FAILURE(":penalty_failure"),
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
