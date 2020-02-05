package eu.xenit.alfresco.solrapi.client.ditto;

import eu.xenit.alfresco.solrapi.client.tests.GetMetadataIntegrationTests;
import eu.xenit.alfresco.solrapi.client.tests.GetNodesIntegrationTests;
import eu.xenit.alfresco.solrapi.client.tests.GetTransactionsIntegrationTests;
import eu.xenit.alfresco.solrapi.client.spi.SolrApiClient;
import eu.xenit.testing.ditto.api.AlfrescoDataSet;

class SolrApiDittoClientIntegrationTest
{

    public SolrApiClient solrApiClient() {
        return FakeSolrApiClient.builder()
                .withDataSet(AlfrescoDataSet.bootstrapAlfresco().build())
                .build();
    }

    static class Transactions
            extends SolrApiDittoClientIntegrationTest
            implements GetTransactionsIntegrationTests {

    }

    static class Nodes
            extends SolrApiDittoClientIntegrationTest
            implements GetNodesIntegrationTests {

    }

    static class Metadata
            extends SolrApiDittoClientIntegrationTest
            implements GetMetadataIntegrationTests {

    }

}
