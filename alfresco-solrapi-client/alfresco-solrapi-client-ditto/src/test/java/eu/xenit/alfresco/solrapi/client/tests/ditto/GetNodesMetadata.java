package eu.xenit.alfresco.solrapi.client.tests.ditto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import eu.xenit.alfresco.solrapi.client.tests.spi.SolrApiClient;
import eu.xenit.alfresco.solrapi.client.tests.spi.dto.SolrNode;
import eu.xenit.alfresco.solrapi.client.tests.spi.dto.SolrNodeMetaData;
import eu.xenit.alfresco.solrapi.client.tests.spi.query.NodeMetaDataQueryParameters;
import eu.xenit.alfresco.solrapi.client.tests.spi.query.NodesQueryParameters;
import eu.xenit.testing.ditto.api.NodeView;
import eu.xenit.testing.ditto.api.TransactionView;
import eu.xenit.testing.ditto.api.data.ContentModel.Content;
import eu.xenit.testing.ditto.api.data.ContentModel.System;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

public class GetNodesMetadata {

    private static NodeView nodeViewMock() {
        NodeView nodeView = mock(NodeView.class);
        when(nodeView.stream()).then((a) -> Stream.of(
                new TestNode(1L, System.STORE_ROOT),
                new TestNode(13L, Content.FOLDER)
                        .withProperty(Content.NAME, "Company Home"),
                new TestNode(6L, Content.CONTENT),
                new TestNode(7L, Content.CONTENT),
                new TestNode(20L, Content.CONTENT))
        );

        return nodeView;
    }

    @Test
    void getNodesMetadata_companyHome() {

        SolrApiClient client = new FakeSolrApiClient(null, nodeViewMock());
        List<SolrNodeMetaData> nodesMetaData = client
                .getNodesMetaData(new NodeMetaDataQueryParameters().withNodeIds(13L));

        assertThat(nodesMetaData)
                .hasOnlyOneElementSatisfying(node -> {
                    assertThat(node.getId()).isEqualTo(13L);
                    assertThat(node.getType()).isEqualTo("cm:folder");
                    assertThat(node.getProperties().get(Content.NAME.toString())).isEqualTo("Company Home");
                });
    }
}
