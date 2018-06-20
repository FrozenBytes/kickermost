package de.frozenbytes.kickermost.io.target.http;

import com.google.common.base.Preconditions;
import de.frozenbytes.kickermost.conf.PropertiesHolder;
import de.frozenbytes.kickermost.dto.Match;
import de.frozenbytes.kickermost.dto.StoryPart;
import de.frozenbytes.kickermost.exception.UnableToPostToMattermostException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

/**
 * Handles posting messages to mattermost via webhook integration
 */
public final class MattermostWebhookClient {

    private static final Logger logger = LoggerFactory.getLogger(MattermostWebhookClient.class);

    private PropertiesHolder propertiesHolder;

    public MattermostWebhookClient(PropertiesHolder propertiesHolder) {
        setPropertiesHolder(propertiesHolder);
    }

    public void postMessage(final Match match, final StoryPart messageParameters) throws UnableToPostToMattermostException {
        Preconditions.checkNotNull(match, "match should not be null");
        Preconditions.checkNotNull(messageParameters, "messageParameters should not be null");

        final String mattermostUrl = propertiesHolder.getMattermostWebhookUrl();
        final HttpPost request = new HttpPost(mattermostUrl);
        request.addHeader("content-type", "application/json;charset=UTF-8");

        StringEntity params = new StringEntity(MattermostMessageBuilder.createJsonMessage(match, messageParameters, propertiesHolder), "UTF-8");
        request.setEntity(params);
        try (CloseableHttpClient httpClient = createAcceptSelfSignedCertificateClient()) {
            HttpResponse response = httpClient.execute(request);
            logger.info(response.toString());
        }catch (IOException e){
            logger.warn(e.getMessage(), e);
            throw new UnableToPostToMattermostException(mattermostUrl, e);
        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public void setPropertiesHolder(final PropertiesHolder propertiesHolder){
        Preconditions.checkNotNull(propertiesHolder, "propertiesHolder should not be null!");
        this.propertiesHolder = propertiesHolder;
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