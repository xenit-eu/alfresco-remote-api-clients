package eu.xenit.alfresco.solrapi.client.ditto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import eu.xenit.alfresco.solrapi.client.spi.dto.SolrNode;
import eu.xenit.alfresco.solrapi.client.spi.query.NodesQueryParameters;
import eu.xenit.testing.ditto.api.TransactionView;
import eu.xenit.testing.ditto.api.data.ContentModel.Content;
import eu.xenit.testing.ditto.api.data.ContentModel.System;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

public class GetNodesTests {

    private static TransactionView getTxnViewMock() {
        TransactionView txnView = mock(TransactionView.class);
        when(txnView.stream()).then((a) -> Stream.of(
                new TestTransaction(1L)
                        .addUpdated(new TestNode(1L, System.STORE_ROOT)),
                new TestTransaction(2L)
                        .addUpdated(new TestNode(5L, Content.FOLDER))
                        .addUpdated(new TestNode(6L, Content.CONTENT))
                        .addUpdated(new TestNode(7L, Content.CONTENT)),
                new TestTransaction(3L)
                        .addUpdated(new TestNode(20L, Content.CONTENT))
        ));

        return txnView;
    }

    @Test
    void testTxnIdsFilter() {

        TransactionView txnView = getTxnViewMock();
        FakeSolrApiClient client = new FakeSolrApiClient(txnView, null);

        List<SolrNode> nodes = client.getNodes(new NodesQueryParameters().withTxnIds(2L));
        assertThat(nodes)
                .hasSize(3)
                .allSatisfy(node -> assertThat(node.getTxnId()).isEqualTo(2L));
    }

    @Test
    void testTxnFromFilter() {

        TransactionView txnView = getTxnViewMock();
        FakeSolrApiClient client = new FakeSolrApiClient(txnView, null);

        List<SolrNode> nodes = client.getNodes(new NodesQueryParameters().setFromTxnId(3L));
        assertThat(nodes)
                .hasOnlyOneElementSatisfying(n -> assertThat(n.getTxnId()).isEqualTo(3L));
    }

    @Test
    void testTxnToFilter() {

        TransactionView txnView = getTxnViewMock();
        FakeSolrApiClient client = new FakeSolrApiClient(txnView, null);

        List<SolrNode> nodes = client.getNodes(new NodesQueryParameters().setToTxnId(1L));
        assertThat(nodes)
                .hasOnlyOneElementSatisfying(n -> assertThat(n.getTxnId()).isEqualTo(1L));
    }

    @Test
    void testNodeIdFromFilter() {

        TransactionView txnView = getTxnViewMock();
        FakeSolrApiClient client = new FakeSolrApiClient(txnView, null);

        List<SolrNode> nodes = client.getNodes(new NodesQueryParameters().setFromNodeId(8L));
        assertThat(nodes)
                .hasOnlyOneElementSatisfying(n -> {
                    assertThat(n.getId()).isEqualTo(20L);
                    assertThat(n.getTxnId()).isEqualTo(3L);
                });
    }

    @Test
    void testNodeIdToFilter() {

        TransactionView txnView = getTxnViewMock();
        FakeSolrApiClient client = new FakeSolrApiClient(txnView, null);

        List<SolrNode> nodes = client.getNodes(new NodesQueryParameters().setToNodeId(1L));
        assertThat(nodes)
                .hasOnlyOneElementSatisfying(n -> {
                    assertThat(n.getId()).isEqualTo(1L);
                    assertThat(n.getTxnId()).isEqualTo(1L);
                });
    }

    @Test
    void testCombinedFilter() {

        TransactionView txnView = getTxnViewMock();
        FakeSolrApiClient client = new FakeSolrApiClient(txnView, null);

        List<SolrNode> nodes = client.getNodes(new NodesQueryParameters()
                .setFromTxnId(2L)
                .setToTxnId(3L)
                .setFromNodeId(7L)
                .setToNodeId(10L));

        assertThat(nodes)
                .hasOnlyOneElementSatisfying(n -> {
                    assertThat(n.getId()).isEqualTo(7L);
                    assertThat(n.getTxnId()).isEqualTo(2L);
                });
    }


}
