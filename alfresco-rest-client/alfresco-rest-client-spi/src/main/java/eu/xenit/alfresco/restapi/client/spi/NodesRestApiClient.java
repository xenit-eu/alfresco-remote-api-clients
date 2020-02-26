package eu.xenit.alfresco.restapi.client.spi;

import eu.xenit.alfresco.restapi.client.spi.model.NodeChildAssociationsList;
import eu.xenit.alfresco.restapi.client.spi.model.NodeEntry;
import eu.xenit.alfresco.restapi.client.spi.query.CreateNodeQueryParameters;
import eu.xenit.alfresco.restapi.client.spi.query.DeleteNodeQueryParameters;
import eu.xenit.alfresco.restapi.client.spi.query.GetNodeQueryParameters;
import eu.xenit.alfresco.restapi.client.spi.query.NodeCreateBody;
import eu.xenit.alfresco.restapi.client.spi.query.PaginationQueryParameters;

/**
 * see: https://api-explorer.alfresco.com/api-explorer/#/nodes
 */
public interface NodesRestApiClient {

    default void delete(String nodeId) {
        delete(nodeId, new DeleteNodeQueryParameters());
    }

    void delete(String nodeId, DeleteNodeQueryParameters queryParameters);

    default NodeEntry get(String nodeId) {
        return get(nodeId, new GetNodeQueryParameters());
    }

    NodeEntry get(String nodeId, GetNodeQueryParameters queryParameters);

    default NodeChildAssociationsList getChildren(String nodeId) {
        return getChildren(nodeId, new PaginationQueryParameters(), new GetNodeQueryParameters());
    }

    NodeChildAssociationsList getChildren(String nodeId, PaginationQueryParameters paginationQueryParameters,
            GetNodeQueryParameters nodeQueryParameters);

    default NodeEntry createChild(String nodeId, NodeCreateBody nodeCreateBody) {
        return createChild(nodeId, nodeCreateBody, new CreateNodeQueryParameters());
    }

    NodeEntry createChild(String nodeId, NodeCreateBody nodeCreateBody, CreateNodeQueryParameters queryParameters);

}
