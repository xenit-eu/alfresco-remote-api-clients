package eu.xenit.alfresco.restapi.client.spi;

import eu.xenit.alfresco.restapi.client.spi.model.NodeCreateBody;
import eu.xenit.alfresco.restapi.client.spi.model.NodeEntry;
import eu.xenit.alfresco.restapi.client.spi.model.NodeList;
import eu.xenit.alfresco.restapi.client.spi.model.TargetAssociation;
import eu.xenit.alfresco.restapi.client.spi.model.TargetAssociationEntry;
import eu.xenit.alfresco.restapi.client.spi.query.CreateNodeQueryParameters;
import eu.xenit.alfresco.restapi.client.spi.query.DeleteNodeQueryParameters;
import eu.xenit.alfresco.restapi.client.spi.query.DeleteTargetQueryParameters;
import eu.xenit.alfresco.restapi.client.spi.query.FilterQueryParameters;
import eu.xenit.alfresco.restapi.client.spi.query.GetNodeQueryParameters;
import eu.xenit.alfresco.restapi.client.spi.query.NodeQueryParameters;
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

    default NodeList getChildren(String nodeId) {
        return getChildren(nodeId, new PaginationQueryParameters(), new FilterQueryParameters(),
                new NodeQueryParameters());
    }

    NodeList getChildren(String nodeId, PaginationQueryParameters paginationQueryParameters,
            FilterQueryParameters filterQueryParameters, NodeQueryParameters nodeQueryParameters);

    default NodeEntry createChild(String nodeId, NodeCreateBody nodeCreateBody) {
        return createChild(nodeId, nodeCreateBody, new CreateNodeQueryParameters());
    }

    NodeEntry createChild(String nodeId, NodeCreateBody nodeCreateBody, CreateNodeQueryParameters queryParameters);

    default NodeList getSources(String sourceNodeId) {
        return getSources(sourceNodeId, new FilterQueryParameters(), new NodeQueryParameters());
    }

    NodeList getSources(String nodeId, FilterQueryParameters filterQueryParameters,
            NodeQueryParameters nodeQueryParameters);

    default NodeList getTargets(String nodeId) {
        return getTargets(nodeId, new FilterQueryParameters(), new NodeQueryParameters());
    }

    NodeList getTargets(String nodeId, FilterQueryParameters filterQueryParameters,
            NodeQueryParameters nodeQueryParameters);

    TargetAssociationEntry createTargetAssociation(String nodeId, TargetAssociation targetAssociation);

    default void deleteTargetAssociation(String nodeId, String targetId) {
        deleteTargetAssociation(nodeId, targetId, new DeleteTargetQueryParameters());
    }

    void deleteTargetAssociation(String nodeId, String targetId, DeleteTargetQueryParameters queryParameters);
}
