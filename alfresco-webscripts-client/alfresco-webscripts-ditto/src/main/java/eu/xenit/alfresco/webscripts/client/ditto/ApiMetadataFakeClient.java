package eu.xenit.alfresco.webscripts.client.ditto;

import eu.xenit.alfresco.webscripts.client.spi.ApiMetadataClient;
import eu.xenit.testing.ditto.api.AlfrescoDataSet;
import eu.xenit.testing.ditto.api.NodeView;
import eu.xenit.testing.ditto.api.data.ContentModel.Content;
import eu.xenit.testing.ditto.api.model.ContentData;
import eu.xenit.testing.ditto.api.model.MLText;
import eu.xenit.testing.ditto.api.model.Node;
import eu.xenit.testing.ditto.api.model.QName;
import eu.xenit.testing.ditto.util.MimeTypes;
import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
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

            Map<String, Object> props = new LinkedHashMap<>();
            node.getProperties().forEach((key, value) -> props.put(key.toString(), convertPropertyValue(value)));
            metadata.setProperties(props);

            return metadata;
        }).orElse(null);
    }

    @Override
    public List<BulkMetadata> get(List<String> nodeRefs) {
        if (nodeRefs == null || nodeRefs.isEmpty()) {
            return Collections.emptyList();
        }

        return nodeRefs.stream().map(this::getBulkMetadata).collect(Collectors.toList());
    }

    private BulkMetadata getBulkMetadata(String nodeRef) {

        BulkMetadata ret = new BulkMetadata();
        ret.setNodeRef(nodeRef);

        Optional<Node> dittoNode =
                this.nodeView.stream().filter(n -> nodeRef.equals(n.getNodeRef().toString())).findAny();

        if (!dittoNode.isPresent()) {
            ret.setError("true");
            ret.setErrorCode("invalidNodeRef");
            ret.setErrorText("");
            return ret;
        }

        return dittoNode.map(node -> {
            ret.setParentNodeRef(node.getParent().getNodeRef().toString());
            ret.setType(node.getType().toString());
            ret.setShortType(node.getType().toPrefixString());
//            ret.setTypeTitle(); not yet supported
            ret.setName(node.getName());
            node.getProperties().get(Content.TITLE).ifPresent(v -> ret.setTitle(((MLText) v).get()));
            ret.setMimeType("");
            node.getProperties().get(Content.CONTENT).ifPresent(v -> ret.setMimeType(((ContentData) v).getMimeType()));
            ret.setHasWritePermission(true);
            ret.setHasDeletePermission(true);
            return ret;
        }).orElse(null);

    }

    private Object convertPropertyValue(final Serializable dittoValue) {
        if (dittoValue instanceof MLText) {
            return ((MLText) dittoValue).get();
        }
        if (dittoValue instanceof Long) {
            return Math.toIntExact((Long) dittoValue);
        }
        if (dittoValue instanceof ContentData) {
            return dittoValue.toString();
        }

        return dittoValue;
    }
}
