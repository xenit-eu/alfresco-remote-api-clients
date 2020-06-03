package eu.xenit.alfresco.solrapi.client.ditto;

import eu.xenit.alfresco.client.api.exception.HttpStatusException;
import eu.xenit.alfresco.client.api.exception.StatusCode;
import eu.xenit.testing.ditto.api.NodeView;
import eu.xenit.testing.ditto.api.data.ContentModel.Version2;
import eu.xenit.testing.ditto.api.model.Node;

public class LiveNodeExistChecker {

    public void noLiveNodeExistsCheck(Node node, NodeView nodeView) {
        if (!("version2Store".equals(node.getNodeRef().getStoreIdentifier()))) {
            return;
        }
        final String liveNodeRef = (String) node.getProperties().get(Version2.FROZEN_NODE_REF).orElse(null);
        if (liveNodeRef == null || liveNodeRef.trim().isEmpty()) {
            return;
        }
        final String deletedNodeRef = liveNodeRef.replace("workspace://SpacesStore/", "archive://SpacesStore/");
        if (nodeView.getNode(deletedNodeRef).isPresent()) {
            throw new HttpStatusException(StatusCode.INTERNAL_SERVER_ERROR, "No live node exists");
        }
    }

}
