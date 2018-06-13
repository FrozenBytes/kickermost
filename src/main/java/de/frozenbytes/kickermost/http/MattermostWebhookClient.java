package de.frozenbytes.kickermost.http;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Handles posting messages to mattermost via webhook integration
 */
public class MattermostWebhookClient {

    private final HttpClient httpClient;
    // TODO: via config file
    private final String webhookUrl = "";

    public MattermostWebhookClient() {
        httpClient = HttpClientBuilder.create().build();
    }

    public void postMessage(){
        HttpPost request = new HttpPost(webhookUrl);
        request.addHeader("content-type", "application/json;charset=UTF-8");

        StringEntity params = new StringEntity(MattermostMessageBuilder.createJsonMessage(), "UTF-8");
        request.setEntity(params);
        try {
            HttpResponse response = httpClient.execute(request);
            System.out.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}