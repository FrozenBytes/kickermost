package de.frozenbytes.kickermost;

import de.frozenbytes.kickermost.concurrent.PollingThread;
import de.frozenbytes.kickermost.concurrent.PushingThread;
import de.frozenbytes.kickermost.concurrent.exchange.ExchangeStorage;
import de.frozenbytes.kickermost.conf.PropertiesHolder;
import de.frozenbytes.kickermost.conf.PropertiesLoader;
import de.frozenbytes.kickermost.dto.Ticker;
import de.frozenbytes.kickermost.dto.property.TickerUrl;
import de.frozenbytes.kickermost.util.GameActiveDecider;
import de.frozenbytes.kickermost.util.arg.ArgumentResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Kickermost {

    private static final Logger logger = LoggerFactory.getLogger(Kickermost.class);

    private static final Map<TickerUrl, PushingThread> activePushingThreads = new HashMap<>();

    /**
     *
     * @param args
     *  -config src/main/resources/de/frozenbytes/kickermost/conf/config.properties
     */
    public static void main(String[] args) {
        logger.info("START");
        try{
            final PropertiesHolder propertiesHolder = PropertiesLoader.createPropertiesHolder(ArgumentResolver.resolveConfigFilePath(args));

            final PollingThread pollingThread = new PollingThread(propertiesHolder);
            pollingThread.start();

            while (pollingThread.isAlive()){
                ExchangeStorage storage = ExchangeStorage.getInstance();
                List<Ticker> activeTickers = getActiveTickers(storage);

                for(Ticker ticker : activeTickers){
                    if(!activePushingThreads.containsKey(ticker.getTickerUrl())){
                        logger.info("Start Pushing thread for " + ticker.getTickerUrl());
                        PushingThread pushingThread = new PushingThread(propertiesHolder, ticker.getTickerUrl());
                        activePushingThreads.put(ticker.getTickerUrl(), pushingThread);
                        pushingThread.start();
                    }
                }

                activePushingThreads.entrySet().removeIf(activePushingThreadEntry -> !activePushingThreadEntry.getValue().isAlive());

                //check for new Ticker every minute
                Thread.sleep(60000);
            }
        }catch (Exception e){
            logger.error(e.getMessage(), e);
        }finally {
            logger.info("TERMINATED");
        }
    }

    private static List<Ticker> getActiveTickers(ExchangeStorage storage) {
        Iterator<Ticker> tickerIterator = storage.getTickerList().iterator();

        List<Ticker> activeTicker = new ArrayList<>();
        while (tickerIterator.hasNext()) {
            Ticker ticker = tickerIterator.next();
            if (new GameActiveDecider(ticker.getMatch().getStory()).isGameActive()) {
                activeTicker.add(ticker);
            }
        }
        return activeTicker;
    }

}
