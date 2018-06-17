package de.frozenbytes.kickermost.concurrent;

import com.google.common.base.Preconditions;
import de.frozenbytes.kickermost.concurrent.exchange.ExchangeStorage;
import de.frozenbytes.kickermost.conf.PropertiesHolder;
import de.frozenbytes.kickermost.dto.Match;
import de.frozenbytes.kickermost.dto.StoryPart;
import de.frozenbytes.kickermost.dto.Ticker;
import de.frozenbytes.kickermost.dto.property.StoryPartTimeLineComparator;
import de.frozenbytes.kickermost.dto.property.TickerUrl;
import de.frozenbytes.kickermost.dto.type.StoryEvent;
import de.frozenbytes.kickermost.http.MattermostWebhookClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PushingThread extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(PushingThread.class);

    private final TickerUrl tickerUrl;
    private final MattermostWebhookClient client;


    public PushingThread(final PropertiesHolder propertiesHolder, final TickerUrl tickerUrl) {
        super();
        Preconditions.checkNotNull(propertiesHolder, "propertiesHolder should not be null!");
        Preconditions.checkNotNull(tickerUrl, "tickerUrl should not be null!");
        this.tickerUrl = tickerUrl;
        this.client = new MattermostWebhookClient(propertiesHolder);
        this.setDaemon(true);
    }

    @Override
    public void run() {
        while (true){
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
                break;
            }
            final ExchangeStorage storage = ExchangeStorage.getInstance();
            final Ticker ticker = storage.getTickerByUrl(tickerUrl);

            final Match match = ticker.getMatch();
            List<StoryPart> messageParameters = match.getStory();
            List<StoryPart> sendMessages = new ArrayList<>();

            // Read send messages
            try(FileInputStream fis = new FileInputStream(convertTickerUrlToFileName(tickerUrl) + ".tmp")) {//
                ObjectInputStream ois = new ObjectInputStream(fis);
                sendMessages = (List<StoryPart>) ois.readObject();
            } catch (FileNotFoundException e){
                // that's normal for a new game
            } catch (IOException | ClassNotFoundException e) {
                logger.error(e.getMessage(), e);
                break;
            }

            messageParameters.removeAll(sendMessages);

            //  No new messages - Skip
            if(messageParameters.isEmpty()){
                continue;
            }

            // Send messages - ordered by GameTime
            messageParameters.sort(new StoryPartTimeLineComparator());
            for(StoryPart messageParameter : messageParameters){
                if(istAllowedEvent(messageParameter.getEvent())) {
                    // Send message if it is a game start/stop/end message OR
                    // the description is filled OR
                    // the message is at least 2 minutes old
                    if(isStartStopEvent(messageParameter.getEvent()) ||
                       messageParameter.getDescription() != null ||
                       messageParameter.getTime() != null && messageParameter.getTime().isBefore(LocalTime.now().minusMinutes(2))) {
                        client.postMessage(match, messageParameter);
                        sendMessages.add(messageParameter);
                    }
                }
            }

            // Save send Messages
            try(FileOutputStream fos = new FileOutputStream(convertTickerUrlToFileName(tickerUrl) + ".tmp")) {
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(sendMessages);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
                break;
            }

            //Game Over - terminate thread
            if(sendMessages.stream().anyMatch(storyPart -> storyPart.getEvent() == StoryEvent.GAME_END)){
                break;
            }
        }
    }

    private String convertTickerUrlToFileName(TickerUrl tickerUrl){
        return tickerUrl.toString().replace('/', '_')
                                   .replace(':', '_')
                                   .replace('\\', '_');
    }

    // TODO: Make configurable
    private boolean istAllowedEvent(StoryEvent event){
        return Arrays.asList(StoryEvent.getAllowedEvents()).contains(event);
    }

    private boolean isStartStopEvent(StoryEvent storyEvent){
        return Arrays.asList(StoryEvent.startStopEvents).contains(storyEvent);
    }
}
