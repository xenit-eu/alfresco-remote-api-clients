package eu.xenit.alfresco.webscripst.client.ditto;

import eu.xenit.alfresco.webscripts.client.spi.SlingshotClient;
import eu.xenit.testing.ditto.api.AlfrescoDataSet;
import eu.xenit.testing.ditto.api.NodeView;
import eu.xenit.testing.ditto.api.model.Node;
import eu.xenit.testing.ditto.api.model.NodeProperties;
import eu.xenit.testing.ditto.api.model.NodeReference;
import eu.xenit.testing.ditto.api.model.ParentChildNodeCollection;
import eu.xenit.testing.ditto.api.model.QName;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class SlingShotDittoClient implements SlingshotClient {

    private final NodeView nodeView;
    private Supplier<List<Association>> targetAssocSupplier; // TODO temporary
    private Supplier<List<Association>> sourceAssocSupplier; // TODO temporary
    private Supplier<Permissions> permissionsSupplier; // TODO temporary

    public SlingShotDittoClient(AlfrescoDataSet dataSet) {
        this(dataSet.getNodeView());
    }

    public SlingShotDittoClient(NodeView nodeView) {
        this.nodeView = nodeView;
    }

    @Override
    public Metadata get(String nodeRef) {

        Optional<Node> dittoNode = this.nodeView.getNode(nodeRef);
        return dittoNode.map(node -> {
            Metadata metadata = new Metadata();
            metadata.setNodeRef(node.getNodeRef().toString());
            metadata.setQnamePath(toNameContainer(null, node.getQNamePath())); // NodeView provides as prefixed TODO
            metadata.setName(toNameContainer(null, node.getName())); // expecting cm:myDoc.pdf as prefixed TODO
            metadata.setParentNodeRef(node.getParent().getNodeRef().toString());
            metadata.setType(toNameContainer(node.getType().toString(), node.getType().toPrefixString()));
            metadata.setId(node.getNodeRef().getUuid());
            metadata.setAspects(toNameContainers(node.getAspects()));
            metadata.setProperties(toProperties(node.getProperties()));
            metadata.setChildren(toParentsFromChildCollection(node.getChildNodeCollection()));
            metadata.setParents(toParentsFromParentCollection(node.getParentNodeCollection()));
            metadata.setAssocs(targetAssocSupplier.get()); // TODO
            metadata.setSourceAssocs(sourceAssocSupplier.get()); // TODO
            metadata.setPermissions(permissionsSupplier.get()); // TODO
            return metadata;
        }).orElse(null);
    }

    private NameContainer toNameContainer(String name, String prefixedName) {
        NameContainer nameContainer = new NameContainer();
        // TODO if one is missing, generate from the other?
        nameContainer.setName(name);
        nameContainer.setPrefixedName(prefixedName);
        return nameContainer;
    }

    private List<NameContainer> toNameContainers(Set<QName> qnames) {
        return qnames.stream()
                .map(qName -> toNameContainer(qName.toString(), qName.toPrefixString()))
                .collect(Collectors.toList());
    }

    private List<Property> toProperties(NodeProperties nodeProperties) {
        List<Property> ret = new ArrayList<>();
        nodeProperties.forEach((qName, serializable) -> {
                    NameContainer name = toNameContainer(qName.toString(), qName.toPrefixString());
                    NameContainer type = toNameContainer("type full qname here",
                            "type prefixed name here"); // TODO how to determine
                    ValueContainer value = new ValueContainer("datatype here", (String) serializable, false, false,
                            false); // TODO how to determine
                    boolean multiple = false; // TODO how to determine
                    boolean residual = false; // TODO how to determine
                    ret.add(new Property(name, value, type, multiple, residual));
                }
        );
        return ret;
    }

    private List<Parent> toParentsFromChildCollection(ParentChildNodeCollection childAssocs) {
        return toParents(childAssocs, true);
    }

    private List<Parent> toParentsFromParentCollection(ParentChildNodeCollection parentAssocs) {
        return toParents(parentAssocs, false);
    }

    private List<Parent> toParents(ParentChildNodeCollection collection, boolean child) {
        return collection.getAssociations()
                .map(parentChildAssoc -> {
                    Parent parent = new Parent();
                    Node childNode = parentChildAssoc.getChild();
                    Node parentNode = parentChildAssoc.getParent();

                    parent.setName(toNameContainer(childNode.getName(),null));
                    NodeReference nodeRef = child ? childNode.getNodeRef(): parentNode.getNodeRef();
                    parent.setNodeRef(nodeRef.toString());
                    QName type = child ? childNode.getType() : parentNode.getType();
                    parent.setType(toNameContainer(type.toString(),type.toPrefixString()));

                    QName assocTypeQName = parentChildAssoc.getAssocTypeQName();
                    parent.setAssocType(toNameContainer(assocTypeQName.toString(), assocTypeQName.toPrefixString()));

                    parent.setPrimary(parentChildAssoc.isPrimary());
                    parent.setIndex(-1); // TODO how to determine

                    return parent;
        }).collect(Collectors.toList());
    }

}
