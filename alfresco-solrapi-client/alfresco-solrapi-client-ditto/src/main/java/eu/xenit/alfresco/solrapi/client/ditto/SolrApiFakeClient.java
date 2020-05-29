package eu.xenit.alfresco.solrapi.client.ditto;

import eu.xenit.alfresco.client.solrapi.api.SolrApiClient;
import eu.xenit.alfresco.client.solrapi.api.model.Acl;
import eu.xenit.alfresco.client.solrapi.api.model.AclChangeSetList;
import eu.xenit.alfresco.client.solrapi.api.model.AclReaders;
import eu.xenit.alfresco.client.solrapi.api.model.AlfrescoModel;
import eu.xenit.alfresco.client.solrapi.api.model.AlfrescoModelDiff;
import eu.xenit.alfresco.client.solrapi.api.model.GetTextContentResponse;
import eu.xenit.alfresco.client.solrapi.api.model.SolrNode;
import eu.xenit.alfresco.client.solrapi.api.model.SolrNodeMetaData;
import eu.xenit.alfresco.client.solrapi.api.model.SolrTransaction;
import eu.xenit.alfresco.client.solrapi.api.model.SolrTransactions;
import eu.xenit.alfresco.client.solrapi.api.query.AclReadersQueryParameters;
import eu.xenit.alfresco.client.solrapi.api.query.AclsQueryParameters;
import eu.xenit.alfresco.client.solrapi.api.query.NodeMetaDataQueryParameters;
import eu.xenit.alfresco.client.solrapi.api.query.NodesQueryParameters;
import eu.xenit.testing.ditto.api.AlfrescoDataSet;
import eu.xenit.testing.ditto.api.NodeView;
import eu.xenit.testing.ditto.api.TransactionView;
import eu.xenit.testing.ditto.api.model.Node;
import eu.xenit.testing.ditto.api.model.Transaction;
import eu.xenit.testing.ditto.api.model.Transaction.Filters;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Data;

public class SolrApiFakeClient implements SolrApiClient {

    private final TransactionView txnView;
    private final NodeView nodeView;

    private SolrModelMapper solrModelMapper = new SolrModelMapper();
    private LiveNodeExistChecker liveNodeExistChecker = new LiveNodeExistChecker();

    protected <T extends SolrApiFakeClient> SolrApiFakeClient(Builder<T> builder) {
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
    public AclChangeSetList getAclChangeSets(Long fromId, Long fromTime, int maxResults) {
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

                .map(txn -> new SolrTransaction(
                        txn.getId(),
                        txn.getCommitTimeMs(),
                        txn.getUpdated().size(),
                        txn.getDeleted().size()
                ))

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
        return this.nodeView.allNodes()
                .filter(Node.Filters.containedIn(params.getNodeIds()))
                .filter(Node.Filters.minNodeIdInclusive(params.getFromNodeId()))
                .filter(Node.Filters.maxNodeIdInclusive(params.getToNodeId()))
                .peek(node -> getLiveNodeExistChecker().noLiveNodeExistsCheck(node, nodeView))
                .map(getSolrModelMapper().toSolrModel(params))
		.map(solrModelMapper::toApiModel)
		.collect(Collectors.toList());
    }


    @Override
    public GetTextContentResponse getTextContent(Long nodeId, String propertyQName) {
        return null;
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

    public static Builder<?> builder() {
        return new Builder<SolrApiFakeClient>() {};
    }

    protected static abstract class Builder<T extends SolrApiFakeClient> {
        private AlfrescoDataSet data;

        public Builder<T> withDataSet(AlfrescoDataSet dataSet) {
            this.data = dataSet;
            return this;
        }

        public SolrApiFakeClient build() {
            return new SolrApiFakeClient(this);
        }
    }

    public SolrModelMapper getSolrModelMapper() {
        return solrModelMapper;
    }

    public void setSolrModelMapper(SolrModelMapper solrModelMapper) {
        this.solrModelMapper = solrModelMapper;
    }

    public LiveNodeExistChecker getLiveNodeExistChecker() {
        return liveNodeExistChecker;
    }

    public void setLiveNodeExistChecker(LiveNodeExistChecker liveNodeExistChecker) {
        this.liveNodeExistChecker = liveNodeExistChecker;
    }

}
