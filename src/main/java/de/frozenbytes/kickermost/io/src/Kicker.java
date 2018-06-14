package de.frozenbytes.kickermost.io.src;

import com.google.common.base.Preconditions;
import de.frozenbytes.kickermost.dto.Story;
import de.frozenbytes.kickermost.dto.StoryPart;
import de.frozenbytes.kickermost.dto.property.*;
import de.frozenbytes.kickermost.dto.type.StoryEvent;
import de.frozenbytes.kickermost.exception.ReloadPollingSourceException;
import de.frozenbytes.kickermost.exception.TickerNotInSourceException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

public class Kicker implements PollingSource {

    private static final String CSS_ROOT = "#tabSpielereignisse";
    private static final String CSS_MATCH = CSS_ROOT + " tr.trTickerBegegnung";

    private static final String CSS_SCOREBOARD = CSS_MATCH + " td.begErg > div.scoreboard";
    private static final String CSS_TEAM_A_SCORE = CSS_SCOREBOARD + " div.boardH";
    private static final String CSS_TEAM_B_SCORE = CSS_SCOREBOARD + " div.boardA";

    private static final String CSS_TEAM_A_NAME = CSS_MATCH + " td.first > a";
    private static final String CSS_TEAM_B_NAME = CSS_MATCH + " td.last > a";

    private static final String CSS_STORY = CSS_ROOT + " tr:not(.trTickerBegegnung,.livecom,.height0,.tr_sep)";
    private static final String CSS_STORY_SUB_TIME = "div.tickerZeit";
    private static final String CSS_STORY_SUB_GAME_MINUTE = "div.tickerMin";
    private static final String CSS_STORY_SUB_TITLE = "div.tickerItemHeader";
    private static final String CSS_STORY_SUB_DESCRIPTION = "div.ltereigkurz > p";
    private static final String CSS_STORY_SUB_ICON = "div.tickerIcon";

    private final TickerUrl tickerUrl;
    private Document document;


    public Kicker(final TickerUrl tickerUrl) throws ReloadPollingSourceException, TickerNotInSourceException {
        super();
        Preconditions.checkNotNull(tickerUrl, "tickerUrl should not be null!");
        this.tickerUrl = tickerUrl;
        reload();
    }

    @Override
    public TeamScore getTeamAScore(){
        return TeamScore.create(Integer.parseInt(getFirstText(document, CSS_TEAM_A_SCORE)));
    }

    @Override
    public TeamScore getTeamBScore(){
        return TeamScore.create(Integer.parseInt(getFirstText(document, CSS_TEAM_B_SCORE)));
    }

    @Override
    public TeamName getTeamAName(){
        return TeamName.create(getFirstText(document, CSS_TEAM_A_NAME));
    }

    @Override
    public TeamName getTeamBName(){
        return TeamName.create(getFirstText(document, CSS_TEAM_B_NAME));
    }

    @Override
    public Story getStory(){
        final Story story = new Story();

        final Elements elements = document.select(CSS_STORY);
        final Iterator<Element> iterator = elements.iterator();
        while (iterator.hasNext()){
            final Element e = iterator.next();

            //Time
            final Element timeElement = e.selectFirst(CSS_STORY_SUB_TIME);
            if(timeElement == null){
                continue; //skip this unparseable row..
            }
            Preconditions.checkNotNull(timeElement, "timeElement should not be null!");
            final LocalTime time = parseLocalTime(timeElement);

            //GameMinute
            final Element gameMinuteElement = e.selectFirst(CSS_STORY_SUB_GAME_MINUTE);
            final GameMinute gameMinute = gameMinuteElement == null ? null : parseGameMinute(gameMinuteElement);

            //StoryTitle
            final Element storyTitleElement = e.selectFirst(CSS_STORY_SUB_TITLE);
            final StoryTitle storyTitle = storyTitleElement == null ? null : parseStoryTitle(storyTitleElement);

            //StoryDescription
            final Element storyDescriptionElement = e.selectFirst(CSS_STORY_SUB_DESCRIPTION);
            final StoryDescription storyDescription = storyDescriptionElement == null ? null : parseStoryDescription(storyDescriptionElement);

            //StoryEvent
            final Element tickerIconElement = e.selectFirst(CSS_STORY_SUB_ICON);
            final StoryEvent storyEvent = parseStoryEvent(tickerIconElement);

            story.add(new StoryPart(time, gameMinute, storyEvent, storyTitle, storyDescription));
        }

        return story;
    }

    @Override
    public void reload() throws ReloadPollingSourceException, TickerNotInSourceException {
        try {
            document = Jsoup.connect(tickerUrl.getValue()).get();
            if(document.selectFirst(CSS_ROOT) == null){
                throw new TickerNotInSourceException(tickerUrl);
            }
        } catch (IOException e) {
            throw new ReloadPollingSourceException(tickerUrl, e);
        }
    }

    private String getFirstText(final Document document, final String cssSelector){
        return document.select(cssSelector).first().text();
    }

    private LocalTime parseLocalTime(final Element timeElement){
        final String hourMinute = timeElement.text();
        return LocalTime.parse(hourMinute + ":00", DateTimeFormatter.ISO_LOCAL_TIME);
    }

    private GameMinute parseGameMinute(final Element gameMinuteElement){
        return GameMinute.create(gameMinuteElement.text());
    }

    private StoryTitle parseStoryTitle(final Element storyTitleElement){
        return StoryTitle.create(storyTitleElement.text());
    }

    private StoryDescription parseStoryDescription(final Element storyDescriptionElement){
        return StoryDescription.create(storyDescriptionElement.text());
    }

    private StoryEvent parseStoryEvent(final Element tickerIconElement){
        if(tickerIconElement == null){
            return StoryEvent.DEFAULT;
        } else {
            final String imageTag = tickerIconElement.html();
            final String imageFileName = imageTag.trim().isEmpty() ? "" : imageTag.substring(imageTag.lastIndexOf("/") + 1, imageTag.lastIndexOf("alt") - 2);
            switch (imageFileName){
                case "":
                    return StoryEvent.DEFAULT;
                case "ergtyp_1-l_v2.png":
                    return StoryEvent.GOAL;
                case "eig-tor-l_v2.png":
                    return StoryEvent.GOAL_OWN;
                case "ergtyp_2-l_v2.png":
                    return StoryEvent.YELLOW_CARD;
                case "ergtyp_3-l_v2.png":
                    return StoryEvent.YELLOW_RED_CARD;
                case "ergtyp_4-l_v2.png":
                    return StoryEvent.RED_CARD;
                case "ergtyp_5-l_v2.png":
                    return StoryEvent.EXCHANGE;
                case "ergtyp_30-l_v2.png":
                    return StoryEvent.PENALTY;
                case "ergtyp_7-l_v2.png":
                    return StoryEvent.PENALTY_FAILURE;
                default:
                    throw new IllegalStateException(String.format("Unexpected imageFileName: %s", imageFileName));
            }
        }
    }

}
