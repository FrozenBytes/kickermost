package de.frozenbytes.kickermost.concurrent;

import com.google.common.base.Preconditions;
import de.frozenbytes.kickermost.concurrent.exchange.ExchangeStorage;
import de.frozenbytes.kickermost.dto.*;
import de.frozenbytes.kickermost.dto.property.TickerUrl;
import de.frozenbytes.kickermost.dto.property.basic.Property;
import de.frozenbytes.kickermost.exception.ReloadPollingSourceException;
import de.frozenbytes.kickermost.exception.TickerNotInSourceException;
import de.frozenbytes.kickermost.exception.UnexpectedThreadException;
import de.frozenbytes.kickermost.io.src.PollingSource;
import de.frozenbytes.kickermost.io.src.PollingSourceFactory;
import de.frozenbytes.kickermost.io.src.rss.RssParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

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
        tickerUrlList = new RssParser(RSS_URL).parse().stream()
                .map(Property::getValue)
                .map(TickerUrl::create)
                .collect(Collectors.toList());
    }

    public void start(){
        thread.start();
    }

    public boolean isAlive() {
        return thread.isAlive();
    }

    private void execute() {
        try {
            //first run
            if(sourceMap.isEmpty()){
                logger.info(String.format("# First run - found %d URLs in RSS feed '%s'.", tickerUrlList.size(), RSS_URL));
                for(TickerUrl url : tickerUrlList){
                    waitForTicker(url);
                    try {
                        sourceMap.put(url, PollingSourceFactory.create(url));
                    }catch (TickerNotInSourceException | ReloadPollingSourceException e){
                        logger.info(e.getMessage());
                    }
                }
            }
            Preconditions.checkState(!sourceMap.isEmpty(), "SourceMap is empty. No document could be parsed during the first run!");

            //interval run
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
                }catch (TickerNotInSourceException | ReloadPollingSourceException e){
                    logger.info(e.getMessage());
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
        return new Random().nextInt(501) + 50; //500-1000ms;
    }

    private Match createMatchFromSource(final PollingSource source){
        final Team teamA = new Team(source.getTeamAName(), source.getTeamAScore());
        final Team teamB = new Team(source.getTeamBName(), source.getTeamBScore());
        return new Match(teamA, teamB, source.getStory());
    }

    private void waitForGlobal() throws InterruptedException {
        final long globalInterval = getGlobalInterval();
        Thread.sleep(globalInterval);
        logger.info(String.format("# Global interval run [%d ms]", globalInterval));
    }

    private void waitForTicker(final TickerUrl tickerUrl) throws InterruptedException {
        final long tickerInterval = getTickerInterval();
        Thread.sleep(tickerInterval);
        logger.info(String.format("# Ticker interval run [%d ms]: '%s'", tickerInterval, tickerUrl));
    }

}
