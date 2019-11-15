package eu.xenit.alfresco.solrapi.client.spring;

import static org.assertj.core.api.Assertions.assertThat;

import eu.xenit.alfresco.solrapi.client.spi.SolrTransactions;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import org.junit.jupiter.api.Test;

public class SolrApiClientImplIntegrationTest {


    @Test
    public void test()
            throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {

        String url = "https://localhost:8443/alfresco/service/api/solr/";

        SolrRequestFactory solrRequestFactory = new SolrRequestFactory(solrSslProps());
        SolrAPIClientImpl client = new SolrAPIClientImpl(url, solrRequestFactory);

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

    private SolrSSLProperties solrSslProps() {

        SolrSSLProperties sslProperties = new SolrSSLProperties();
        sslProperties
                .setKeystorePath("file:../alfresco-solrapi-integration-tests/build/keystore/ssl.repo.client.keystore");
        sslProperties.setKeystorePassword("kT9X6oe68t");
        sslProperties.setTruststorePath(
                "file:../alfresco-solrapi-integration-tests/build/keystore/ssl.repo.client.truststore");
        sslProperties.setTruststorePassword("kT9X6oe68t");

        return sslProperties;
    }
}
