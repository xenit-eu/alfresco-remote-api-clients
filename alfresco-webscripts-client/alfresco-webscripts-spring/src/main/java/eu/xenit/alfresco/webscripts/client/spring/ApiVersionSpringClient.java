package eu.xenit.alfresco.webscripts.client.spring;

import eu.xenit.alfresco.webscripts.client.spi.ApiVersionClient;
import eu.xenit.alfresco.webscripts.client.spring.http.RestTemplateHelper;
import eu.xenit.alfresco.webscripts.client.spring.model.AlfrescoProperties;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class ApiVersionSpringClient implements ApiVersionClient {

    private final RestTemplate restClient;
    private final String url;

    public ApiVersionSpringClient(AlfrescoProperties alfrescoProperties) {
        this(alfrescoProperties, RestTemplateHelper.buildRestTemplate(alfrescoProperties));
    }

    public ApiVersionSpringClient(AlfrescoProperties alfrescoProperties, RestTemplate restTemplate) {
        this.url = UriComponentsBuilder.fromHttpUrl(alfrescoProperties.getUrl())
                .path("/service/api")
                .toUriString();

        this.restClient = restTemplate;
    }

    @Override
    public List<Version> getVersions(String nodeRef) {
        URI uri = UriComponentsBuilder
                .fromHttpUrl(url).path("/version")
                .queryParam("nodeRef", nodeRef)
                .build().toUri();

        Version[] response = this.restClient.getForObject(uri, Version[].class);

        if (response == null) {
            throw new IllegalStateException("version response is null");
        }

        return Arrays.asList(response);
    }
}
