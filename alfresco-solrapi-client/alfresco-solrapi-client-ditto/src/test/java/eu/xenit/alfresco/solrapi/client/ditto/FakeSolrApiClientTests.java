package eu.xenit.alfresco.solrapi.client.ditto;

import static org.assertj.core.api.Assertions.assertThat;

import eu.xenit.alfresco.solrapi.client.spi.SolrTransactions;
import eu.xenit.testing.ditto.alfresco.AlfrescoDataSet;
import org.junit.jupiter.api.Test;

public class FakeSolrApiClientTests {

    private AlfrescoDataSet defaultDataSet = AlfrescoDataSet.builder().bootstrapAlfresco().build();

    @Test
    public void testGetMaxTransactionId() {
        long maxTxnId = defaultDataSet.getTransactions().getLastTxnId();

        FakeSolrApiClient solrApiClient = new FakeSolrApiClient(defaultDataSet);
        SolrTransactions transactions = solrApiClient.getTransactions(null, null, null, null, 0);

        assertThat(transactions)
                .satisfies(txns -> assertThat(txns.getMaxTxnId()).isEqualTo(maxTxnId));

    }
}
