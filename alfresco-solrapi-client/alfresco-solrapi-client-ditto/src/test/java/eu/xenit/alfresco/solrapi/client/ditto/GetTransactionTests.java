package eu.xenit.alfresco.solrapi.client.ditto;

import static org.assertj.core.api.Assertions.assertThat;

import eu.xenit.alfresco.client.solrapi.api.model.SolrTransactions;
import eu.xenit.testing.ditto.api.AlfrescoDataSet;
import org.junit.jupiter.api.Test;

public class GetTransactionTests {

    private AlfrescoDataSet defaultDataSet = AlfrescoDataSet.bootstrapAlfresco().build();

    @Test
    public void testGetMaxTransactionId() {
        long maxTxnId = defaultDataSet.getTransactionView().getLastTxnId();

        SolrApiFakeClient solrApiClient = new SolrApiFakeClient(defaultDataSet);
        SolrTransactions result = solrApiClient.getTransactions(null, null, null, null, 0);

        assertThat(result)
                .satisfies(transactions -> assertThat(transactions.getMaxTxnId()).isEqualTo(maxTxnId));

    }


}
