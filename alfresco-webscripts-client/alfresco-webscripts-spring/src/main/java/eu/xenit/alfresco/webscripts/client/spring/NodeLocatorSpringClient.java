package eu.xenit.alfresco.webscripts.client.spring;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.xenit.alfresco.webscripts.client.spi.NodeLocatorClient;
import java.io.UncheckedIOException;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import lombok.Data;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpServerErrorException.InternalServerError;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import org.springframework.web.util.UriComponentsBuilder;

public class NodeLocatorSpringClient implements NodeLocatorClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public NodeLocatorSpringClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String get(String locatorName, Map<String, List<String>> params) {

        return this.execute(UriComponentsBuilder.fromPath("/api/nodelocator/")
                .path(locatorName)
                .queryParams(CollectionUtils.toMultiValueMap(params))
                .toUriString());

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

    private String addParamsToUrl(String url, Map<String, String> params) {
        StringBuilder urlBuilder = new StringBuilder(url);
        if (params.size() > 0) {
            urlBuilder.append("?");
            for (String key : params.keySet()) {
                urlBuilder.append(key).append("={").append(key).append("}").append("&");
            }
            urlBuilder.setLength(urlBuilder.length() - 1);
        }
        return urlBuilder.toString();
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
