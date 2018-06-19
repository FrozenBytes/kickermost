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

   /**
    * This test fails because of violated contract. (SystemTime comparison is inverted!)
    * a: GM = null, time = null, systemTime = 2
    * b: GM = 2, time = null, systemTime = 3
    * c: GM = 1, time = null, systemTime = 1
    *
    * -> a > b
    * -> b > c
    * -> a < c
    *
    * -> Violation transitivity a > b and c < b should lead to a > c but it's a < c
    *
    */
   @Test
   public void testFail(){
      List<StoryPart> storyParts = new ArrayList<>();
      StoryPart a = new StoryPart(LocalTime.of(0, 2), null, null, StoryEvent.HALF_TIME_A_END, StoryTitle.create("halftime 1 end"),
              StoryDescription.create("halftime 2 end"));
      StoryPart b = new StoryPart(LocalTime.of(0, 3), null, GameMinute.create("2."), StoryEvent.GOAL, StoryTitle.create("Goal 2"),
              StoryDescription.create("goal 2"));
      StoryPart c = new StoryPart(LocalTime.of(0, 1), null, GameMinute.create("1."), StoryEvent.YELLOW_CARD,
              StoryTitle.create("yellow card 1"), StoryDescription.create("yellow card 1"));
      storyParts.add(a);
      storyParts.add(b);
      storyParts.add(c);

      Collections.shuffle(storyParts);
      storyParts.sort(new StoryPartTimeLineComparator());
      List<StoryPart> sortA = new ArrayList<>(storyParts);
      Collections.shuffle(storyParts);
      storyParts.sort(new StoryPartTimeLineComparator());
      List<StoryPart> sortB = new ArrayList<>(storyParts);
      Collections.shuffle(storyParts);
      storyParts.sort(new StoryPartTimeLineComparator());
      List<StoryPart> sortC = new ArrayList<>(storyParts);

      assertEquals(sortA.get(0), sortB.get(0));
      assertEquals(sortA.get(0), sortC.get(0));

      assertEquals(sortA.get(1), sortB.get(1));
      assertEquals(sortA.get(1), sortC.get(1));

      assertEquals(sortA.get(2), sortB.get(2));
      assertEquals(sortA.get(2), sortC.get(2));

   }
}