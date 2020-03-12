package eu.xenit.alfresco.webscripts.client.spring;

import eu.xenit.alfresco.webscripts.client.spi.ApiNodeContentClient;
import eu.xenit.alfresco.webscripts.client.spring.http.RestTemplateHelper;
import java.io.OutputStream;
import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException.NotFound;
import org.springframework.web.client.HttpServerErrorException.InternalServerError;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class ApiNodeContentSpringClient implements ApiNodeContentClient {

    private static final Logger logger = LoggerFactory.getLogger(ApiNodeContentSpringClient.class);

    private final RestTemplate restClient;
    private final String url;

    public ApiNodeContentSpringClient(AlfrescoProperties alfrescoProperties) {
        this(alfrescoProperties, RestTemplateHelper.buildRestTemplate(alfrescoProperties));
    }

    public ApiNodeContentSpringClient(AlfrescoProperties alfrescoProperties, RestTemplate restTemplate) {
        this.url = UriComponentsBuilder.fromHttpUrl(alfrescoProperties.getUrl())
                .path("/service/api/node/content")
                .toUriString();

        this.restClient = restTemplate;
    }

    @Override
    public boolean hasContent(String nodeRef) {
        URI uri = UriComponentsBuilder.fromHttpUrl(url).path("/" + convertToUrlSegment(nodeRef)).build().toUri();
        try {
            restClient.headForHeaders(uri);
        } catch (NotFound | InternalServerError e) {
            logger.debug("HEAD ../api/node/content '{}' response: '{}'", nodeRef, e.getStatusCode().toString());
            return false;
        } catch (Exception e) {
            logger.error("HEAD ../api/node/content '{}' unexpected error", nodeRef, e);
            return false;
        }

        return true;
    }

    @Override
    public void getContent(String nodeRef, String contentPropertyShortQName, OutputStream outputStream) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url);
        if (StringUtils.hasText(contentPropertyShortQName)) {
            if (contentPropertyShortQName.contains("{")) {
                throw new IllegalArgumentException("Expected short QName but got: '" + contentPropertyShortQName + "'");
            }
            uriBuilder.path(";" + contentPropertyShortQName);
        }
        uriBuilder.path("/" + convertToUrlSegment(nodeRef));

        restClient.execute(uriBuilder.build().toUri(), HttpMethod.GET, null,
                httpResponse -> {
                    StreamUtils.copy(httpResponse.getBody(), outputStream);
                    return null;
                });
    }

    private static String convertToUrlSegment(String nodeRef) {
        return nodeRef.replace("://", "/");
    }
}
