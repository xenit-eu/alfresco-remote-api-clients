package eu.xenit.alfresco.restapi.client.spi;

import eu.xenit.alfresco.restapi.client.spi.model.NodeEntry;
import eu.xenit.alfresco.restapi.client.spi.model.NodeList;
import eu.xenit.alfresco.restapi.client.spi.query.NodeQueryParameters;
import eu.xenit.alfresco.restapi.client.spi.query.PaginationQueryParameters;

/**
 * see: https://api-explorer.alfresco.com/api-explorer/#/versions
 */
public interface VersionsRestApiClient {

    default NodeList getVersions(String nodeId) {
        return getVersions(nodeId, new PaginationQueryParameters(), new NodeQueryParameters<>());
    }

    NodeList getVersions(String nodeId, PaginationQueryParameters paginationQueryParameters,
            NodeQueryParameters<?> nodeQueryParameters);

    NodeEntry getVersion(String nodeId, String versionId);

}
