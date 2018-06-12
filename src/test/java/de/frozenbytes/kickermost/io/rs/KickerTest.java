package de.frozenbytes.kickermost.io.rs;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

public class KickerTest {

    @BeforeClass
    public static void beforeClass() throws IOException {
        kicker = new Kicker(URL);
    }

    private static final String URL = "http://www.kicker.de/news/fussball/nationalelf/startseite/fussball-nationalteams-freundschaftsspiele/2018/4/4204625/livematch_deutschland_saudi-arabien.html";
    private static final String TEAM_A_SCORE = "2";
    private static final String TEAM_B_SCORE = "1";

    private static Kicker kicker;


    @Test
    public void testGetTeamAScore(){
        assertThat(kicker.getTeamAScore()).isEqualTo(TEAM_A_SCORE);
    }

    @Test
    public void testGetTeamBScore(){
        assertThat(kicker.getTeamBScore()).isEqualTo(TEAM_B_SCORE);
    }

}
