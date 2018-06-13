package de.frozenbytes.kickermost.io.rs;

import static org.fest.assertions.Assertions.assertThat;

import de.frozenbytes.kickermost.dto.Match;
import de.frozenbytes.kickermost.dto.Story;
import de.frozenbytes.kickermost.dto.StoryPart;
import de.frozenbytes.kickermost.dto.Team;
import de.frozenbytes.kickermost.dto.property.TeamName;
import de.frozenbytes.kickermost.dto.property.TeamScore;
import de.frozenbytes.kickermost.dto.type.StoryEvent;
import de.frozenbytes.kickermost.http.MattermostWebhookClient;
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

    /*
     * ===========================
     *      Mattermost tests
     * ===========================
     */

    @Test
    public void postGoalTest() {
        final Match match = new Match(new Team(TEAM_A_NAME, TEAM_A_SCORE), new Team(TEAM_B_NAME, TEAM_B_SCORE), kicker.getStory());

        MattermostWebhookClient client = new MattermostWebhookClient();
        for(StoryPart storyPart : match.getStory()) {
            if(storyPart.getEvent() == StoryEvent.GOAL) {
                client.postMessage(match, storyPart);
                break;
            }
        }
    }

    @Test
    public void postOwnGoalTest() {
        final Match match = new Match(new Team(TEAM_A_NAME, TEAM_A_SCORE), new Team(TEAM_B_NAME, TEAM_B_SCORE), kicker.getStory());

        MattermostWebhookClient client = new MattermostWebhookClient();
        for(StoryPart storyPart : match.getStory()) {
            if(storyPart.getEvent() == StoryEvent.GOAL_OWN) {
                client.postMessage(match, storyPart);
                break;
            }
        }
    }

    @Test
    public void postExchangeTest() {
        final Match match = new Match(new Team(TEAM_A_NAME, TEAM_A_SCORE), new Team(TEAM_B_NAME, TEAM_B_SCORE), kicker.getStory());

        MattermostWebhookClient client = new MattermostWebhookClient();
        for(StoryPart storyPart : match.getStory()) {
            if(storyPart.getEvent() == StoryEvent.EXCHANGE) {
                client.postMessage(match, storyPart);
                break;
            }
        }
    }

    @Test
    public void postYellowCardTest() {
        final Match match = new Match(new Team(TEAM_A_NAME, TEAM_A_SCORE), new Team(TEAM_B_NAME, TEAM_B_SCORE), kicker.getStory());

        MattermostWebhookClient client = new MattermostWebhookClient();
        for(StoryPart storyPart : match.getStory()) {
            if(storyPart.getEvent() == StoryEvent.YELLOW_CARD) {
                client.postMessage(match, storyPart);
                break;
            }
        }
    }

    @Test
    public void postRedCardTest() {
        final Match match = new Match(new Team(TEAM_A_NAME, TEAM_A_SCORE), new Team(TEAM_B_NAME, TEAM_B_SCORE), kicker.getStory());

        MattermostWebhookClient client = new MattermostWebhookClient();
        for(StoryPart storyPart : match.getStory()) {
            if(storyPart.getEvent() == StoryEvent.RED_CARD) {
                client.postMessage(match, storyPart);
                break;
            }
        }
    }

    @Test
    public void postYellowRedCardTest() {
        final Match match = new Match(new Team(TEAM_A_NAME, TEAM_A_SCORE), new Team(TEAM_B_NAME, TEAM_B_SCORE), kicker.getStory());

        MattermostWebhookClient client = new MattermostWebhookClient();
        for(StoryPart storyPart : match.getStory()) {
            if(storyPart.getEvent() == StoryEvent.YELLOW_RED_CARD) {
                client.postMessage(match, storyPart);
                break;
            }
        }
    }

    @Test
    public void postPenaltyTest() {
        final Match match = new Match(new Team(TEAM_A_NAME, TEAM_A_SCORE), new Team(TEAM_B_NAME, TEAM_B_SCORE), kicker.getStory());

        MattermostWebhookClient client = new MattermostWebhookClient();
        for(StoryPart storyPart : match.getStory()) {
            if(storyPart.getEvent() == StoryEvent.PENALTY) {
                client.postMessage(match, storyPart);
                break;
            }
        }
    }

    @Test
    public void postPenaltyFailureTest() {
        final Match match = new Match(new Team(TEAM_A_NAME, TEAM_A_SCORE), new Team(TEAM_B_NAME, TEAM_B_SCORE), kicker.getStory());

        MattermostWebhookClient client = new MattermostWebhookClient();
        for(StoryPart storyPart : match.getStory()) {
            if(storyPart.getEvent() == StoryEvent.PENALTY_FAILURE) {
                client.postMessage(match, storyPart);
                break;
            }
        }
    }

    @Test
    public void postDefaultTest() {
        final Match match = new Match(new Team(TEAM_A_NAME, TEAM_A_SCORE), new Team(TEAM_B_NAME, TEAM_B_SCORE), kicker.getStory());

        MattermostWebhookClient client = new MattermostWebhookClient();
        for(StoryPart storyPart : match.getStory()) {
            if(storyPart.getEvent() == StoryEvent.DEFAULT) {
                client.postMessage(match, storyPart);
                break;
            }
        }
    }
}
