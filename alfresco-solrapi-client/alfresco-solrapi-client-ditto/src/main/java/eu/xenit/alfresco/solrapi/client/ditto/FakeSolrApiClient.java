package eu.xenit.alfresco.solrapi.client.ditto;

import eu.xenit.alfresco.solrapi.client.spi.SolrApiClient;
import eu.xenit.alfresco.solrapi.client.spi.dto.Acl;
import eu.xenit.alfresco.solrapi.client.spi.dto.AclChangeSet;
import eu.xenit.alfresco.solrapi.client.spi.dto.AclChangeSets;
import eu.xenit.alfresco.solrapi.client.spi.dto.AclReaders;
import eu.xenit.alfresco.solrapi.client.spi.dto.AlfrescoModel;
import eu.xenit.alfresco.solrapi.client.spi.dto.AlfrescoModelDiff;
import eu.xenit.alfresco.solrapi.client.spi.dto.SolrNode;
import eu.xenit.alfresco.solrapi.client.spi.dto.SolrNodeMetaData;
import eu.xenit.alfresco.solrapi.client.spi.dto.SolrTransaction;
import eu.xenit.alfresco.solrapi.client.spi.dto.SolrTransactions;
import eu.xenit.alfresco.solrapi.client.spi.query.NodeMetaDataQueryParameters;
import eu.xenit.alfresco.solrapi.client.spi.query.NodesQueryParameters;
import eu.xenit.testing.ditto.api.AlfrescoDataSet;
import eu.xenit.testing.ditto.api.Transaction;
import eu.xenit.testing.ditto.api.TransactionView;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FakeSolrApiClient implements SolrApiClient {

    private final AlfrescoDataSet dataSet;

    private FakeSolrApiClient(Builder builder) {
        this(builder.data);
    }

    FakeSolrApiClient(AlfrescoDataSet dataSet) {
        this.dataSet = dataSet;
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
        TransactionView txnView = this.dataSet.getTransactionView();

        List<SolrTransaction> transactions = txnView.stream()
                .filter(Transaction.Filters.fromCommitTime(fromCommitTime))
                .filter(Transaction.Filters.minTxnId(minTxnId))
                .filter(Transaction.Filters.toCommitTime(toCommitTime))
                .filter(Transaction.Filters.maxTxnId(maxTxnId))
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

        return new SolrTransactions(transactions, txnView.getLastCommitTimeMs(), txnView.getLastTxnId());

    }

    @Override
    public List<SolrNode> getNodes(NodesQueryParameters parameters) {
        if (parameters.getFromNodeId() != null) {
            throw new UnsupportedOperationException("fromNodeId is not yet supported");
        }

        if (parameters.getToNodeId() != null) {
            throw new UnsupportedOperationException("toNodeId is not yet supported");
        }

        if (parameters.getTxnIds() != null) {
            return parameters.getTxnIds().stream()
                    .map(txnId -> this.dataSet.getTransactionView().getTransactionById(txnId))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .flatMap(txn -> Stream.concat(
                            txn.getUpdated()
                                    .stream()
                                    .map(n -> new SolrNode(n.getNodeId(), n.getNodeRef().toString(), txn.getId(), "u", "", -1, "")),
                            txn.getDeleted()
                                    .stream()
                                    .map(n -> new SolrNode(n.getNodeId(), n.getNodeRef().toString(), txn.getId(), "d", "", -1, ""))
                    ))
                    .collect(Collectors.toList());
        } else {
            throw new UnsupportedOperationException("missing txnIds");
        }


    }

    @Override
    public List<SolrNodeMetaData> getNodesMetaData(NodeMetaDataQueryParameters params) {
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
}
