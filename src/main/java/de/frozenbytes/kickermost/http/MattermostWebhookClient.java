package de.frozenbytes.kickermost.http;

import de.frozenbytes.kickermost.PropertiesLoader;
import de.frozenbytes.kickermost.dto.Match;
import de.frozenbytes.kickermost.dto.StoryPart;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;

/**
 * Handles posting messages to mattermost via webhook integration
 */
public class MattermostWebhookClient {

    private final HttpClient httpClient;

    public MattermostWebhookClient() {
        httpClient = HttpClientBuilder.create().build();
    }

    public void postMessage(final Match match, final StoryPart messageParameters){
        HttpPost request = new HttpPost(PropertiesLoader.loadProperties().getProperty(PropertiesLoader.WEBHOOK_URL));
        request.addHeader("content-type", "application/json;charset=UTF-8");

        StringEntity params = new StringEntity(MattermostMessageBuilder.createJsonMessage(match, messageParameters), "UTF-8");
        request.setEntity(params);
        try {
            HttpResponse response = httpClient.execute(request);
            System.out.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}