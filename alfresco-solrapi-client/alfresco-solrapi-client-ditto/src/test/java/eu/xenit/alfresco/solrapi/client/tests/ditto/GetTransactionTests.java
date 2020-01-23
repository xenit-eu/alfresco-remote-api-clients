package eu.xenit.alfresco.solrapi.client.tests.ditto;

import static org.assertj.core.api.Assertions.assertThat;

import eu.xenit.alfresco.solrapi.client.tests.spi.dto.SolrTransactions;
import eu.xenit.testing.ditto.api.AlfrescoDataSet;
import org.junit.jupiter.api.Test;

public class GetTransactionTests {

    private AlfrescoDataSet defaultDataSet = AlfrescoDataSet.bootstrapAlfresco().build();

    @Test
    public void testGetMaxTransactionId() {
        long maxTxnId = defaultDataSet.getTransactionView().getLastTxnId();

        FakeSolrApiClient solrApiClient = new FakeSolrApiClient(defaultDataSet);
        SolrTransactions result = solrApiClient.getTransactions(null, null, null, null, 0);

        assertThat(result)
                .satisfies(transactions -> assertThat(transactions.getMaxTxnId()).isEqualTo(maxTxnId));

    }


}
