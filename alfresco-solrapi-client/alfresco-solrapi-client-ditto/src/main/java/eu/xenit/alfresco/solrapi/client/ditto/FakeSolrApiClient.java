package eu.xenit.alfresco.solrapi.client.ditto;

import eu.xenit.alfresco.solrapi.client.spi.dto.Acl;
import eu.xenit.alfresco.solrapi.client.spi.dto.AclChangeSet;
import eu.xenit.alfresco.solrapi.client.spi.dto.AclChangeSets;
import eu.xenit.alfresco.solrapi.client.spi.dto.AclReaders;
import eu.xenit.alfresco.solrapi.client.spi.dto.AlfrescoModel;
import eu.xenit.alfresco.solrapi.client.spi.dto.AlfrescoModelDiff;
import eu.xenit.alfresco.solrapi.client.spi.dto.SolrNode;
import eu.xenit.alfresco.solrapi.client.spi.dto.SolrNodeMetaData;
import eu.xenit.alfresco.solrapi.client.spi.query.NodeMetaDataQueryParameters;
import eu.xenit.alfresco.solrapi.client.spi.query.NodesQueryParameters;
import eu.xenit.alfresco.solrapi.client.spi.SolrApiClient;
import eu.xenit.alfresco.solrapi.client.spi.dto.SolrTransaction;
import eu.xenit.alfresco.solrapi.client.spi.dto.SolrTransactions;
import eu.xenit.testing.ditto.alfresco.AlfrescoDataSet;
import eu.xenit.testing.ditto.alfresco.TransactionContainer;
import eu.xenit.testing.ditto.alfresco.TransactionFilter;
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
        TransactionContainer txnLog = this.dataSet.getTransactions();

        List<SolrTransaction> transactions = txnLog.stream()
                .filter(TransactionFilter.fromCommitTime(fromCommitTime))
                .filter(TransactionFilter.minTxnId(minTxnId))
                .filter(TransactionFilter.toCommitTime(toCommitTime))
                .filter(TransactionFilter.maxTxnId(maxTxnId))
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

        return new SolrTransactions(transactions, txnLog.getLastCommitTimeMs(), txnLog.getLastTxnId());

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
                    .map(txnId -> this.dataSet.getTransactions().getTransactionById(txnId))
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
