
package eu.xenit.alfresco.solrapi.client.ditto;

import eu.xenit.alfresco.solrapi.client.spi.SolrApiClient;
import eu.xenit.alfresco.solrapi.client.spi.dto.Acl;
import eu.xenit.alfresco.solrapi.client.spi.dto.AclChangeSet;
import eu.xenit.alfresco.solrapi.client.spi.dto.AclReaders;
import eu.xenit.alfresco.solrapi.client.spi.dto.AlfrescoModel;
import eu.xenit.alfresco.solrapi.client.spi.dto.AlfrescoModelDiff;
import eu.xenit.alfresco.solrapi.client.spi.dto.NodeNamePaths;
import eu.xenit.alfresco.solrapi.client.spi.dto.NodePathInfo;
import eu.xenit.alfresco.solrapi.client.spi.dto.SolrNode;
import eu.xenit.alfresco.solrapi.client.spi.dto.SolrNodeMetaData;
import eu.xenit.alfresco.solrapi.client.spi.dto.SolrTransaction;
import eu.xenit.alfresco.solrapi.client.spi.dto.SolrTransactions;
import eu.xenit.alfresco.solrapi.client.spi.query.AclReadersQueryParameters;
import eu.xenit.alfresco.solrapi.client.spi.query.AclsQueryParameters;
import eu.xenit.alfresco.solrapi.client.spi.query.NodeMetaDataQueryParameters;
import eu.xenit.alfresco.solrapi.client.spi.query.NodesQueryParameters;
import eu.xenit.testing.ditto.api.AlfrescoDataSet;
import eu.xenit.testing.ditto.api.NodeView;
import eu.xenit.testing.ditto.api.TransactionView;
import eu.xenit.testing.ditto.api.data.ContentModel.Content;
import eu.xenit.testing.ditto.api.model.ContentData;
import eu.xenit.testing.ditto.api.model.MLText;
import eu.xenit.testing.ditto.api.model.Node;
import eu.xenit.testing.ditto.api.model.ParentChildAssoc;
import eu.xenit.testing.ditto.api.model.QName;
import eu.xenit.testing.ditto.api.model.Transaction;
import eu.xenit.testing.ditto.api.model.Transaction.Filters;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Data;

public class SolrApiFakeClient implements SolrApiClient {

    private final TransactionView txnView;
    private final NodeView nodeView;

    private SolrApiFakeClient(Builder builder) {
        this(builder.data);
    }

    SolrApiFakeClient(AlfrescoDataSet dataSet) {
        this(dataSet.getTransactionView(), dataSet.getNodeView());
    }

    SolrApiFakeClient(TransactionView transactionView, NodeView nodeView) {
        this.txnView = transactionView;
        this.nodeView = nodeView;
    }

    @Override
    public List<AclChangeSet> getAclChangeSets(Long fromId, Long fromTime, int maxResults) {
        return null;
    }

    @Override
    public List<Acl> getAcls(AclsQueryParameters parameters) {
        return null;
    }

    @Override
    public List<AclReaders> getAclReaders(AclReadersQueryParameters parameters) {
        return null;
    }

    @Override
    public SolrTransactions getTransactions(Long fromCommitTime, Long minTxnId, Long toCommitTime, Long maxTxnId,
            int maxResults) {

        List<SolrTransaction> transactions = this.txnView.stream()
                .filter(Transaction.Filters.fromCommitTime(fromCommitTime))
                .filter(Transaction.Filters.minTxnIdInclusive(minTxnId))
                .filter(Transaction.Filters.toCommitTime(toCommitTime))
                .filter(Transaction.Filters.maxTxnIdExclusive(maxTxnId))
                .limit(maxResults)

                .map(txn -> {
                    SolrTransaction solrTxn = new SolrTransaction();
                    solrTxn.setId(txn.getId());
                    solrTxn.setCommitTimeMs(txn.getCommitTimeMs());
                    solrTxn.setUpdates(txn.getUpdated().size());
                    solrTxn.setDeletes(txn.getDeleted().size());
                    return solrTxn;
                })

                .collect(Collectors.toList());

        return new SolrTransactions(transactions, this.txnView.getLastCommitTimeMs(), this.txnView.getLastTxnId());

    }

