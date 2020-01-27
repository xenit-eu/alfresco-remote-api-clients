package eu.xenit.alfresco.webscripts.client.spring;

import eu.xenit.alfresco.webscripts.client.spi.ApiMetadataClient;
import java.util.Collections;
import java.util.Map;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

public class ApiMetadataSpringClient implements ApiMetadataClient {

    private final RestTemplate restClient;

    public ApiMetadataSpringClient(RestTemplateBuilder restTemplateBuilder) {

//        this.restTemplateBuilder = restTemplateBuilder;
        this.restClient = restTemplateBuilder.build();
    }

    @Override
    public Metadata get(String nodeRef) {

        Map<String, String> params = Collections.singletonMap("nodeRef", nodeRef);

        return this.restClient.getForObject("/api/metadata?nodeRef={nodeRef}", Metadata.class, params);
    }
}
