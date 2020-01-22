package eu.xenit.alfresco.webscriptsapi.client.spring;

import eu.xenit.alfresco.webscriptsapi.client.spi.AlfrescoWebscriptsApiClient;
import eu.xenit.alfresco.webscriptsapi.client.spi.dto.Metadata;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

@Slf4j
public class AlfrescoWebscriptsApiClientImpl implements AlfrescoWebscriptsApiClient {

    private final RestTemplate restTemplate;
    private final String url;

    public AlfrescoWebscriptsApiClientImpl(String url, String username, String password) throws URISyntaxException {
        this(url, new HttpBasicAuthRequestFactory(
                new HttpHost(
                        new URI(url)
                                        .getHost())));
        this.restTemplate.getInterceptors()
                .add(new BasicAuthenticationInterceptor(username, password));
    }

    public AlfrescoWebscriptsApiClientImpl(String url, ClientHttpRequestFactory requestFactory) {
        this(url, new RestTemplate(requestFactory));
    }

    public AlfrescoWebscriptsApiClientImpl(String url, RestTemplate restTemplate) {
        this.url =UriComponentsBuilder.fromHttpUrl(url)
                .path("/service/api")
                .toUriString();
        this.restTemplate = restTemplate;
        this.restTemplate.getInterceptors()
                .add(new LogAsCurlRequestsInterceptor());
    }

    @Override
    public Metadata getMetadata(String nodeRef) {
        log.debug("getMetadata for " + nodeRef);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url).path("/metadata");
        if(Objects.nonNull(nodeRef)) {
            uriBuilder.queryParam("nodeRef", nodeRef);
        }
        return restTemplate.getForObject(uriBuilder.toUriString(), Metadata.class);
    }
}
