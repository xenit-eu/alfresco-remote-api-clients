package eu.xenit.alfresco.solrapi.client.ditto;

import eu.xenit.alfresco.client.solrapi.api.SolrApiClient;
import eu.xenit.alfresco.solrapi.client.tests.GetMetadataIntegrationTests;
import eu.xenit.alfresco.solrapi.client.tests.GetNodesIntegrationTests;
import eu.xenit.alfresco.solrapi.client.tests.GetTransactionsIntegrationTests;
import eu.xenit.testing.ditto.api.AlfrescoDataSet;

class SolrApiDittoClientIntegrationTest {

    public SolrApiClient solrApiClient() {
        return SolrApiFakeClient.builder()
                .withDataSet(
                        AlfrescoDataSet.bootstrapAlfresco()
                                .addTransaction(t -> {
                                    t.addNode(n -> {
                                        n.name("node-with-content.txt");
                                        n.type("cm:content");
                                        n.content("I have content");
                                    });
                                })
                                .build())
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
