package de.frozenbytes.kickermost.io.src;

import com.google.common.base.Preconditions;
import de.frozenbytes.kickermost.conf.PropertiesHolder;
import de.frozenbytes.kickermost.dto.Story;
import de.frozenbytes.kickermost.dto.StoryPart;
import de.frozenbytes.kickermost.dto.property.GameMinute;
import de.frozenbytes.kickermost.dto.property.StoryDescription;
import de.frozenbytes.kickermost.dto.property.StoryTitle;
import de.frozenbytes.kickermost.dto.property.TeamName;
import de.frozenbytes.kickermost.dto.property.TeamScore;
import de.frozenbytes.kickermost.dto.property.TickerUrl;
import de.frozenbytes.kickermost.dto.type.StoryEvent;
import de.frozenbytes.kickermost.exception.MatchNotStartedException;
import de.frozenbytes.kickermost.exception.ReloadPollingSourceException;
import de.frozenbytes.kickermost.exception.TickerNotInSourceException;
import de.frozenbytes.kickermost.util.jsoup.JsoupUtility;
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

/**
 * kicker.de has different DOM structures for live and past matches.
 * This class supports only live matches, since the CSS classes for past matches differs!
 */
public class Kicker implements PollingSource {

    private static final Logger logger = LoggerFactory.getLogger(Kicker.class);

    private static final String CSS_ROOT = "div#ovMain";
    private static final String CSS_MATCH = CSS_ROOT + " div#ovMatchHeader div#pageTitle table.liveTitle tr#SpielpaarungLiveTitleRow";
    private static final String CSS_GAME_NOT_STARTED = CSS_ROOT + " tr#ctl00_PlaceHolderContent_spielereignisse_contentContainer_NoDataEreignisse > td.nodata";

    private static final String CSS_SCOREBOARD_HALFTIME_A = CSS_MATCH + " td.lttabst div.ergBoard";
    private static final String CSS_TEAM_A_SCORE_HALFTIME_A = CSS_SCOREBOARD_HALFTIME_A + " div#ovBoardMainH";
    private static final String CSS_TEAM_B_SCORE_HALFTIME_A = CSS_SCOREBOARD_HALFTIME_A + " div#ovBoardMainA";

    private static final String CSS_SCOREBOARD_HALFTIME_B = CSS_MATCH + " td.lttabst div.ergBoardExtT";
    private static final String CSS_TEAM_A_SCORE_HALFTIME_B = CSS_SCOREBOARD_HALFTIME_B + " div#ovBoardExtMainH";
    private static final String CSS_TEAM_B_SCORE_HALFTIME_B = CSS_SCOREBOARD_HALFTIME_B + " div#ovBoardExtMainA";

    private static final String CSS_TEAM_A_NAME = CSS_MATCH + " td.lttabvrnName > h1 > a";
    private static final String CSS_TEAM_B_NAME = CSS_MATCH + " td.lttabvrnName+td+td > h1 > a";

    private static final String CSS_STORY = CSS_ROOT + " div#ovContent div#spielereignisse_maincont table#tabSpielereignisse tr:not(.trTickerBegegnung,.livecom,.height0,.tr_sep)";
    private static final String CSS_STORY_SUB_TIME = "div.tickerZeit";
    private static final String CSS_STORY_SUB_GAME_MINUTE = "div.tickerMin";
    private static final String CSS_STORY_SUB_TEXT = "div.ltereigtxt";
    private static final String CSS_STORY_SUB_ICON = "div.tickerIcon";
    private static final String CSS_STORY_HALF_TIME = "div.tickerHalbzeitText";
    private static final String CSS_STORY_STATISTICS = "div.tickerOpta";

    private static final String HALF_TIME_START = "anpfiff";
    private static final String HALF_TIME_END = "abpfiff";
    private static final String HALF_TIME_A = "1.";
    private static final String HALF_TIME_B = "2.";
    private static final String OVERTIME = "verlängerung";
    private static final String PENALTIES = "elfmeter";
    private static final String GAME_END = "schlusspfiff";

    private final TickerUrl tickerUrl;
    private final PropertiesHolder propertiesHolder;
    private Document document;


    public Kicker(final TickerUrl tickerUrl, final PropertiesHolder propertiesHolder) {
        super();
        Preconditions.checkNotNull(tickerUrl, "tickerUrl should not be null!");
        this.tickerUrl = tickerUrl;
        Preconditions.checkNotNull(propertiesHolder, "propertiesHolder should not be null!");
        this.propertiesHolder = propertiesHolder;
    }

