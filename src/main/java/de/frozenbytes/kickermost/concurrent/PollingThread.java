package de.frozenbytes.kickermost.concurrent;

import com.google.common.base.Preconditions;
import de.frozenbytes.kickermost.concurrent.exchange.ExchangeStorage;
import de.frozenbytes.kickermost.dto.*;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public final class PollingThread {

    private static final Logger logger = LoggerFactory.getLogger(PollingThread.class);

    public static final String RSS_URL = "http://rss.kicker.de/live/wm";

    private static final int INTERVAL_MIN = 30000;
    private static final int INTERVAL_MAX = 90000;

    private final Thread thread;
    private final List<TickerUrl> tickerUrlList;

    private Map<TickerUrl, PollingSource> sourceMap = new HashMap<>();


    public PollingThread() throws IOException {
        super();
        thread = new Thread(this::execute);
        thread.setDaemon(true); // terminate this thread, if the superior user thread terminates
        tickerUrlList = PollingSourceFactory.parseRssFeed(RSS_URL);
    }

    public void start(){
        thread.start();
    }

    public boolean isAlive() {
        return thread.isAlive();
    }

    private void execute() {
        try {
            //initialize
            if(sourceMap.isEmpty()){
                logger.info(String.format("# Initializing - found %d URLs in RSS feed '%s'.", tickerUrlList.size(), RSS_URL));
                for(TickerUrl url : tickerUrlList){
                    logger.info(url.getValue());
                    sourceMap.put(url, PollingSourceFactory.create(url));
                }
            }
            Preconditions.checkState(!sourceMap.isEmpty(), "SourceMap is empty. No document could be parsed during the first run!");

            //interval run
            while(true){
                waitForGlobal();

                for(TickerUrl tickerUrl : sourceMap.keySet()){
                    waitForTicker(tickerUrl);

                    final PollingSource source = sourceMap.get(tickerUrl);
                    try{
                        source.reload();

                        final ExchangeStorage storage = ExchangeStorage.getInstance();

                        Ticker ticker;
                        if(storage.containsTickerWithUrl(tickerUrl)){
                            ticker = storage.getTickerByUrl(tickerUrl);
                        }else{
                            ticker = new Ticker(tickerUrl, createMatchFromSource(source));
                            storage.addTicker(ticker);
                        }

                        final Story prevStory = ticker.getMatch().getStory();
                        final Story currentStory = source.getStory();
                        for(StoryPart currentStoryPart : currentStory){
                            if(!prevStory.contains(currentStoryPart)){
                                prevStory.add(currentStoryPart); //update the previous story with the new content
                            }
                        }
                    }catch (TickerNotInSourceException | ReloadPollingSourceException | MatchNotStartedException e){
                        logger.info(e.getMessage());
                    }
                }
            }
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
            throw new UnexpectedThreadException(e);
        }
    }

    private long getGlobalInterval(){
        return new Random().nextInt(INTERVAL_MAX - INTERVAL_MIN) + INTERVAL_MIN;
    }

    private long getTickerInterval(){
        return new Random().nextInt(501) + 500; //500-1000ms;
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
        logger.info(String.format("# Ticker interval run [wait %d ms]: '%s'", tickerInterval, tickerUrl));
        Thread.sleep(tickerInterval);
    }

}
