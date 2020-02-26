package eu.xenit.alfresco.restapi.client.spi;

import eu.xenit.alfresco.restapi.client.spi.model.Node;
import eu.xenit.alfresco.restapi.client.spi.model.NodeChildAssociationsList;
import eu.xenit.alfresco.restapi.client.spi.model.NodeEntry;
import eu.xenit.alfresco.restapi.client.spi.query.DeleteNodeQueryParameters;
import eu.xenit.alfresco.restapi.client.spi.query.GetNodeQueryParameters;
import eu.xenit.alfresco.restapi.client.spi.query.PaginationQueryParameters;

/**
 * see: https://api-explorer.alfresco.com/api-explorer/#/nodes
 */
public interface NodesRestApiClient {

    default void delete(String nodeId) {
        delete(nodeId, new DeleteNodeQueryParameters());
    }

    void delete(String nodeId, DeleteNodeQueryParameters queryParameters);

    default Node get(String nodeId) {
        return get(nodeId, new GetNodeQueryParameters());
    }

    default Node get(String nodeId, GetNodeQueryParameters queryParameters) {
        NodeEntry entry = getNodeEntry(nodeId, queryParameters);
        return entry == null ? null : entry.getEntry();
    }

    default NodeEntry getNodeEntry(String nodeId) {
        return getNodeEntry(nodeId, new GetNodeQueryParameters());
    }

    NodeEntry getNodeEntry(String nodeId, GetNodeQueryParameters queryParameters);

    default NodeChildAssociationsList getChildren(String nodeId) {
        return getChildren(nodeId, new PaginationQueryParameters(), new GetNodeQueryParameters());
    }

    NodeChildAssociationsList getChildren(String nodeId, PaginationQueryParameters paginationQueryParameters,
            GetNodeQueryParameters nodeQueryParameters);

}
