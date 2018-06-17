package de.frozenbytes.kickermost.concurrent;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import de.frozenbytes.kickermost.concurrent.exchange.ExchangeStorage;
import de.frozenbytes.kickermost.conf.PropertiesHolder;
import de.frozenbytes.kickermost.dto.Match;
import de.frozenbytes.kickermost.dto.Story;
import de.frozenbytes.kickermost.dto.StoryPart;
import de.frozenbytes.kickermost.dto.Team;
import de.frozenbytes.kickermost.dto.Ticker;
import de.frozenbytes.kickermost.dto.property.TickerUrl;
import de.frozenbytes.kickermost.exception.MatchNotStartedException;
import de.frozenbytes.kickermost.exception.ReloadPollingSourceException;
import de.frozenbytes.kickermost.exception.TickerNotInSourceException;
import de.frozenbytes.kickermost.exception.UnexpectedThreadException;
import de.frozenbytes.kickermost.io.src.PollingSource;
import de.frozenbytes.kickermost.io.src.PollingSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public final class PollingThread {

    private static final Logger logger = LoggerFactory.getLogger(PollingThread.class);
    private static final int HOUR_IN_MS = 3600 * 1000;

    private final PropertiesHolder propertiesHolder;
    private final Thread thread;

    private Map<TickerUrl, PollingSource> rssSourceMap = new HashMap<>();

    private boolean firstPoll = true;


    public PollingThread(final PropertiesHolder propertiesHolder) {
        super();
        Preconditions.checkNotNull(propertiesHolder, "propertiesHolder should not be null!");
        this.propertiesHolder = propertiesHolder;
        this.thread = new Thread(this::execute);
        this.thread.setDaemon(true); // terminate this thread, if the superior user thread terminates
    }

    public void start(){
        thread.start();
    }

    public boolean isAlive() {
        return thread.isAlive();
    }

    private void execute() {
        try {
            Long rssLastRefreshedMs = null;
            while(true){
                //force the reload of the rss feed once an hour
                if(rssLastRefreshedMs == null || getCurrentMilliseconds() > (rssLastRefreshedMs + HOUR_IN_MS)){
                    rssLastRefreshedMs = getCurrentMilliseconds();
                    rssSourceMap.clear();
                }
                //reload rss feed, if necessary
                if(rssSourceMap.isEmpty()){
                    final List<TickerUrl> tickerUrlList = PollingSourceFactory.parseRssFeed(propertiesHolder.getPollingRssFeedUrl(), propertiesHolder.getPollingFssFeedUrlContains());
                    logger.info(String.format("# Initializing - found %d URLs in RSS feed '%s'.", tickerUrlList.size(), propertiesHolder.getPollingRssFeedUrl()));
                    for(TickerUrl url : tickerUrlList){
                        logger.info(url.getValue());
                        rssSourceMap.put(url, PollingSourceFactory.create(url));
                    }
                    if(rssSourceMap.isEmpty()){
                        logger.warn("RssSourceMap is empty. No document could be parsed during the first run!");
                    }
                }

                //interval run
                waitForGlobal();

                for(TickerUrl tickerUrl : rssSourceMap.keySet()){
                    waitForTicker(tickerUrl);

                    final PollingSource source = rssSourceMap.get(tickerUrl);
                    try{
                        source.reload();

                        final ExchangeStorage storage = ExchangeStorage.getInstance();

                        Ticker ticker;
                        if(storage.containsTickerWithUrl(tickerUrl)){
                            ticker = storage.getTickerByUrl(tickerUrl);
                            ticker.getMatch().getTeamA().setScore(source.getTeamAScore());
                            ticker.getMatch().getTeamB().setScore(source.getTeamBScore());
                        }else{
                            ticker = new Ticker(tickerUrl, createMatchFromSource(source));
                            storage.addTicker(ticker);
                        }

                        final Match match = ticker.getMatch();
                        final ImmutableList<StoryPart> prevStoryPartList = match.getStory();
                        final Story currentStory = source.getStory();
                        for(StoryPart currentStoryPart : currentStory){
                            if(!prevStoryPartList.contains(currentStoryPart)){
                                match.addStoryPart(currentStoryPart); //update the previous story with the new content
                            }
                        }
                    }catch (TickerNotInSourceException | ReloadPollingSourceException | MatchNotStartedException e){
                        logger.info(e.getMessage());
                    }
                }
            }
        } catch (InterruptedException | IOException e) {
            logger.error(e.getMessage(), e);
            throw new UnexpectedThreadException(e);
        }
    }

    private long getGlobalInterval(){
        if (firstPoll) {
            firstPoll = false;
            return 0;
        }
        return new Random().nextInt(propertiesHolder.getPollingGlobalIntervalMax() - propertiesHolder.getPollingGlobalIntervalMin()) + propertiesHolder.getPollingGlobalIntervalMin();
    }

    private long getTickerInterval(){
        return new Random().nextInt(propertiesHolder.getPollingTickerIntervalMax() - propertiesHolder.getPollingTickerIntervalMin()) + propertiesHolder.getPollingTickerIntervalMin();
    }

    private Match createMatchFromSource(final PollingSource source){
        final Team teamA = new Team(source.getTeamAName(), source.getTeamAScore());
        final Team teamB = new Team(source.getTeamBName(), source.getTeamBScore());
        return new Match(teamA, teamB, source.getStory());
    }

    private void waitForGlobal() throws InterruptedException {
        final long globalInterval = getGlobalInterval();
        logger.info(String.format("# Global interval run [wait %d ms]", globalInterval));
        Thread.sleep(globalInterval);
    }

    private void waitForTicker(final TickerUrl tickerUrl) throws InterruptedException {
        final long tickerInterval = getTickerInterval();
        logger.info(String.format("## Ticker interval run [wait %d ms]: '%s'", tickerInterval, tickerUrl));
        Thread.sleep(tickerInterval);
    }

    private long getCurrentMilliseconds(){
        return new Date().getTime();
    }

}
