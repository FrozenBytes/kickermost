package de.frozenbytes.kickermost.dto.property;

import de.frozenbytes.kickermost.dto.StoryPart;

import java.util.Comparator;

public class StoryPartTimeLineComparator implements Comparator<StoryPart> {

    @Override
    public int compare(StoryPart message1, StoryPart message2) {
        if (message1.getGameMinute() != null && message2.getGameMinute() != null) {
            int comparedGameMinute = message1.getGameMinute().getValue().compareTo(message2.getGameMinute().getValue());
            if(comparedGameMinute != 0){
                return comparedGameMinute;
            }
        }
        if(message1.getTime() != null && message2.getTime() != null) {
            int comparedTime =  message1.getTime().compareTo(message2.getTime());
            if(comparedTime != 0){
                return comparedTime;
            }
        }
        return message1.getSystemTime().compareTo(message2.getSystemTime()) * -1;
    }
}
