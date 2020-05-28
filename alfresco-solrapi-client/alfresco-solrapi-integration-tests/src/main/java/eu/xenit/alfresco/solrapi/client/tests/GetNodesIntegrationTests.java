package eu.xenit.alfresco.solrapi.client.tests;

import static org.assertj.core.api.Assertions.assertThat;

import eu.xenit.alfresco.client.solrapi.api.SolrApiClient;
import eu.xenit.alfresco.client.solrapi.api.model.SolrNode;
import eu.xenit.alfresco.client.solrapi.api.query.NodesQueryParameters;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

public interface GetNodesIntegrationTests {

    SolrApiClient solrApiClient();

    @Test
    default void getNodes_companyHome() {
        SolrApiClient client = solrApiClient();

        // Assuming that Company Home stays alf_node.id = 13
        // We could need to change this test if that changes across Alfresco versions ?
        List<SolrNode> nodes = client.getNodes(new NodesQueryParameters().setFromNodeId(13L).setToNodeId(13L));
        assertThat(nodes)
                .hasOnlyOneElementSatisfying(node -> {
                    assertThat(node.getId()).isEqualTo(13);
                    assertThat(node.getNodeRef()).startsWith("workspace://SpacesStore/");
                    assertThat(node.getTxnId()).isPositive();
                    assertThat(node.getStatus()).isEqualTo("u");
//                    assertThat(node.getAclId()).isEqualTo(10);
                    assertThat(node.getTenant()).isEmpty();
                });
    }

    @Test
    default void getNodesFromFirstTransaction() {
        SolrApiClient client = solrApiClient();

        List<SolrNode> nodes = client.getNodes(new NodesQueryParameters().setTxnIds(Collections.singletonList(1L)));

        assertThat(nodes)
                .hasSize(4)
                .last()
                .satisfies(node -> {
                    assertThat(node.getId()).isEqualTo(4);
                    assertThat(node.getTxnId()).isEqualTo(1);
                    assertThat(node.getStatus()).isEqualTo("u");
                });
    }
}
