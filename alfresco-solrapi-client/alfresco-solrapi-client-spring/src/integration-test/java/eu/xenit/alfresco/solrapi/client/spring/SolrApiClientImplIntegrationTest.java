package eu.xenit.alfresco.solrapi.client.spring;

import static org.assertj.core.api.Assertions.assertThat;

import eu.xenit.alfresco.solrapi.client.spi.SolrApiClient;
import eu.xenit.alfresco.solrapi.client.spi.dto.SolrNode;
import eu.xenit.alfresco.solrapi.client.spi.dto.SolrNodeMetaData;
import eu.xenit.alfresco.solrapi.client.spi.dto.SolrTransactions;
import eu.xenit.alfresco.solrapi.client.spi.query.NodeMetaDataQueryParameters;
import eu.xenit.alfresco.solrapi.client.spi.query.NodesQueryParameters;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import org.junit.jupiter.api.Test;

public class SolrApiClientImplIntegrationTest {

    private static SolrApiProperties solrApiProperties() {
        return SolrApiProperties.builder()
                .host(System.getProperty("alfresco.host", "localhost"))
                .port(Integer.parseInt(System.getProperty("alfresco.tcp.8443", "8443")))
                .build();
    }

    private static SolrAPIClientImpl solrApiClient() throws GeneralSecurityException, IOException {
        SolrRequestFactory solrRequestFactory = new SolrRequestFactory(new SolrSslProperties());
        return new SolrAPIClientImpl(solrApiProperties(), solrRequestFactory);
    }

    static class Transactions {

        @Test
        public void getTransactions() throws GeneralSecurityException, IOException {

            SolrApiClient client = solrApiClient();

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

    static class Nodes {

        @Test
        public void getNodes_companyHome() throws GeneralSecurityException, IOException {
            SolrApiClient client = solrApiClient();

            // Assuming that Company Home stays alf_node.id = 13
            // We could need to change this test if that changes across Alfresco versions ?
            List<SolrNode> nodes = client.getNodes(new NodesQueryParameters().setFromNodeId(13L).setToNodeId(13L));
            assertThat(nodes)
                    .hasOnlyOneElementSatisfying(node -> {
                        assertThat(node.getId()).isEqualTo(13);
                        assertThat(node.getNodeRef()).startsWith("workspace://SpacesStore/");
                        assertThat(node.getTxnId()).isEqualTo(6);
                        assertThat(node.getStatus()).isEqualTo("u");
                        assertThat(node.getAclId()).isEqualTo(10);
                        assertThat(node.getTenant()).isEmpty();
                    });
        }
    }


    static class Metadata {

        @Test
        public void getMetadata_companyHome() throws GeneralSecurityException, IOException {

            SolrApiClient client = solrApiClient();

            // Assuming that Company Home stays alf_node.id = 13
            // We could need to change this test if that changes across Alfresco versions ?
            List<SolrNodeMetaData> nodesMetaData = client.getNodesMetaData(
                    new NodeMetaDataQueryParameters()
                            .setFromNodeId(13L)
                            .setToNodeId(13L)
                            .setMaxResults(1));

            assertThat(nodesMetaData)
                    .isNotNull()
                    .hasOnlyOneElementSatisfying(node -> {
                        assertThat(node.getId()).isEqualTo(13);
                        assertThat(node.getType()).isEqualTo("cm:folder");
                        assertThat(node.getProperties())
                                .containsEntry("{http://www.alfresco.org/model/content/1.0}name", "Company Home");
                        assertThat(node.getPaths())
                                .hasOnlyOneElementSatisfying(path -> {
                                    assertThat(path.getPath())
                                            .isEqualTo("/{http://www.alfresco.org/model/application/1.0}company_home");
                                });
                        assertThat(node.getAncestors())
                                .hasOnlyOneElementSatisfying(ancestor ->
                                        assertThat(ancestor).startsWith("workspace://SpacesStore/")
                                );
                        assertThat(node.getNamePaths())
                                // There is only a single named path to company home:
                                // it has no secondary parents or category-paths
                                .hasOnlyOneElementSatisfying(primaryPath ->
                                        assertThat(primaryPath.getNamePath())
                                                .contains("Company Home")
                                );

                    });
        }
    }
}
