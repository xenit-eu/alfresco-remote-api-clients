package eu.xenit.alfresco.webscripst.client.ditto;

import eu.xenit.alfresco.webscripts.client.spi.ApiMetadataClient;
import eu.xenit.testing.ditto.api.AlfrescoDataSet;
import eu.xenit.testing.ditto.api.NodeView;
import eu.xenit.testing.ditto.api.model.ContentData;
import eu.xenit.testing.ditto.api.model.Node;
import eu.xenit.testing.ditto.api.model.QName;
import eu.xenit.testing.ditto.util.MimeTypes;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ApiMetadataFakeClient implements ApiMetadataClient {

    private final NodeView nodeView;

    public ApiMetadataFakeClient(AlfrescoDataSet dataSet) {
        this(dataSet.getNodeView());
    }

    public ApiMetadataFakeClient(NodeView nodeView) {
        this.nodeView = nodeView;
    }


    @Override
    public Metadata get(String nodeRef) {

        Optional<Node> dittoNode = this.nodeView.getNode(nodeRef);

        return dittoNode.map(node -> {
            Metadata metadata = new Metadata();
            metadata.setType(node.getType().toString());
            metadata.setNodeRef(node.getNodeRef().toString());

            metadata.setMimetype(node.getProperties().getContentData()
                    .map(ContentData::getMimeType)
                    .orElse(MimeTypes.APPLICATION_OCTET_STREAM));

            Set<String> aspects = node.getAspects().stream().map(QName::toString)
                    .collect(Collectors.toSet());
            metadata.setAspects(aspects);

            Map<String, String> props = new LinkedHashMap<>();
            node.getProperties().forEach((key, value) -> {
                props.put(key.toString(), value.toString());
            });
            metadata.setProperties(props);

            return metadata;
        }).orElse(null);
    }
}
