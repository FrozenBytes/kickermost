package de.frozenbytes.kickermost;

import de.frozenbytes.kickermost.concurrent.PollingThread;
import de.frozenbytes.kickermost.concurrent.PushingThread;
import de.frozenbytes.kickermost.concurrent.exchange.ExchangeStorage;
import de.frozenbytes.kickermost.conf.PropertiesHolder;
import de.frozenbytes.kickermost.conf.PropertiesLoader;
import de.frozenbytes.kickermost.dto.Ticker;
import de.frozenbytes.kickermost.dto.property.TickerUrl;
import de.frozenbytes.kickermost.dto.type.StoryEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Kickermost {

    private static final Logger logger = LoggerFactory.getLogger(Kickermost.class);

    private static final Map<TickerUrl, PushingThread> activePushingThreads = new HashMap<>();

    public static void main(String[] args) {
        logger.info("START");
        try{
            final PropertiesHolder propertiesHolder = PropertiesLoader.createPropertiesHolder();

            final PollingThread pollingThread = new PollingThread(propertiesHolder);
            pollingThread.start();

            while (pollingThread.isAlive()){
                Thread.sleep(500);

                ExchangeStorage storage = ExchangeStorage.getInstance();
                List<Ticker> activeTickers = getActiveTickers(storage);

                for(Ticker ticker : activeTickers){
                    if(!activePushingThreads.containsKey(ticker.getTickerUrl())){
                        PushingThread pushingThread = new PushingThread(propertiesHolder, ticker.getTickerUrl());
                        activePushingThreads.put(ticker.getTickerUrl(), pushingThread);
                        pushingThread.start();
                    }
                }

                for(Map.Entry<TickerUrl, PushingThread> entry : activePushingThreads.entrySet()){
                    if(!entry.getValue().isAlive()){
                        activePushingThreads.remove(entry.getKey());
                    }
                }
            }
        }catch (Exception e){
            logger.error(e.getMessage(), e);
        }finally {
            logger.info("TERMINATED");
        }
    }

    private static List<Ticker> getActiveTickers(ExchangeStorage storage) {
        return storage.getTickerList().stream()
                .filter(ticker -> ticker.getMatch().getStory().stream()
                        .anyMatch(storyPart -> storyPart.getEvent() == StoryEvent.KICKOFF))
                .filter(ticker -> ticker.getMatch().getStory().stream()
                        .noneMatch(storyPart -> storyPart.getEvent() == StoryEvent.FINAL_WHISTLE))
                .collect(Collectors.toList());
    }

}
