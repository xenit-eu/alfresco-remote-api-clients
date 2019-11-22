package eu.xenit.alfresco.solrapi.client.spring;

import static org.assertj.core.api.Assertions.assertThat;

import eu.xenit.alfresco.solrapi.client.spi.dto.SolrTransactions;
import java.io.IOException;
import java.security.GeneralSecurityException;
import org.junit.jupiter.api.Test;

public class SolrApiClientImplIntegrationTest {

    private SolrApiProperties solrApiProperties() {
        return SolrApiProperties.builder()
                .host(System.getProperty("alfresco.host", "localhost"))
                .port(Integer.parseInt(System.getProperty("alfresco.tcp.8443", "8443")))
                .build();
    }


    @Test
    public void test() throws GeneralSecurityException, IOException {

        SolrRequestFactory solrRequestFactory = new SolrRequestFactory(new SolrSslProperties());
        SolrAPIClientImpl client = new SolrAPIClientImpl(solrApiProperties(), solrRequestFactory);

        SolrTransactions transactions = client.getTransactions(null, 1L, null, 10L, 1);
        assertThat(transactions)
                .isNotNull()
                .satisfies(txns -> {
                    assertThat(txns.getTransactions())
                            .hasOnlyOneElementSatisfying(txn -> {
                                assertThat(txn.getId()).isEqualTo(1);
                                assertThat(txn.getUpdates()).isEqualTo(4);
                                assertThat(txn.getDeletes()).isEqualTo(0);
                            });
                });
    }

}
