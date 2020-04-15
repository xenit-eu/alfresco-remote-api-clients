package eu.xenit.alfresco.restapi.client.spring;

import eu.xenit.alfresco.restapi.client.spi.VersionsRestApiClient;
import eu.xenit.alfresco.restapi.client.spi.model.NodeEntry;
import eu.xenit.alfresco.restapi.client.spi.model.NodeList;
import eu.xenit.alfresco.restapi.client.spi.query.NodeQueryParameters;
import eu.xenit.alfresco.restapi.client.spi.query.PaginationQueryParameters;
import eu.xenit.alfresco.restapi.client.spi.query.QueryParameters;
import java.net.URI;
import org.springframework.http.RequestEntity;
import org.springframework.util.StringUtils;

public class VersionsRestApiSpringClient extends RestApiSpringClient implements VersionsRestApiClient {

    public VersionsRestApiSpringClient(AlfrescoRestProperties alfrescoRestProperties) {
        super(alfrescoRestProperties);
    }

    @Override
    protected String getApiBasePath() {
        return "/nodes";
    }

    @Override
    public NodeList getVersions(String nodeId, PaginationQueryParameters paginationQueryParameters,
            NodeQueryParameters<?> nodeQueryParameters) {
        URI uri = versions(nodeId, paginationQueryParameters, nodeQueryParameters);

        RequestEntity<Void> requestEntity = RequestEntity.get(uri).build();
        return execute(nodeId, requestEntity, NodeList.class);
    }

    @Override
    public NodeEntry getVersion(String nodeId, String versionId) {
        URI uri = versions(nodeId, versionId);

        RequestEntity<Void> requestEntity = RequestEntity.get(uri).build();
        return execute(nodeId, requestEntity, NodeEntry.class);
    }

    private URI versions(String nodeId, QueryParameters... queryParameters) {
        return versions(nodeId, null, queryParameters);
    }

    private URI versions(String nodeId, String versionId, QueryParameters... queryParameters) {
        final String path = StringUtils.hasText(versionId) ?
                "/versions/" + versionId : "/versions";
        return uri(nodeId, path, queryParameters);
    }
}
