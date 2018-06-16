package de.frozenbytes.kickermost.io.src;

import com.google.common.base.Preconditions;
import de.frozenbytes.kickermost.dto.Story;
import de.frozenbytes.kickermost.dto.StoryPart;
import de.frozenbytes.kickermost.dto.property.*;
import de.frozenbytes.kickermost.dto.type.StoryEvent;
import de.frozenbytes.kickermost.exception.MatchNotStartedException;
import de.frozenbytes.kickermost.exception.ReloadPollingSourceException;
import de.frozenbytes.kickermost.exception.TickerNotInSourceException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Kicker implements PollingSource {

    private static final Logger logger = LoggerFactory.getLogger(Kicker.class);

    private static final String CSS_ROOT = "div#ovMain";
    private static final String CSS_MATCH = CSS_ROOT + " div#ovMatchHeader div#pageTitle table.liveTitle tr#SpielpaarungLiveTitleRow";
    private static final String CSS_GAME_NOT_STARTED = CSS_ROOT + " tr#ctl00_PlaceHolderContent_spielereignisse_contentContainer_NoDataEreignisse > td.nodata";

    private static final String CSS_SCOREBOARD = CSS_MATCH + " td.lttabst div.ergBoardExtT";
    private static final String CSS_TEAM_A_SCORE = CSS_SCOREBOARD + " div#ovBoardExtMainH";
    private static final String CSS_TEAM_B_SCORE = CSS_SCOREBOARD + " div#ovBoardExtMainA";

    private static final String CSS_TEAM_A_NAME = CSS_MATCH + " td.lttabvrnName > h1 > a";
    private static final String CSS_TEAM_B_NAME = CSS_MATCH + " td.lttabvrnName+td+td > h1 > a";

    private static final String CSS_STORY = CSS_ROOT + " div#ovContent div#spielereignisse_maincont table#tabSpielereignisse tr:not(.trTickerBegegnung,.livecom,.height0,.tr_sep)";
    private static final String CSS_STORY_SUB_TIME = "div.lttdspzeit > div.ltspst";
    private static final String CSS_STORY_SUB_GAME_MINUTE = "div.lttdspzeit > div.ltereig > b";
    private static final String CSS_STORY_SUB_TEXT = "div.ltereigtxt";
    private static final String CSS_STORY_SUB_ICON = "div.lttdspst div.ltereig";

    private final TickerUrl tickerUrl;
    private Document document;


    public Kicker(final TickerUrl tickerUrl) {
        super();
        Preconditions.checkNotNull(tickerUrl, "tickerUrl should not be null!");
        this.tickerUrl = tickerUrl;
    }

    @Override
    public TeamScore getTeamAScore(){
        checkDocument();
        return TeamScore.create(Integer.parseInt(getFirstText(document, CSS_TEAM_A_SCORE)));
    }

    @Override
    public TeamScore getTeamBScore(){
        checkDocument();
        return TeamScore.create(Integer.parseInt(getFirstText(document, CSS_TEAM_B_SCORE)));
    }

    @Override
    public TeamName getTeamAName(){
        checkDocument();
        return TeamName.create(getFirstText(document, CSS_TEAM_A_NAME));
    }

    @Override
    public TeamName getTeamBName(){
        checkDocument();
        return TeamName.create(getFirstText(document, CSS_TEAM_B_NAME));
    }

    @Override
    public Story getStory(){
        checkDocument();

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

            final Element storyTextElement = e.selectFirst(CSS_STORY_SUB_TEXT);
            //StoryTitle
            final StoryTitle storyTitle = storyTextElement == null ? null : parseStoryTitle(storyTextElement);

            //StoryDescription
            final StoryDescription storyDescription = storyTextElement == null ? null : parseStoryDescription(storyTextElement);

            //StoryEvent
            final Element tickerIconElement = e.selectFirst(CSS_STORY_SUB_ICON);
            final StoryEvent storyEvent = parseStoryEvent(tickerIconElement);

            story.add(new StoryPart(time, gameMinute, storyEvent, storyTitle, storyDescription));
        }

        return story;
    }

    @Override
    public void reload() throws ReloadPollingSourceException, TickerNotInSourceException, MatchNotStartedException {
        try {
            document = Jsoup.connect(tickerUrl.getValue()).get();
            if(document.selectFirst(CSS_ROOT) == null){
                throw new TickerNotInSourceException(tickerUrl);
            }
            if(document.selectFirst(CSS_GAME_NOT_STARTED) != null){
                throw new MatchNotStartedException(tickerUrl);
            }
        } catch (IOException e) {
            throw new ReloadPollingSourceException(tickerUrl, e);
        }
    }

    private String getFirstText(final Document document, final String cssSelector){
        return document.select(cssSelector).first().text();
    }

    private LocalTime parseLocalTime(final Element timeElement){
        final String hourMinute = timeElement.text().replace(" Uhr", "");
        return LocalTime.parse(hourMinute + ":00", DateTimeFormatter.ISO_LOCAL_TIME);
    }

    private GameMinute parseGameMinute(final Element gameMinuteElement){
        return GameMinute.create(gameMinuteElement.text());
    }

    private StoryTitle parseStoryTitle(final Element storyTitleElement){
        return StoryTitle.create(parseTextBlock(storyTitleElement, true));
    }

    private StoryDescription parseStoryDescription(final Element storyDescriptionElement){
        return StoryDescription.create(parseTextBlock(storyDescriptionElement, false));
    }

    private String parseTextBlock(Element element, boolean title){
        final String html = element.html();
        if(html == null){
            return null;
        }
        final String[] parts = html.split("<br>");
        if(title){
            return reparseHtmlAndRecieveText(parts[0]);
        }else{
            final String descriptionPart = Stream.of(parts).skip(1).collect(Collectors.joining("<br>"));
            return reparseHtmlAndRecieveText(descriptionPart);
        }
    }

    private String reparseHtmlAndRecieveText(String html){
        if(html == null || html.trim().isEmpty()){
            return null;
        }
        return Jsoup.parse(html).text();
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
                case "ergtyp_1-m.png":
                    return StoryEvent.GOAL;
                case "eig-tor-m.png":
                    return StoryEvent.GOAL_OWN;
                case "ergtyp_2-m.png":
                    return StoryEvent.YELLOW_CARD;
                case "ergtyp_3-m.png":
                    return StoryEvent.YELLOW_RED_CARD;
                case "ergtyp_4-m.png":
                    return StoryEvent.RED_CARD;
                case "ergtyp_5-m.png":
                    return StoryEvent.EXCHANGE;
                case "ergtyp_30-m.png":
                    return StoryEvent.PENALTY;
                case "ergtyp_7-m.png":
                    return StoryEvent.PENALTY_FAILURE;
                case "anstoss-m.png":
                    return StoryEvent.KICKOFF;
                case "abpfiff-m.png":
                    return StoryEvent.FINAL_WHISTLE;
                case "ergtyp_70-m.png":
                case "ergtyp_71-m.png":
                    return StoryEvent.VIDEO_PROOF;
                default:
                    logger.warn(String.format("Unexpected imageFileName: %s", imageFileName));
                    return StoryEvent.DEFAULT;
                    //throw new IllegalStateException(String.format("Unexpected imageFileName: %s", imageFileName));
            }
        }
    }

    private void checkDocument(){
        Preconditions.checkNotNull(document, "document is null. Use reload() first to initialize it!");
    }

}
