package eu.xenit.alfresco.solrapi.client.ditto;

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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SolrModelMapper {

    public Function<Node, SolrNodeMetaData> toSolrModel(NodeMetaDataQueryParameters params) {
        return node -> {
            SolrNodeMetaData ret = new SolrNodeMetaData();
            ret.setId(node.getNodeId());
            doIfTrue(params.isIncludeTxnId(), () -> ret.setTxnId(node.getTxnId()));
            doIfTrue(params.isIncludeType(), () -> ret.setType(node.getType().toPrefixString()));
            doIfTrue(params.isIncludeNodeRef(), () -> ret.setNodeRef(node.getNodeRef().toString()));
            doIfTrue(params.isIncludeProperties(), () -> ret.setProperties(node.getProperties().stream()
                    .collect(HashMap::new,
                            (map, entry) -> map.put(entry.getKey().toString(), convertPropertyValue(entry.getValue())),
                            HashMap::putAll))); // Collectors.toMap doesn't support null values (JDK-8148463)
            doIfTrue(params.isIncludeAspects(), () -> ret.setAspects(node.getAspects().stream()
                    .map(QName::toPrefixString)
                    .collect(Collectors.toList())));
            doIfTrue(params.isIncludeOwner(), () -> {
                String owner = node.getProperties().get(Content.OWNER).map(Object::toString)
                        .orElse(node.getProperties().get(Content.CREATOR).map(Object::toString).orElse(null));
                ret.setOwner(owner);
            });
            doIfTrue(params.isIncludePaths(), () -> ret.setPaths(getParentPaths(node)
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
                    .collect(Collectors.toList())));
            doIfTrue(params.isIncludePaths(), () -> {
                List<NodeNamePaths> namedPaths = getParentPaths(node)
                        .stream()
                        .map(lineage -> new NodeNamePaths(lineage.stream().map(assoc -> assoc.getChild().getName())))
                        .collect(Collectors.toList());
                ret.setNamePaths(namedPaths);
            });
            doIfTrue(params.isIncludePaths(), () -> ret.setAncestors(getPrimaryPath(node)
                    .stream()
                    .map(assoc -> assoc.getParent().getNodeRef().toString())
                    .collect(Collectors.toList())));
            doIfTrue(params.isIncludeChildAssociations() && node.getChildNodeCollection() != null, () ->
                    ret.setChildAssocs(node.getChildNodeCollection()
                            .getAssociations()
                            .map(this::toAssocString)
                            .collect(Collectors.toList())
                    ));
            doIfTrue(params.isIncludeParentAssociations() && node.getParentNodeCollection() != null, () ->
                    ret.setParentAssocs(node.getParentNodeCollection()
                            .getAssociations()
                            .map(this::toAssocString)
                            .collect(Collectors.toList())
                    ));
            return ret;
        };
    }

    private void doIfTrue(boolean bool, Runnable runnable) {
        if (bool) {
            runnable.run();
        }
    }

    private String toAssocString(ParentChildAssoc assoc) {
        List<String> objects = Arrays.asList(
                assoc.getParent().getNodeRef().toString(),
                assoc.getChild().getNodeRef().toString(),
                assoc.getAssocTypeQName().toString(),
                assoc.getChild().getQName().toString(),
                String.valueOf(assoc.isPrimary()),
                "-1" // TODO association index, currently no way of getting it, usually -1 anyway
        );
        return String.join("|", objects);
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
