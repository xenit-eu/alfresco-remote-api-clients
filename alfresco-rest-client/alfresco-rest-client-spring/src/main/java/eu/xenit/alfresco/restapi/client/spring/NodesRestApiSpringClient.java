package eu.xenit.alfresco.restapi.client.spring;

import eu.xenit.alfresco.restapi.client.spi.NodesRestApiClient;
import eu.xenit.alfresco.restapi.client.spi.model.NodeEntry;
import eu.xenit.alfresco.restapi.client.spi.query.DeleteNodeQueryParameters;
import eu.xenit.alfresco.restapi.client.spi.query.GetNodeQueryParameters;
import java.net.URI;
import org.springframework.web.util.UriComponentsBuilder;

public class NodesRestApiSpringClient extends RestApiSpringClient implements NodesRestApiClient {

    public NodesRestApiSpringClient(AlfrescoRestProperties alfrescoRestProperties) {
        super(alfrescoRestProperties);
    }

    @Override
    protected String getApiBasePath() {
        return "/nodes";
    }

    @Override
    public void delete(String nodeId, DeleteNodeQueryParameters queryParameters) {
        URI uri = withQueryParameters(UriComponentsBuilder
                .fromHttpUrl(url).path("/" + nodeId), queryParameters)
                .build().toUri();

        restTemplate.delete(uri);
    }

    @Override
    public NodeEntry getNodeEntry(String nodeId, GetNodeQueryParameters queryParameters) {
        URI uri = withQueryParameters(UriComponentsBuilder
                .fromHttpUrl(url).path("/" + nodeId), queryParameters)
                .build().toUri();

        return restTemplate.getForObject(uri, NodeEntry.class);
    }
}
