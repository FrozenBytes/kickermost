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
    KICKOFF(":arrow_forward:"),
    FINAL_WHISTLE(":pause_button:"),
    VIDEO_PROOF(":movie_camera:"),
    DEFAULT("")
    ;
    private static final StoryEvent[] ALLOWED_STORY_EVENTS = {GOAL, GOAL_OWN, YELLOW_CARD, YELLOW_RED_CARD, RED_CARD, PENALTY, PENALTY_FAILURE, KICKOFF, FINAL_WHISTLE, VIDEO_PROOF, EXCHANGE};

    final String mattermostCode;

    StoryEvent(String mattermostCode){
        this.mattermostCode = mattermostCode;
    }

    public String getMattermostCode() {
        return mattermostCode;
    }

    public static StoryEvent[] getAllowedEvents(){
        return ALLOWED_STORY_EVENTS;
    }
}
