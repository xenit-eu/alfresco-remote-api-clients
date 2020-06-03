package eu.xenit.alfresco.solrapi.client.ditto;

import eu.xenit.alfresco.client.solrapi.api.model.ChildAssociation;
import eu.xenit.alfresco.client.solrapi.api.model.NodeNamePaths;
import eu.xenit.alfresco.client.solrapi.api.model.NodePathInfo;
import eu.xenit.alfresco.client.solrapi.api.model.SolrNodeMetaData;
import eu.xenit.alfresco.client.solrapi.api.query.NodeMetaDataQueryParameters;
import eu.xenit.testing.ditto.api.data.ContentModel.Content;
import eu.xenit.testing.ditto.api.model.ContentData;
import eu.xenit.testing.ditto.api.model.MLText;
import eu.xenit.testing.ditto.api.model.Node;
import eu.xenit.testing.ditto.api.model.ParentChildAssoc;
import eu.xenit.testing.ditto.api.model.QName;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SolrModelMapper {

    public SolrNodeMetaData toSolrModel(Node node, NodeMetaDataQueryParameters params) {
        return new SolrNodeMetaData(
                node.getNodeId(),
                -1L,
                getTxnId(node, params),
                getNodeRef(node, params),
                getType(node, params),
                getProperties(node, params),
                getAspects(node, params),
                getPaths(node, params),
                getNamePaths(node, params),
                getAncestors(node, params),
                getParentAssocs(node, params),
                -1L,
                getChildAssocs(node, params),
                getChildIds(node, params),
                getOwner(node, params),
                null);
    }

    protected long getTxnId(Node node, NodeMetaDataQueryParameters params) {
        if (!params.isIncludeTxnId()) {
            return -1L;
        }
        return node.getTxnId();
    }

    protected String getNodeRef(Node node, NodeMetaDataQueryParameters params) {
        if (!params.isIncludeNodeRef()) {
            return null;
        }
        return node.getNodeRef().toString();
    }

    protected String getType(Node node, NodeMetaDataQueryParameters params) {
        if (!params.isIncludeType()) {
            return null;
        }
        return node.getType().toPrefixString();
    }

    protected Map<String, Object> getProperties(Node node, NodeMetaDataQueryParameters params) {
        if (!params.isIncludeProperties()) {
            return null;
        }

        return node.getProperties().stream().collect(HashMap::new,
                (map, entry) -> map.put(entry.getKey().toString(), convertPropertyValue(entry.getValue())),
                HashMap::putAll); // Collectors.toMap doesn't support null values (JDK-8148463)
    }

    protected List<String> getAspects(Node node, NodeMetaDataQueryParameters params) {
        if (!params.isIncludeAspects() || node.getAspects() == null) {
            return null;
        }
        return node.getAspects().stream()
                .map(QName::toPrefixString)
                .collect(Collectors.toList());
    }

    protected List<NodePathInfo> getPaths(Node node, NodeMetaDataQueryParameters params) {
        if (!params.isIncludePaths()) {
            return null;
        }
        return this.getParentPaths(node)
                .stream()
                .map(lineage -> {
                    String apath = "/" + lineage.stream()
                            .map(a -> a.getParent().getNodeRef().getUuid())
                            .collect(Collectors.joining("/"));
                    String path = "/" + lineage.stream()
                            .map(a -> a.getChild().getQName().toString())
                            .collect(Collectors.joining("/"));
                    return new NodePathInfo(apath, path, null);
                })
                .collect(Collectors.toList());
    }

    protected List<NodeNamePaths> getNamePaths(Node node, NodeMetaDataQueryParameters params) {
        if (!params.isIncludePaths()) {
            return null;
        }
        return this.getParentPaths(node)
                .stream()
                .map(lineage -> new NodeNamePaths(lineage.stream().map(assoc -> assoc.getChild().getName())))
                .collect(Collectors.toList());
    }

    protected List<String> getAncestors(Node node, NodeMetaDataQueryParameters params) {
        if (!params.isIncludePaths()) {
            return null;
        }
        return this.getPrimaryPath(node)
                .stream()
                .map(assoc -> assoc.getParent().getNodeRef().toString())
                .collect(Collectors.toList());
    }

    protected List<ChildAssociation> getParentAssocs(Node node, NodeMetaDataQueryParameters params) {
        if (!params.isIncludeParentAssociations() || node.getParentNodeCollection() == null) {
            return Collections.emptyList();
        }

        return node.getParentNodeCollection()
                .getAssociations()
                .map(this::toApiModel)
                .collect(Collectors.toList());
    }

    protected List<ChildAssociation> getChildAssocs(Node node, NodeMetaDataQueryParameters params) {
        if (!params.isIncludeChildAssociations() || node.getChildNodeCollection() == null) {
            return Collections.emptyList();
        }

        return node.getChildNodeCollection()
                .getAssociations()
                .map(this::toApiModel)
                .collect(Collectors.toList());
    }

    protected ChildAssociation toApiModel(ParentChildAssoc assoc) {
        return new ChildAssociation(
                assoc.getParent().getNodeRef().toString(),
                assoc.getChild().getNodeRef().toString(),
                assoc.getAssocTypeQName().toString(),
                assoc.getChild().getQName().toString(),
                assoc.isPrimary(),
                assoc.getNthSibling());
    }

    protected List<Long> getChildIds(Node node, NodeMetaDataQueryParameters params) {
        if (!params.isIncludeChildIds() || node.getChildNodeCollection() == null) {
            return null;
        }

        return node.getChildNodeCollection()
                .getAssociations()
                .map(ParentChildAssoc::getChild)
                .map(Node::getNodeId)
                .collect(Collectors.toList());
    }

    protected String getOwner(Node node, NodeMetaDataQueryParameters params) {
        if (!params.isIncludeOwner()) {
            return null;
        }
        return node.getProperties().get(Content.OWNER).map(Object::toString)
                .orElse(node.getProperties().get(Content.CREATOR).map(Object::toString).orElse(null));
    }

    private Serializable convertPropertyValue(Serializable propertyValue) {
        if (propertyValue instanceof MLText) {
            return (Serializable) ((MLText) propertyValue).stream()
                    .map(e -> new HashMap<String, Serializable>() {{
                        put("locale", e.getKey());
                        put("value", e.getValue());
                    }})
                    .collect(Collectors.toList());
        }
        if (propertyValue instanceof ContentData) {
            Map<String, String> retVal = new HashMap<>();
            retVal.put("contentId", "-1");
            retVal.put("encoding", ((ContentData) propertyValue).getEncoding());
            retVal.put("locale", ((ContentData) propertyValue).getLocale());
            retVal.put("mimetype", ((ContentData) propertyValue).getMimeType());
            retVal.put("size", Long.toString(((ContentData) propertyValue).getSize()));
            return (Serializable) retVal;
        }
        return propertyValue;
    }

    private List<List<ParentChildAssoc>> getParentPaths(Node node) {
        // TODO Get every parent-path - not only the primary parent path
        // for each parent assoc:
        ParentChildAssoc parentAssoc = node.getPrimaryParentAssoc();
        if (parentAssoc == null) {
            return new ArrayList<>(Collections.emptyList());
        }

        List<List<ParentChildAssoc>> parentPaths = getParentPaths(parentAssoc.getParent());
        if (parentPaths.isEmpty()) {
            // the parent is a root
            return toList(toList(parentAssoc));
        } else {
            // for each path of the parent
            return parentPaths.stream()
                    .peek(path -> path.add(parentAssoc))
                    .collect(Collectors.toList());
        }

        // end foreach
        // collect/flatmap all partial-paths

//        return parialResult;

    }

    private <T> List<T> toList(T item) {
        if (item == null) {
            return null;
        }

        List<T> list = new ArrayList<>();
        list.add(item);
        return list;
    }

    private List<ParentChildAssoc> getPrimaryPath(Node node) {
        ParentChildAssoc assoc = node.getPrimaryParentAssoc();
        if (assoc == null) {
            return new ArrayList<>();
        }

        List<ParentChildAssoc> result = getPrimaryPath(assoc.getParent());
        result.add(assoc);
        return result;
    }

}
