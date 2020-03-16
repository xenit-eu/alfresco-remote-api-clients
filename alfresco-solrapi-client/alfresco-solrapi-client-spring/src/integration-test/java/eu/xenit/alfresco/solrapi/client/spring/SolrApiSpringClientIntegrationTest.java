package eu.xenit.alfresco.solrapi.client.spring;

import eu.xenit.alfresco.solrapi.client.spi.SolrApiClient;
import eu.xenit.alfresco.solrapi.client.spring.http.SolrRequestFactory;
import eu.xenit.alfresco.solrapi.client.spring.model.SolrApiProperties;
import eu.xenit.alfresco.solrapi.client.spring.model.SolrSslProperties;
import eu.xenit.alfresco.solrapi.client.tests.GetAclChangeSetsIntegrationTests;
import eu.xenit.alfresco.solrapi.client.tests.GetAclReadersIntegrationTests;
import eu.xenit.alfresco.solrapi.client.tests.GetAclsIntegrationTests;
import eu.xenit.alfresco.solrapi.client.tests.GetMetadataIntegrationTests;
import eu.xenit.alfresco.solrapi.client.tests.GetNodesIntegrationTests;
import eu.xenit.alfresco.solrapi.client.tests.GetTextContentResponseIntegrationTests;
import eu.xenit.alfresco.solrapi.client.tests.GetTransactionsIntegrationTests;

class SolrApiSpringClientIntegrationTest {

    SolrApiProperties solrApiProperties() {
        return new SolrApiProperties()
                .setUrl("https://" + System.getProperty("alfresco.host", "localhost") + ":" +
                        System.getProperty("alfresco.tcp.8443", "8443") + "/alfresco");
    }

    public SolrApiClient solrApiClient() {
        try {
            SolrRequestFactory solrRequestFactory = new SolrRequestFactory(new SolrSslProperties());
            return new SolrApiSpringClient(solrApiProperties(), solrRequestFactory);
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

    static class AclChangeSets
            extends SolrApiSpringClientIntegrationTest
            implements GetAclChangeSetsIntegrationTests {

    }

    static class Acls
            extends SolrApiSpringClientIntegrationTest
            implements GetAclsIntegrationTests {

    }

    static class AclReaders
            extends SolrApiSpringClientIntegrationTest
            implements GetAclReadersIntegrationTests {

    }

    static class TextContentResponse
            extends SolrApiSpringClientIntegrationTest
            implements GetTextContentResponseIntegrationTests {
    }
}
