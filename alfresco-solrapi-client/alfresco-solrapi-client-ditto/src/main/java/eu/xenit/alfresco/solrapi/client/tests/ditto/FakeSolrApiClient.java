package eu.xenit.alfresco.solrapi.client.tests.ditto;

import eu.xenit.alfresco.solrapi.client.tests.spi.SolrApiClient;
import eu.xenit.alfresco.solrapi.client.tests.spi.dto.Acl;
import eu.xenit.alfresco.solrapi.client.tests.spi.dto.AclChangeSet;
import eu.xenit.alfresco.solrapi.client.tests.spi.dto.AclChangeSets;
import eu.xenit.alfresco.solrapi.client.tests.spi.dto.AclReaders;
import eu.xenit.alfresco.solrapi.client.tests.spi.dto.AlfrescoModel;
import eu.xenit.alfresco.solrapi.client.tests.spi.dto.AlfrescoModelDiff;
import eu.xenit.alfresco.solrapi.client.tests.spi.dto.SolrNode;
import eu.xenit.alfresco.solrapi.client.tests.spi.dto.SolrNodeMetaData;
import eu.xenit.alfresco.solrapi.client.tests.spi.dto.SolrTransaction;
import eu.xenit.alfresco.solrapi.client.tests.spi.dto.SolrTransactions;
import eu.xenit.alfresco.solrapi.client.tests.spi.query.NodeMetaDataQueryParameters;
import eu.xenit.alfresco.solrapi.client.tests.spi.query.NodesQueryParameters;
import eu.xenit.testing.ditto.api.AlfrescoDataSet;
import eu.xenit.testing.ditto.api.NodeView;
import eu.xenit.testing.ditto.api.TransactionView;
import eu.xenit.testing.ditto.api.data.ContentModel.Content;
import eu.xenit.testing.ditto.api.model.MLText;
import eu.xenit.testing.ditto.api.model.Node;
import eu.xenit.testing.ditto.api.model.QName;
import eu.xenit.testing.ditto.api.model.Transaction;
import eu.xenit.testing.ditto.api.model.Transaction.Filters;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Data;

public class FakeSolrApiClient implements SolrApiClient {

    private final TransactionView txnView;
    private final NodeView nodeView;

    private FakeSolrApiClient(Builder builder) {
        this(builder.data);
    }

    FakeSolrApiClient(AlfrescoDataSet dataSet) {
        this(dataSet.getTransactionView(), dataSet.getNodeView());
    }

    FakeSolrApiClient(TransactionView transactionView, NodeView nodeView) {
        this.txnView = transactionView;
        this.nodeView = nodeView;
    }



    @Override
    public AclChangeSets getAclChangeSets(Long fromCommitTime, Long minAclChangeSetId, Long toCommitTime,
            Long maxAclChangeSetId, int maxResults) {
        return null;
    }

    @Override
    public List<Acl> getAcls(List<AclChangeSet> aclChangeSets, Long minAclId, int maxResults) {
        return null;
    }

    @Override
    public List<AclReaders> getAclReaders(List<Acl> acls) {
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
            return ret;
        };
    }

    private static Serializable convertPropertyValue(Serializable propertyValue) {
        if (propertyValue instanceof MLText) {
            return (Serializable) ((MLText) propertyValue).entrySet().stream()
                    .map(e -> new HashMap<String, Serializable>() {{
                        put("locale", e.getKey());
                        put("value", e.getValue());
                    }})
                    .collect(Collectors.toList());
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

        public FakeSolrApiClient build() {
            return new FakeSolrApiClient(this);
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
}
