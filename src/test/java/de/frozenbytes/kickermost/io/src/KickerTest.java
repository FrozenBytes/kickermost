package de.frozenbytes.kickermost.io.src;

import de.frozenbytes.kickermost.BasicTest;
import de.frozenbytes.kickermost.conf.PropertiesLoader;
import de.frozenbytes.kickermost.conf.PropertiesHolder;
import de.frozenbytes.kickermost.dto.Match;
import de.frozenbytes.kickermost.dto.Story;
import de.frozenbytes.kickermost.dto.StoryPart;
import de.frozenbytes.kickermost.dto.Team;
import de.frozenbytes.kickermost.dto.property.TeamName;
import de.frozenbytes.kickermost.dto.property.TeamScore;
import de.frozenbytes.kickermost.dto.property.TickerUrl;
import de.frozenbytes.kickermost.dto.type.StoryEvent;
import de.frozenbytes.kickermost.exception.MatchNotStartedException;
import de.frozenbytes.kickermost.io.target.http.MattermostWebhookClient;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;


public class KickerTest extends BasicTest {

    @BeforeClass
    public static void beforeClass() throws Exception {
        propertiesHolder = PropertiesLoader.createPropertiesHolder(CONFIG_FILEPATH);
        kicker = new Kicker(TickerUrl.create(URL), propertiesHolder);
        kicker.reload();
    }

    private static final String URL = "http://www.kicker.de/news/fussball/weltmeisterschaft/spiele/weltmeisterschaft/2018/1/3070425/livematch_kroatien_nigeria.html#omrss";
    private static final String URL_MATCH_NOT_STARTED = "http://www.kicker.de/news/fussball/weltmeisterschaft/spiele/weltmeisterschaft/2018/1/3070431/spielverlauf_costa-rica_serbien-9954.html#omrss";

    private static final TeamScore TEAM_A_SCORE = TeamScore.create(5);
    private static final TeamScore TEAM_B_SCORE = TeamScore.create(0);
    private static final TeamName TEAM_A_NAME = TeamName.create("Russland");
    private static final TeamName TEAM_B_NAME = TeamName.create("Saudi-Arabien");

    private static PropertiesHolder propertiesHolder;
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

        story.forEach(System.out::println);

        assertThat(story).isNotNull().isNotEmpty().hasSize(117);
    }

    @Test(expected = MatchNotStartedException.class)
    public void testMatchNotStarted() throws Exception {
        new Kicker(TickerUrl.create(URL_MATCH_NOT_STARTED), propertiesHolder).reload();
    }

    /*
     * ===========================
     *      Mattermost tests
     * ===========================
     */

    @Test
    @Ignore
    public void postGoalTest() throws Exception {
        final Match match = new Match(new Team(TEAM_A_NAME, TEAM_A_SCORE), new Team(TEAM_B_NAME, TEAM_B_SCORE), kicker.getStory());

        MattermostWebhookClient client = new MattermostWebhookClient(propertiesHolder);
        for(StoryPart storyPart : match.getStory()) {
            if(storyPart.getEvent() == StoryEvent.GOAL) {
                client.postMessage(match, storyPart);
                break;
            }
        }
    }

    @Test
    @Ignore
    public void postOwnGoalTest() throws Exception {
        final Match match = new Match(new Team(TEAM_A_NAME, TEAM_A_SCORE), new Team(TEAM_B_NAME, TEAM_B_SCORE), kicker.getStory());

        MattermostWebhookClient client = new MattermostWebhookClient(propertiesHolder);
        for(StoryPart storyPart : match.getStory()) {
            if(storyPart.getEvent() == StoryEvent.GOAL_OWN) {
                client.postMessage(match, storyPart);
                break;
            }
        }
    }

    @Test
    @Ignore
    public void postExchangeTest() throws Exception {
        final Match match = new Match(new Team(TEAM_A_NAME, TEAM_A_SCORE), new Team(TEAM_B_NAME, TEAM_B_SCORE), kicker.getStory());

        MattermostWebhookClient client = new MattermostWebhookClient(propertiesHolder);
        for(StoryPart storyPart : match.getStory()) {
            if(storyPart.getEvent() == StoryEvent.EXCHANGE) {
                client.postMessage(match, storyPart);
                break;
            }
        }
    }

    @Test
    @Ignore
    public void postYellowCardTest() throws Exception {
        final Match match = new Match(new Team(TEAM_A_NAME, TEAM_A_SCORE), new Team(TEAM_B_NAME, TEAM_B_SCORE), kicker.getStory());

        MattermostWebhookClient client = new MattermostWebhookClient(propertiesHolder);
        for(StoryPart storyPart : match.getStory()) {
            if(storyPart.getEvent() == StoryEvent.YELLOW_CARD) {
                client.postMessage(match, storyPart);
                break;
            }
        }
    }

    @Test
    @Ignore
    public void postRedCardTest() throws Exception {
        final Match match = new Match(new Team(TEAM_A_NAME, TEAM_A_SCORE), new Team(TEAM_B_NAME, TEAM_B_SCORE), kicker.getStory());

        MattermostWebhookClient client = new MattermostWebhookClient(propertiesHolder);
        for(StoryPart storyPart : match.getStory()) {
            if(storyPart.getEvent() == StoryEvent.RED_CARD) {
                client.postMessage(match, storyPart);
                break;
            }
        }
    }

    @Test
    @Ignore
    public void postYellowRedCardTest() throws Exception {
        final Match match = new Match(new Team(TEAM_A_NAME, TEAM_A_SCORE), new Team(TEAM_B_NAME, TEAM_B_SCORE), kicker.getStory());

        MattermostWebhookClient client = new MattermostWebhookClient(propertiesHolder);
        for(StoryPart storyPart : match.getStory()) {
            if(storyPart.getEvent() == StoryEvent.YELLOW_RED_CARD) {
                client.postMessage(match, storyPart);
                break;
            }
        }
    }

    @Test
    @Ignore
    public void postPenaltyTest() throws Exception {
        final Match match = new Match(new Team(TEAM_A_NAME, TEAM_A_SCORE), new Team(TEAM_B_NAME, TEAM_B_SCORE), kicker.getStory());

        MattermostWebhookClient client = new MattermostWebhookClient(propertiesHolder);
        for(StoryPart storyPart : match.getStory()) {
            if(storyPart.getEvent() == StoryEvent.PENALTY) {
                client.postMessage(match, storyPart);
                break;
            }
        }
    }

    @Test
    @Ignore
    public void postPenaltyFailureTest() throws Exception {
        final Match match = new Match(new Team(TEAM_A_NAME, TEAM_A_SCORE), new Team(TEAM_B_NAME, TEAM_B_SCORE), kicker.getStory());

        MattermostWebhookClient client = new MattermostWebhookClient(propertiesHolder);
        for(StoryPart storyPart : match.getStory()) {
            if(storyPart.getEvent() == StoryEvent.PENALTY_FAILURE) {
                client.postMessage(match, storyPart);
                break;
            }
        }
    }

    @Test
    @Ignore
    public void postDefaultTest() throws Exception {
        final Match match = new Match(new Team(TEAM_A_NAME, TEAM_A_SCORE), new Team(TEAM_B_NAME, TEAM_B_SCORE), kicker.getStory());

        MattermostWebhookClient client = new MattermostWebhookClient(propertiesHolder);
        for(StoryPart storyPart : match.getStory()) {
            if(storyPart.getEvent() == StoryEvent.DEFAULT) {
                client.postMessage(match, storyPart);
                break;
            }
        }
    }
}
