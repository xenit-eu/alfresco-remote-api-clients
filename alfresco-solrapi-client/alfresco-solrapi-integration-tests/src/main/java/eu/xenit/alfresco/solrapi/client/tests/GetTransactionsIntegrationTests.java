package eu.xenit.alfresco.solrapi.client.tests;

import static org.assertj.core.api.Assertions.assertThat;

import eu.xenit.alfresco.client.solrapi.api.SolrApiClient;
import eu.xenit.alfresco.client.solrapi.api.model.SolrTransactions;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public interface GetTransactionsIntegrationTests {

    SolrApiClient solrApiClient();

    @Test
    default void getTransactions() {

        SolrApiClient client = solrApiClient();

        SolrTransactions transactions = client.getTransactions(null, 1L, null, 10L, 1);
        assertThat(transactions)
                .isNotNull()
                .satisfies(txns -> {
                    Assertions.assertThat(txns.getTransactions())
                            .hasOnlyOneElementSatisfying(txn -> {
                                assertThat(txn.getId()).isEqualTo(1);
                                assertThat(txn.getUpdates()).isEqualTo(4);
                                assertThat(txn.getDeletes()).isEqualTo(0);
                            });
                });
    }

}
