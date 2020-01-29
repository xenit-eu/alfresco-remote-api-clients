package eu.xenit.alfresco.webscripts.client.spring;

import eu.xenit.alfresco.webscripts.client.spi.ApiMetadataClient;
import java.util.Collections;
import java.util.Map;

import eu.xenit.alfresco.webscripts.client.spi.Metadata;
import org.springframework.web.client.RestTemplate;

public class ApiMetadataSpringClient implements ApiMetadataClient {

    private final RestTemplate restClient;

    public ApiMetadataSpringClient(RestTemplate restTemplate) {
        this.restClient = restTemplate;
    }

    @Override
    public Metadata get(String nodeRef) {

        Map<String, String> params = Collections.singletonMap("nodeRef", nodeRef);

        return this.restClient.getForObject("/api/metadata?nodeRef={nodeRef}", Metadata.class, params);
    }
}
