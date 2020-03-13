package eu.xenit.alfresco.webscripts.client.spring;

import eu.xenit.alfresco.webscripts.client.spi.ApiMetadataClient;
import eu.xenit.alfresco.webscripts.client.spring.http.RestTemplateHelper;
import eu.xenit.alfresco.webscripts.client.spring.model.AlfrescoProperties;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class ApiMetadataSpringClient implements ApiMetadataClient {

    private final RestTemplate restClient;
    private final String url;

    public ApiMetadataSpringClient(AlfrescoProperties alfrescoProperties) {
        this(alfrescoProperties, RestTemplateHelper.buildRestTemplate(alfrescoProperties));
    }

    public ApiMetadataSpringClient(AlfrescoProperties alfrescoProperties, RestTemplate restTemplate) {
        this.url = UriComponentsBuilder.fromHttpUrl(alfrescoProperties.getUrl())
                .path("/service/api")
                .toUriString();

        this.restClient = restTemplate;
    }

    @Override
    public Metadata get(String nodeRef) {
        URI uri = UriComponentsBuilder
                .fromHttpUrl(url).path("/metadata")
                .queryParam("nodeRef", nodeRef)
                .build().toUri();

        return this.restClient.getForObject(uri, Metadata.class);
    }

    @Override
    public List<BulkMetadata> get(List<String> nodeRefs) {
        if (nodeRefs == null || nodeRefs.isEmpty()) {
            return Collections.emptyList();
        }

        URI uri = UriComponentsBuilder.fromHttpUrl(url).path("/bulkmetadata").build().toUri();

        BulkMetadataResponse response = this.restClient
                .postForObject(uri, new BulkMetadataBody(nodeRefs), BulkMetadataResponse.class);

        if (response == null) {
            throw new IllegalStateException("bulkmetadata response is null");
        }
        return response.getNodes();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class BulkMetadataBody {

        List<String> nodeRefs;
    }

    @Data
    private static class BulkMetadataResponse {

        List<BulkMetadata> nodes;
    }
}
