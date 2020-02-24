package eu.xenit.alfresco.webscripts.client.spring;

import eu.xenit.alfresco.webscripts.client.spi.ApiMetadataClient;
import java.net.URI;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class ApiMetadataSpringClient implements ApiMetadataClient {

    private final RestTemplate restClient;
    private final String url;

    public ApiMetadataSpringClient(AlfrescoProperties alfrescoProperties, RestTemplate restTemplate) {
        this.url = UriComponentsBuilder.fromHttpUrl(alfrescoProperties.getUrl())
                .path("/service/api/metadata")
                .toUriString();

        this.restClient = restTemplate;
    }

    @Override
    public Metadata get(String nodeRef) {
        URI uri = UriComponentsBuilder
                .fromHttpUrl(url)
                .queryParam("nodeRef", nodeRef)
                .build().toUri();

        return this.restClient.getForObject(uri, Metadata.class);
    }
}
