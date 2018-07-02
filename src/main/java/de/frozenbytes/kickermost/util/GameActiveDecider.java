package de.frozenbytes.kickermost.util;

import de.frozenbytes.kickermost.dto.StoryPart;
import de.frozenbytes.kickermost.dto.type.StoryEvent;

import java.util.List;

public class GameActiveDecider {

    private boolean gameStart = false;
    private boolean gameEnd = false;
    private boolean overtimeStart = false;
    private boolean overtimeEnd = false;
    private boolean penaltiesStart = false;
    private boolean penaltiesEnd = false;

    public GameActiveDecider() {
    }

    public GameActiveDecider(List<StoryPart> storyParts) {
        for (StoryPart storyPart : storyParts) {
            // game end message types
            if (storyPart.isSentToMattermost() && storyPart.getEvent() == StoryEvent.PENALTIES_END) {
                this.setPenaltiesEnded();
            }
            if (storyPart.isSentToMattermost() && storyPart.getEvent() == StoryEvent.OVERTIME_B_END) {
                this.setOvertimeEnded();
            }
            if (storyPart.isSentToMattermost() && (storyPart.getEvent() == StoryEvent.GAME_END || storyPart.getEvent() == StoryEvent.HALF_TIME_B_END)) {
                this.setGameEnded();
            }
            // game start message types
            if (storyPart.getEvent() == StoryEvent.HALF_TIME_A_START) {
                this.setGameStarted();
            }
            if (storyPart.getEvent() == StoryEvent.OVERTIME_A_START) {
                this.setOvertimeStarted();
            }
            if (storyPart.getEvent() == StoryEvent.PENALTIES_START) {
                this.setPenaltiesStarted();
            }
        }
    }

    public void setGameStarted() {
        this.gameStart = true;
    }

    public void setGameEnded() {
        this.gameEnd = true;
    }

    public void setOvertimeStarted() {
        this.overtimeStart = true;
    }

    public void setOvertimeEnded() {
        this.overtimeEnd = true;
    }

    public void setPenaltiesStarted() {
        this.penaltiesStart = true;
    }

    public void setPenaltiesEnded() {
        this.penaltiesEnd = true;
    }

    public boolean isGameActive(){
        return gameStart && !isGameOver();
    }

    public boolean isGameOver(){
        if(penaltiesEnd){
            return true;
        }
        if(overtimeEnd && !penaltiesStart){
            return true;
        }
        return gameEnd && !overtimeStart;
    }
}
