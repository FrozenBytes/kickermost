package de.frozenbytes.kickermost;

import de.frozenbytes.kickermost.concurrent.PollingThread;
import de.frozenbytes.kickermost.conf.PropertiesHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class Kickermost {

    private static final Logger logger = LoggerFactory.getLogger(Kickermost.class);

    public static void main(String[] args) {
        logger.info("START");
        try{
            final PropertiesHolder propertiesHolder = PropertiesLoader.createPropertiesHolder();

//            MattermostWebhookClient client = new MattermostWebhookClient();
//            client.postMessage();
//            new Kicker().get();

            final PollingThread pollingThread = new PollingThread(propertiesHolder);
            pollingThread.start();

            while (pollingThread.isAlive()){
                Thread.sleep(500);
                //System.out.print("Polling thread: " + (pollingThread.isAlive() ? "alive" : "dead") + "\r");
                //System.out.flush();
            }
        }catch (Exception e){
            logger.error(e.getMessage(), e);
        }finally {
            logger.info("TERMINATED");
        }
    }

}
