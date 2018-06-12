package de.frozenbytes.kickermost.io.rs;

import static org.fest.assertions.Assertions.assertThat;

import de.frozenbytes.kickermost.dto.Story;
import de.frozenbytes.kickermost.dto.property.TeamName;
import de.frozenbytes.kickermost.dto.property.TeamScore;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

public class KickerTest {

    @BeforeClass
    public static void beforeClass() throws IOException {
        kicker = new Kicker(URL);
    }

    private static final String URL = "http://www.kicker.de/news/fussball/nationalelf/startseite/fussball-nationalteams-freundschaftsspiele/2018/4/4204625/livematch_deutschland_saudi-arabien.html";
    private static final TeamScore TEAM_A_SCORE = TeamScore.create(2);
    private static final TeamScore TEAM_B_SCORE = TeamScore.create(1);
    private static final TeamName TEAM_A_NAME = TeamName.create("Deutschland");
    private static final TeamName TEAM_B_NAME = TeamName.create("Saudi-Arabien");

    private static Kicker kicker;


    @Test
    public void getTeamAScore() {
        assertThat(kicker.getTeamAScore()).isEqualTo(TEAM_A_SCORE);
    }

    @Test
    public void getTeamBScore() {
        assertThat(kicker.getTeamBScore()).isEqualTo(TEAM_B_SCORE);
    }

    @Test
    public void getTeamAName() {
        assertThat(kicker.getTeamAName()).isEqualTo(TEAM_A_NAME);
    }

    @Test
    public void getTeamBName() {
        assertThat(kicker.getTeamBName()).isEqualTo(TEAM_B_NAME);
    }

    @Test
    public void getStory() {
        final Story story = kicker.getStory();

        story.forEach(p -> System.out.println(p));

        assertThat(story).isNotNull().isNotEmpty().hasSize(117);
    }
}
