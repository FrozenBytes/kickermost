package de.frozenbytes.kickermost.dto.property;

import de.frozenbytes.kickermost.dto.StoryPart;
import de.frozenbytes.kickermost.dto.type.StoryEvent;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static org.junit.Assert.*;

import de.frozenbytes.kickermost.util.comparator.StoryPartTimeLineComparator;
import org.junit.Test;

public class StoryPartTimeLineComparatorTest {

   @Test
   public void compare() {
      List<StoryPart> storyParts = new ArrayList<>();

      storyParts.add(new StoryPart(LocalTime.of(0, 1), null, null, StoryEvent.HALF_TIME_A_END, StoryTitle.create("halftime 1 end"),
            StoryDescription.create("halftime 2 end")));
      storyParts.add(
            new StoryPart(LocalTime.of(0, 2), LocalTime.of(1, 29), GameMinute.create("45."), StoryEvent.GOAL, StoryTitle.create("Goal 2"),
                  StoryDescription.create("goal 2")));
      storyParts.add(new StoryPart(LocalTime.of(0, 3), LocalTime.of(1, 29), GameMinute.create("45."), StoryEvent.YELLOW_CARD,
            StoryTitle.create("yellow card 1"), StoryDescription.create("yellow card 1")));
      storyParts.add(
            new StoryPart(LocalTime.of(0, 4), LocalTime.of(0, 59), GameMinute.create("15."), StoryEvent.GOAL, StoryTitle.create("Goal 1"),
                  StoryDescription.create("goal 1")));
      storyParts.add(new StoryPart(LocalTime.of(0, 5), LocalTime.of(1, 0), GameMinute.create("13."), StoryEvent.DEFAULT, StoryTitle
            .create("default 3"), StoryDescription.create("default 3")));
      storyParts.add(new StoryPart(LocalTime.of(0, 5), LocalTime.of(1, 0), GameMinute.create("13."), StoryEvent.DEFAULT, StoryTitle
            .create("default 2"), StoryDescription.create("default 2")));
      storyParts.add(new StoryPart(LocalTime.of(0, 6), LocalTime.of(0, 59), GameMinute.create("13."), StoryEvent.DEFAULT, StoryTitle
            .create("default 1"), StoryDescription.create("default 1")));
      storyParts.add(new StoryPart(LocalTime.of(0, 7), null, null, StoryEvent.HALF_TIME_A_START, StoryTitle.create("halftime 1 start"),
            StoryDescription.create("halftime 1 start")));

      Collections.shuffle(storyParts);
      storyParts.sort(new StoryPartTimeLineComparator());

      assertEquals("halftime 1 start", storyParts.get(0).getTitle().getValue());
      assertEquals("halftime 1 end", storyParts.get(storyParts.size()-1).getTitle().getValue());
      assertEquals("Goal 1", storyParts.get(4).getTitle().getValue());

      Collections.shuffle(storyParts);
      storyParts.sort(new StoryPartTimeLineComparator());

      assertEquals("halftime 1 start", storyParts.get(0).getTitle().getValue());
      assertEquals("halftime 1 end", storyParts.get(storyParts.size()-1).getTitle().getValue());
      assertEquals("Goal 1", storyParts.get(4).getTitle().getValue());

      Collections.shuffle(storyParts);
      storyParts.sort(new StoryPartTimeLineComparator());

      assertEquals("halftime 1 start", storyParts.get(0).getTitle().getValue());
      assertEquals("halftime 1 end", storyParts.get(storyParts.size()-1).getTitle().getValue());
      assertEquals("Goal 1", storyParts.get(4).getTitle().getValue());

   }
}