    @Override
    public List<SolrNode> getNodes(NodesQueryParameters parameters) {

        @Data
        class NodeStatusTuple {

            final Node node;
            final String status;
            final long txnId;

            private SolrNode getSolrNode() {
                return new SolrNode(
                        node.getNodeId(), node.getNodeRef().toString(),
                        txnId, status, "", -1, "");
            }
        }

        return this.txnView.stream()
                .filter(Filters.containedIn(parameters.getTxnIds()))
                .filter(Filters.minTxnIdInclusive(parameters.getFromTxnId()))
                .filter(Filters.maxTxnIdInclusive(parameters.getToTxnId()))
                .flatMap(txn -> Stream.concat(
                        txn.getUpdated().stream().map(n -> new NodeStatusTuple(n, "u", txn.getId())),
                        txn.getDeleted().stream().map(n -> new NodeStatusTuple(n, "d", txn.getId()))
                ))
                .filter(pair -> Node.Filters.minNodeIdInclusive(parameters.getFromNodeId()).test(pair.getNode()))
                .filter(pair -> Node.Filters.maxNodeIdInclusive(parameters.getToNodeId()).test(pair.getNode()))
                .map(NodeStatusTuple::getSolrNode)
                .collect(Collectors.toList());
    }

    @Override
    public List<SolrNodeMetaData> getNodesMetaData(NodeMetaDataQueryParameters params) {
        return this.nodeView.stream()
                .filter(Node.Filters.containedIn(params.getNodeIds()))
                .filter(Node.Filters.minNodeIdInclusive(params.getFromNodeId()))
                .filter(Node.Filters.maxNodeIdInclusive(params.getToNodeId()))
                .map(toSolrModel(params))
                .collect(Collectors.toList());
    }

    private Function<Node, SolrNodeMetaData> toSolrModel(NodeMetaDataQueryParameters params) {
        return node -> {
            SolrNodeMetaData ret = new SolrNodeMetaData();
            ret.setId(node.getNodeId());
            doIfTrue(params.isIncludeTxnId(), () -> ret.setTxnId(node.getTxnId()));
            doIfTrue(params.isIncludeType(), () -> ret.setType(node.getType().toPrefixString()));
            doIfTrue(params.isIncludeNodeRef(), () -> ret.setNodeRef(node.getNodeRef().toString()));
            doIfTrue(params.isIncludeProperties(), () -> ret.setProperties(node.getProperties().stream()
                    .collect(Collectors.toMap(
                            entry -> entry.getKey().toString(),
                            entry -> convertPropertyValue(entry.getValue())
                    ))));
            doIfTrue(params.isIncludeAspects(), () -> ret.setAspects(node.getAspects().stream()
                    .map(QName::toPrefixString)
                    .collect(Collectors.toList())));
            doIfTrue(params.isIncludeOwner(), () -> {
                String owner = node.getProperties().get(Content.OWNER).map(Object::toString)
                        .orElse(node.getProperties().get(Content.CREATOR).map(Object::toString).orElse(null));
                ret.setOwner(owner);
            });
            doIfTrue(params.isIncludePaths(), () -> ret.setPaths(this.getParentPaths(node)
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
                List<NodeNamePaths> namedPaths = this.getParentPaths(node)
                        .stream()
                        .map(lineage -> new NodeNamePaths(lineage.stream().map(assoc -> assoc.getChild().getName())))
                        .collect(Collectors.toList());
                ret.setNamePaths(namedPaths);
            });
            doIfTrue(params.isIncludePaths(), () -> ret.setAncestors(this.getPrimaryPath(node)
                    .stream()
                    .map(assoc -> assoc.getParent().getNodeRef().toString())
                    .collect(Collectors.toList())));
            doIfTrue(params.isIncludeChildAssociations() && node.getChildNodeCollection() != null, () -> {
                ret.setChildAssocs(node.getChildNodeCollection()
                        .getAssociations()
                        .map(this::toAssocString)
                        .collect(Collectors.toList())
                );
            });
            doIfTrue(params.isIncludeParentAssociations() && node.getParentNodeCollection() != null, () -> {
                ret.setParentAssocs(node.getParentNodeCollection()
                        .getAssociations()
                        .map(this::toAssocString)
                        .collect(Collectors.toList())
                );
            });
            return ret;
        };
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

        List<ParentChildAssoc> result = this.getPrimaryPath(assoc.getParent());
        result.add(assoc);
        return result;
    }

    private static Serializable convertPropertyValue(Serializable propertyValue) {
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

    private <T, P> void doIfTrue(boolean bool, Runnable runnable) {
        if (bool) {
            runnable.run();
        }
    }

    @Override
    public AlfrescoModel getModel(String coreName, String modelName) {
        return null;
    }

    @Override
    public List<AlfrescoModelDiff> getModelsDiff(String coreName, List<AlfrescoModel> currentModels) {
        return null;
    }

    @Override
    public void close() {

    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private AlfrescoDataSet data;

        private Builder() {

        }

        public SolrApiFakeClient build() {
            return new SolrApiFakeClient(this);
        }

        public Builder withDataSet(AlfrescoDataSet dataSet) {
            this.data = dataSet;
            return this;
        }

    }

    private static Function<Node, SolrNode> mapNodeToSolrNode(long txnId, String status) {
        return (node) -> new SolrNode(
                node.getNodeId(),
                node.getNodeRef().toString(),
                txnId, status,
                "",
                -1,
                "");
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
}
