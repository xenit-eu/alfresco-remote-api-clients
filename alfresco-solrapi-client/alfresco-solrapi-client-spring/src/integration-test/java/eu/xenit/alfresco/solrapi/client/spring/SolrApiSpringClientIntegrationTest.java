package eu.xenit.alfresco.solrapi.client.spring;

import eu.xenit.alfresco.solrapi.client.tests.GetMetadataIntegrationTests;
import eu.xenit.alfresco.solrapi.client.tests.GetNodesIntegrationTests;
import eu.xenit.alfresco.solrapi.client.tests.GetTransactionsIntegrationTests;
import eu.xenit.alfresco.solrapi.client.tests.spi.SolrApiClient;
import eu.xenit.alfresco.solrapi.client.tests.spring.SolrAPIClientImpl;
import eu.xenit.alfresco.solrapi.client.tests.spring.SolrApiProperties;
import eu.xenit.alfresco.solrapi.client.tests.spring.SolrRequestFactory;
import eu.xenit.alfresco.solrapi.client.tests.spring.SolrSslProperties;

class SolrApiSpringClientIntegrationTest
{

    SolrApiProperties solrApiProperties() {
        return SolrApiProperties.builder()
                .host(System.getProperty("alfresco.host", "localhost"))
                .port(Integer.parseInt(System.getProperty("alfresco.tcp.8443", "8443")))
                .build();
    }

    public SolrApiClient solrApiClient() {
        try {
            SolrRequestFactory solrRequestFactory = new SolrRequestFactory(new SolrSslProperties());
            return new SolrAPIClientImpl(solrApiProperties(), solrRequestFactory);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static class Transactions
            extends SolrApiSpringClientIntegrationTest
            implements GetTransactionsIntegrationTests {

    }

    static class Nodes
            extends SolrApiSpringClientIntegrationTest
            implements GetNodesIntegrationTests {

    }

    static class Metadata
            extends SolrApiSpringClientIntegrationTest
            implements GetMetadataIntegrationTests {

    }

}