    @Override
    public TeamScore getTeamAScore(){
        checkDocument();
        final Element a = document.selectFirst(CSS_TEAM_A_SCORE_HALFTIME_A);
        final Element b = document.selectFirst(CSS_TEAM_A_SCORE_HALFTIME_B);

        if(a != null && b != null){
            throw new IllegalStateException("Both halftimes could be found. This should not be possible?!");
        }

        if(a != null){
            return TeamScore.create(Integer.parseInt(a.text()));
        }
        if(b != null){
            return TeamScore.create(Integer.parseInt(b.text()));
        }

        throw new IllegalStateException("Neither halftime A nor B could be found!");
    }

    @Override
    public TeamScore getTeamBScore(){
        checkDocument();
        final Element a = document.selectFirst(CSS_TEAM_B_SCORE_HALFTIME_A);
        final Element b = document.selectFirst(CSS_TEAM_B_SCORE_HALFTIME_B);

        if(a != null && b != null){
            throw new IllegalStateException("Both halftimes could be found. This should not be possible?!");
        }

        if(a != null){
            return TeamScore.create(Integer.parseInt(a.text()));
        }
        if(b != null){
            return TeamScore.create(Integer.parseInt(b.text()));
        }

        throw new IllegalStateException("Neither halftime A nor B could be found!");
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
                //HalfTime row?
                final Element halfTimeElement = e.selectFirst(CSS_STORY_HALF_TIME);
                if(halfTimeElement != null){
                    final String halfTimeText = halfTimeElement.text();
                    logger.info("Found html row having half time: " + halfTimeText);
                    story.add(parseHalfTimeStoryPart(halfTimeText));
                }

                logger.info("Skipped html row having no time");
                continue; //skip this unparseable row..
            }
            Preconditions.checkNotNull(timeElement, "timeElement should not be null!");
            final LocalTime time = parseLocalTime(timeElement);
            logger.info("Found html row having time: " + time.toString());

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

