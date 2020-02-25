package eu.xenit.alfresco.webscripts.client.ditto;

import eu.xenit.alfresco.webscripts.client.spi.SlingShotClient;
import eu.xenit.alfresco.webscripts.client.ditto.model.ModelHelper;
import eu.xenit.alfresco.webscripts.client.ditto.model.ModelInfo;
import eu.xenit.alfresco.webscripts.client.spi.model.slingshot.Metadata;
import eu.xenit.alfresco.webscripts.client.spi.model.slingshot.Metadata.Association;
import eu.xenit.alfresco.webscripts.client.spi.model.slingshot.Metadata.NameContainer;
import eu.xenit.alfresco.webscripts.client.spi.model.slingshot.Metadata.Parent;
import eu.xenit.alfresco.webscripts.client.spi.model.slingshot.Metadata.Property;
import eu.xenit.alfresco.webscripts.client.spi.model.slingshot.Metadata.ValueContainer;
import eu.xenit.testing.ditto.api.AlfrescoDataSet;
import eu.xenit.testing.ditto.api.NodeView;
import eu.xenit.testing.ditto.api.model.Namespace;
import eu.xenit.testing.ditto.api.model.Node;
import eu.xenit.testing.ditto.api.model.NodeProperties;
import eu.xenit.testing.ditto.api.model.NodeReference;
import eu.xenit.testing.ditto.api.model.ParentChildNodeCollection;
import eu.xenit.testing.ditto.api.model.PeerAssoc;
import eu.xenit.testing.ditto.api.model.PeerAssocCollection;
import eu.xenit.testing.ditto.api.model.PeerAssocCollection.Type;
import eu.xenit.testing.ditto.api.model.QName;
import eu.xenit.testing.ditto.util.StringUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class SlingShotFakeClient implements SlingShotClient {

    private final NodeView nodeView;
    private final ModelHelper modelHelper = new ModelHelper();

    public SlingShotFakeClient(AlfrescoDataSet dataSet) {
        this(dataSet.getNodeView());
    }

    public SlingShotFakeClient(NodeView nodeView) {
        this.nodeView = nodeView;
    }

    @Override
    public Metadata get(String nodeRef) {

        Optional<Node> dittoNode = this.nodeView.getNode(nodeRef);
        return dittoNode.map(node -> {
            Metadata metadata = new Metadata();
            metadata.setNodeRef(node.getNodeRef().toString());
            metadata.setQnamePath(
                    new NameContainer(toFullyQualifiedQNamePath(node.getQNamePath()), node.getQNamePath()));
            metadata.setName(toNameContainer(node.getQName()));
            metadata.setParentNodeRef(node.getParent() == null ? null : node.getParent().getNodeRef().toString());
            metadata.setType(toNameContainer(node.getType()));
            metadata.setId(node.getNodeRef().getUuid());
            metadata.setAspects(toNameContainers(node.getAspects()));
            metadata.setProperties(toProperties(node.getProperties()));
            metadata.setChildren(toParentsFromChildCollection(node.getChildNodeCollection()));
            metadata.setParents(toParentsFromParentCollection(node.getParentNodeCollection()));
            metadata.setAssocs(toAssociations(Type.TARGET, node.getTargetAssociationCollection()));
            metadata.setSourceAssocs(toAssociations(Type.SOURCE, node.getSourceAssociationCollection()));
            metadata.setPermissions(null); // TODO not yet in ditto
            return metadata;
        }).orElse(null);
    }

    private NameContainer toNameContainer(QName qName) {
        return new NameContainer(qName.toString(), qName.toPrefixString());
    }

    private List<NameContainer> toNameContainers(Set<QName> qnames) {
        return qnames.stream()
                .map(this::toNameContainer)
                .collect(Collectors.toList());
    }

    private List<Property> toProperties(NodeProperties nodeProperties) {
        List<Property> ret = new ArrayList<>();
        nodeProperties.forEach((qName, serializable) -> {
            ModelInfo info = modelHelper.getModelInfoByQName(qName);
            NameContainer name = toNameContainer(qName);
            NameContainer type = new NameContainer(info.getType().toString(), info.getType().toPrefixString());
            boolean multiple = isMultiple(serializable);
            boolean residual = info.isResidual();
            if (!multiple) {
                String value = info.getDeserializer().apply(serializable);
                ValueContainer valueContainer = new ValueContainer(info.getDataType(), value, info.isContent(),
                    info.isNodeRef(), StringUtils.nullOrEmpty(value));
                ret.add(new Property(name, Collections.singletonList(valueContainer), type, multiple, residual));
            }
            else {
                List<ValueContainer> valueContainers = new ArrayList<>();
                ((Collection<Serializable>) serializable)
                        .forEach(element -> {
                            String value = info.getDeserializer().apply(element);
                            valueContainers.add(new ValueContainer(info.getDataType(), value, info.isContent(),
                                    info.isNodeRef(), StringUtils.nullOrEmpty(value)));
                        });
                ret.add(new Property(name, valueContainers, type, multiple, residual));
            }
        });
        return ret;
    }

    private List<Parent> toParentsFromChildCollection(ParentChildNodeCollection childAssocs) {
        return toParents(childAssocs, true);
    }

    private List<Parent> toParentsFromParentCollection(ParentChildNodeCollection parentAssocs) {
        return toParents(parentAssocs, false);
    }

    private List<Parent> toParents(ParentChildNodeCollection collection, boolean child) {
        if (collection == null) {
            return Collections.emptyList();
        }
        return collection.getAssociations()
                .map(parentChildAssoc -> {
                    Parent parent = new Parent();
                    Node childNode = parentChildAssoc.getChild();
                    Node parentNode = parentChildAssoc.getParent();

                    parent.setName(new NameContainer(childNode.getName(), null));
                    NodeReference nodeRef = child ? childNode.getNodeRef() : parentNode.getNodeRef();
                    parent.setNodeRef(nodeRef.toString());
                    QName type = child ? childNode.getType() : parentNode.getType();
                    parent.setType(toNameContainer(type));

                    QName assocTypeQName = parentChildAssoc.getAssocTypeQName();
                    parent.setAssocType(toNameContainer(assocTypeQName));

                    parent.setPrimary(parentChildAssoc.isPrimary());
                    parent.setIndex(-1); // TODO how to determine

                    return parent;
                }).collect(Collectors.toList());
    }

    private List<Association> toAssociations(Type type, PeerAssocCollection peerAssocCollection) {
        return peerAssocCollection.getAssociations()
                .map(s -> toAssociation(type, s))
                .collect(Collectors.toList());
    }

    private Association toAssociation(Type type, PeerAssoc peerAssoc) {
        QName sourceOrTargetType = type == Type.TARGET ?
                peerAssoc.getTargetNode().getType() :
                peerAssoc.getSourceNode().getType();
        NodeReference sourceRef = peerAssoc.getSourceNode().getNodeRef();
        NodeReference targetRef = peerAssoc.getTargetNode().getNodeRef();
        QName assocTypeQName = peerAssoc.getAssocTypeQName();

        return new Association(toNameContainer(sourceOrTargetType), sourceRef.toString(), targetRef.toString(),
                toNameContainer(assocTypeQName));
    }

    private String toFullyQualifiedQNamePath(String prefixedQNamePath) {
        String ret = prefixedQNamePath;
        for (Entry<String, Namespace> entry : modelHelper.getPrefixToNamespaceMap().entrySet()) {
            ret = ret.replaceAll("/" + entry.getKey() + ":", "/{" + entry.getValue().getNamespace() + "}");
        }
        return ret;
    }

    private boolean isMultiple(Serializable value) {
        return value instanceof Collection;
    }

}
