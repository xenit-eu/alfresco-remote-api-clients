package eu.xenit.alfresco.webscripst.client.ditto;

import eu.xenit.alfresco.webscripts.client.spi.ApiMetadataClient;
import eu.xenit.testing.ditto.api.AlfrescoDataSet;
import eu.xenit.testing.ditto.api.NodeView;
import eu.xenit.testing.ditto.api.model.Node;
import java.util.Optional;


public class ApiMetadataDittoClient implements ApiMetadataClient {

    private final NodeView nodeView;

    ApiMetadataDittoClient(AlfrescoDataSet dataSet) {
        this(dataSet.getNodeView());
    }

    ApiMetadataDittoClient(NodeView nodeView) {
        this.nodeView = nodeView;
    }


    @Override
    public Metadata get(String nodeRef) {

        Optional<Node> dittoNode = this.nodeView.getNode(nodeRef);

        return dittoNode.map(node -> {
            Metadata metadata = new Metadata();
            metadata.setType(node.getType().toString());

            return metadata;
        }).orElse(null);
    }
}