            story.add(new StoryPart(LocalTime.now(), time, gameMinute, storyEvent, storyTitle, storyDescription));
        }

        return story;
    }

    @Override
    public void reload() throws ReloadPollingSourceException, TickerNotInSourceException, MatchNotStartedException {
        try {
            document = JsoupUtility.requestDocument(tickerUrl.getValue(), propertiesHolder);
            if(document.selectFirst(CSS_ROOT) == null){
                throw new TickerNotInSourceException(tickerUrl);
            }
            if(isGameNotStarted()){
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

    private String parseTextBlock(final Element element, final boolean title){
        final String html = element.html();
        if(html == null){
            return null;
        }
        final String[] parts = html.split("<br>");
        if(title){
            return reparseAndModifyHtmlTextBlock(parts[0]);
        }else{
            final String descriptionPart = Stream.of(parts).skip(1).collect(Collectors.joining("<br>"));
            return reparseAndModifyHtmlTextBlock(descriptionPart);
        }
    }

    private String reparseAndModifyHtmlTextBlock(final String html){
        if(html == null || html.trim().isEmpty()){
            return null;
        }
        return removeStatistics(Jsoup.parse(html)).text();
    }

    private Document removeStatistics(final Document document){
        final Element statisticsElement = document.selectFirst(CSS_STORY_STATISTICS);
        if(statisticsElement != null){
            statisticsElement.remove();
        }
        return document;
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
                case "ergtyp_1-l_v2.png":
                    return StoryEvent.GOAL;
                case "eig-tor-m.png":
                case "eig-tor-l_v2.png":
                    return StoryEvent.GOAL_OWN;
                case "ergtyp_2-m.png":
                case "ergtyp_2-l_v2.png":
                    return StoryEvent.YELLOW_CARD;
                case "ergtyp_3-m.png":
                case "ergtyp_3-l_v2.png":
                    return StoryEvent.YELLOW_RED_CARD;
                case "ergtyp_4-m.png":
                case "ergtyp_4-l_v2.png":
                    return StoryEvent.RED_CARD;
                case "ergtyp_5-m.png":
                case "ergtyp_5-l_v2.png":
                    return StoryEvent.EXCHANGE;
                case "ergtyp_30-m.png":
                case "ergtyp_30-l_v2.png":
                    return StoryEvent.PENALTY;
                case "icon-penaltygoal-24.png":
                    return StoryEvent.PENALTY_GOAL;
                case "ergtyp_7-m.png":
                case "ergtyp_7-l_v2.png":
                    return StoryEvent.PENALTY_FAILURE;
                case "ergtyp_70-m.png":
                case "ergtyp_71-m.png":
                case "ergtyp_70-l_v2.png":
                case "ergtyp_71-l_v2.png":
                    return StoryEvent.VIDEO_PROOF;
                default:
                    logger.warn(String.format("Unexpected imageFileName: %s", imageFileName));
                    return StoryEvent.DEFAULT;
            }
        }
    }

    private StoryPart parseHalfTimeStoryPart(final String halfTimeText){
        final String lowerHalfTimeText = halfTimeText.toLowerCase();
        //game end
        if(containsAllStrings(lowerHalfTimeText, GAME_END)){
            return createHalfTimeStoryPart(StoryEvent.GAME_END, halfTimeText);
        }

        //penalties end
        if(containsAllStrings(lowerHalfTimeText, HALF_TIME_END, PENALTIES)){
            return createHalfTimeStoryPart(StoryEvent.PENALTIES_START, halfTimeText);
        }

        //penalties start
        if(containsAllStrings(lowerHalfTimeText, HALF_TIME_START, PENALTIES)){
            return createHalfTimeStoryPart(StoryEvent.PENALTIES_START, halfTimeText);
        }

        //overtime b end
        if(containsAllStrings(lowerHalfTimeText, HALF_TIME_END, HALF_TIME_B, OVERTIME)){
            return createHalfTimeStoryPart(StoryEvent.OVERTIME_B_END, halfTimeText);
        }

        //overtime b start
        if(containsAllStrings(lowerHalfTimeText, HALF_TIME_START, HALF_TIME_B, OVERTIME)){
            return createHalfTimeStoryPart(StoryEvent.OVERTIME_B_START, halfTimeText);
        }
        //overtime a end
        if(containsAllStrings(lowerHalfTimeText, HALF_TIME_END, HALF_TIME_A, OVERTIME)){
            return createHalfTimeStoryPart(StoryEvent.OVERTIME_A_END, halfTimeText);
        }
        //overtime a start
        if(containsAllStrings(lowerHalfTimeText, HALF_TIME_START, HALF_TIME_A, OVERTIME)){
            return createHalfTimeStoryPart(StoryEvent.OVERTIME_A_START, halfTimeText);
        }

        //halftime b end
        if(containsAllStrings(lowerHalfTimeText, HALF_TIME_END, HALF_TIME_B)){
            return createHalfTimeStoryPart(StoryEvent.HALF_TIME_B_END, halfTimeText);
        }

        //halftime b start
        if(containsAllStrings(lowerHalfTimeText, HALF_TIME_START, HALF_TIME_B)){
            return createHalfTimeStoryPart(StoryEvent.HALF_TIME_B_START, halfTimeText);
        }

        //halftime a end
        if(containsAllStrings(lowerHalfTimeText, HALF_TIME_END, HALF_TIME_A)){
            return createHalfTimeStoryPart(StoryEvent.HALF_TIME_A_END, halfTimeText);
        }

        //halftime a start
        if(containsAllStrings(lowerHalfTimeText, HALF_TIME_START, HALF_TIME_A)){
            return createHalfTimeStoryPart(StoryEvent.HALF_TIME_A_START, halfTimeText);
        }

        throw new IllegalStateException(String.format("Unable to parse lowerHalfTimeText: '%s'!", lowerHalfTimeText));
    }

    private StoryPart createHalfTimeStoryPart(final StoryEvent storyEvent, final String halfTimeText){
        return new StoryPart(LocalTime.now(), null, null, storyEvent, StoryTitle.create(halfTimeText), null);
    }

    private boolean containsAllStrings(final String text, final String... containsTexts){
        Preconditions.checkNotNull(text, "text should not be null!");
        Preconditions.checkNotNull(containsTexts, "containsTexts should not be null!");
        Preconditions.checkArgument(containsTexts.length > 0, "containsTexts should not be empty!");
        for(String containsText : containsTexts){
            if(!text.contains(containsText)){
                return false;
            }
        }
        return true;
    }

    private void checkDocument(){
        Preconditions.checkNotNull(document, "document is null. Use reload() first to initialize it!");
    }

    private boolean isGameNotStarted(){
        if(document.selectFirst(CSS_GAME_NOT_STARTED) != null){
            return true;
        }
        final Element e = document.selectFirst(CSS_TEAM_A_SCORE_HALFTIME_A);
        if(e != null && e.text().equals("-")){
            return true;
        }
        return false;
    }

}
