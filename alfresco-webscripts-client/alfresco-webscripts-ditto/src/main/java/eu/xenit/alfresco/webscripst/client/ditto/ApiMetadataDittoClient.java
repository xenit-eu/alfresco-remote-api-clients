package eu.xenit.alfresco.webscripst.client.ditto;

import eu.xenit.alfresco.webscripts.client.spi.ApiMetadataClient;
import eu.xenit.alfresco.webscripts.client.spi.Metadata;
import eu.xenit.testing.ditto.api.AlfrescoDataSet;
import eu.xenit.testing.ditto.api.NodeView;
import eu.xenit.testing.ditto.api.model.ContentData;
import eu.xenit.testing.ditto.api.model.Node;
import eu.xenit.testing.ditto.api.model.QName;

import java.util.*;
import java.util.stream.Collectors;

public class ApiMetadataDittoClient implements ApiMetadataClient {

    private final NodeView nodeView;

    public ApiMetadataDittoClient(AlfrescoDataSet dataSet) {
        this(dataSet.getNodeView());
    }

    public ApiMetadataDittoClient(NodeView nodeView) {
        this.nodeView = nodeView;
    }


    @Override
    public Metadata get(String nodeRef) {

        Optional<Node> dittoNode = this.nodeView.getNode(nodeRef);

        return dittoNode.map(node -> {
            Metadata metadata = new Metadata();
            metadata.setType(node.getType().toString());
            metadata.setNodeRef(node.getNodeRef().toString());

            Optional<ContentData> contentData = node.getProperties().getContentData();
            contentData.ifPresent(data -> metadata.setMimetype(data.getMimeType()));

            Set<String> aspects = node.getAspects().stream().map(aspectQName -> aspectQName.toString()).collect(Collectors.toSet());
            metadata.setAspects(aspects);

            Map<String, String> props = new HashMap<>();
            for(QName key : node.getProperties().keySet()) {
                props.put(key.toString(), node.getProperties().get(key).toString());
            }
            metadata.setProperties(props);

            return metadata;
        }).orElse(null);
    }
}
