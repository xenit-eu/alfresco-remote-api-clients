package eu.xenit.alfresco.webscripts.client.spring;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.xenit.alfresco.webscripts.client.spi.NodeLocatorClient;
import eu.xenit.alfresco.webscripts.client.spring.http.RestTemplateHelper;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.Data;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpServerErrorException.InternalServerError;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public class NodeLocatorSpringClient implements NodeLocatorClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String url;

    public NodeLocatorSpringClient(AlfrescoProperties alfrescoProperties) {
        this(alfrescoProperties, RestTemplateHelper.buildRestTemplate(alfrescoProperties));
    }

    public NodeLocatorSpringClient(AlfrescoProperties alfrescoProperties, RestTemplate restTemplate) {
        this.url = UriComponentsBuilder.fromHttpUrl(alfrescoProperties.getUrl())
                .path("/service/api/nodelocator")
                .toUriString();
        this.restTemplate = restTemplate;
    }

    @Override
    public String get(String locatorName, Map<String, List<String>> params) {

        UriComponents uri = UriComponentsBuilder.fromHttpUrl(url)
                .path("/" + locatorName)
                .queryParams(CollectionUtils.toMultiValueMap(params))
                .build();

        return this.execute(uri.toUriString());

    }

    private String execute(String uriPath) {
        Objects.requireNonNull(uriPath, "Argument 'uri' is required");

        try {
            NodeLocatorResponse nodeRefData = this.restTemplate.getForObject(uriPath, NodeLocatorResponse.class);
            if (nodeRefData == null || nodeRefData.getData() == null) {
                return null;
            }
            return nodeRefData.getData().getNodeRef();
        } catch (InternalServerError http500) {
            try {
                WebscriptHttpErrorResponse response = this.objectMapper
                        .readValue(http500.getResponseBodyAsString(), WebscriptHttpErrorResponse.class);

                throw new RuntimeException(response.getMessage(), http500);

            } catch (JsonProcessingException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    @Data
    public static class NodeLocatorResponse {

        private NodeRefData data;

        @Data
        public static class NodeRefData {

            private String nodeRef;
        }
    }


}
