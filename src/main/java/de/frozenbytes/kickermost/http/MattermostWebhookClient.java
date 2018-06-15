package de.frozenbytes.kickermost.http;

import de.frozenbytes.kickermost.PropertiesLoader;
import de.frozenbytes.kickermost.dto.Match;
import de.frozenbytes.kickermost.dto.StoryPart;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;

/**
 * Handles posting messages to mattermost via webhook integration
 */
public class MattermostWebhookClient {


    public MattermostWebhookClient() {
    }

    public void postMessage(final Match match, final StoryPart messageParameters) {
//        HttpHost proxy = new HttpHost("", 8080);
//        RequestConfig config = RequestConfig.custom()
//                                            .setProxy(proxy)
//                                            .build();

        HttpPost request = new HttpPost(PropertiesLoader.loadProperties().getProperty(PropertiesLoader.WEBHOOK_URL));
//        request.setConfig(config);
        request.addHeader("content-type", "application/json;charset=UTF-8");

        StringEntity params = new StringEntity(MattermostMessageBuilder.createJsonMessage(match, messageParameters), "UTF-8");
        request.setEntity(params);
        try (CloseableHttpClient httpClient = createAcceptSelfSignedCertificateClient()){
            HttpResponse response = httpClient.execute(request);
            System.out.println(response);
        } catch (IOException | NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
            e.printStackTrace();
        }
    }

    private static CloseableHttpClient createAcceptSelfSignedCertificateClient()
          throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {

        SSLContextBuilder builder = new SSLContextBuilder();
        builder.loadTrustMaterial(null, (chain, authType) -> true);
        SSLConnectionSocketFactory sslsf = new
              SSLConnectionSocketFactory(builder.build(), NoopHostnameVerifier.INSTANCE);
        return HttpClients.custom().setSSLSocketFactory(sslsf).build();
    }
}