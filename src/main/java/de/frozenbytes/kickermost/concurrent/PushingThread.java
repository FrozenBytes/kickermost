package de.frozenbytes.kickermost.concurrent;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import de.frozenbytes.kickermost.concurrent.exchange.ExchangeStorage;
import de.frozenbytes.kickermost.conf.PropertiesHolder;
import de.frozenbytes.kickermost.dto.Match;
import de.frozenbytes.kickermost.dto.StoryPart;
import de.frozenbytes.kickermost.dto.Ticker;
import de.frozenbytes.kickermost.exception.UnableToPostToMattermostException;
import de.frozenbytes.kickermost.exception.UnexpectedThreadException;
import de.frozenbytes.kickermost.util.comparator.StoryPartTimeLineComparator;
import de.frozenbytes.kickermost.dto.property.TickerUrl;
import de.frozenbytes.kickermost.dto.type.StoryEvent;
import de.frozenbytes.kickermost.io.target.http.MattermostWebhookClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
                throw new UnexpectedThreadException(e);
            }
            final ExchangeStorage storage = ExchangeStorage.getInstance();
            final Ticker ticker = storage.getTickerByUrl(tickerUrl);

            final Match match = ticker.getMatch();
            final ImmutableList<StoryPart> messageParameters = match.getStory();

            // Read send messages
            List<StoryPart> sendMessages = new ArrayList<>();
            try(FileInputStream fis = new FileInputStream(convertTickerUrlToFileName(tickerUrl) + ".tmp")) {//
                ObjectInputStream ois = new ObjectInputStream(fis);
                sendMessages = (List<StoryPart>) ois.readObject();
            } catch (FileNotFoundException e){
                // that's normal for a new game
            } catch (IOException | ClassNotFoundException e) {
                logger.error(e.getMessage(), e);
                break;
            }

            //set sentToMattermostFlag by remembered storyParts
            for(StoryPart fileStoryPart : sendMessages){
                int index = messageParameters.indexOf(fileStoryPart);
                if(index == -1){
                    continue;
                }
                final StoryPart messageParameter = messageParameters.get(index);
                if(messageParameter != null){
                    messageParameter.setSentToMattermost(true);
                }
            }

            final List<StoryPart> partsToSendList = messageParameters.stream().filter(m -> !m.isSentToMattermost()).collect(Collectors.toList());
            //  No new messages - Skip
            if(partsToSendList.isEmpty()){
                continue;
            }

            // Send messages - ordered by GameTime
            partsToSendList.sort(new StoryPartTimeLineComparator());
            for(StoryPart partToSend : partsToSendList){
                if(isAllowedEvent(partToSend.getEvent())) {
                    // Send message if it is a game start/stop/end message OR
                    // the description is filled OR
                    // the message is at least 2 minutes old
                    if(isStartStopEvent(partToSend.getEvent()) ||
                       partToSend.getDescription() != null ||
                       partToSend.getTime() != null && partToSend.getTime().isBefore(LocalTime.now().minusMinutes(3))) {
                        try {
                            client.postMessage(match, partToSend);
                            partToSend.setSentToMattermost(true);
                        } catch (UnableToPostToMattermostException e) {
                            logger.warn(e.getMessage(), e); //handle and survive network issues
                            break;
                        }
                    }
                }
            }

            // Save send Messages
            try(FileOutputStream fos = new FileOutputStream(convertTickerUrlToFileName(tickerUrl) + ".tmp")) {
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(messageParameters.stream().filter(StoryPart::isSentToMattermost).collect(Collectors.toList()));
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
    private boolean isAllowedEvent(StoryEvent event){
        return Arrays.asList(StoryEvent.getAllowedEvents()).contains(event);
    }

    private boolean isStartStopEvent(StoryEvent storyEvent){
        return Arrays.asList(StoryEvent.startStopEvents).contains(storyEvent);
    }
}
