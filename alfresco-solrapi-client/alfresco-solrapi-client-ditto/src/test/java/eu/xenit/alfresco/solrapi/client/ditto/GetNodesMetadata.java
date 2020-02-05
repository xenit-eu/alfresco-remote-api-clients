package eu.xenit.alfresco.solrapi.client.ditto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import eu.xenit.alfresco.solrapi.client.spi.SolrApiClient;
import eu.xenit.alfresco.solrapi.client.spi.dto.SolrNodeMetaData;
import eu.xenit.alfresco.solrapi.client.spi.query.NodeMetaDataQueryParameters;
import eu.xenit.testing.ditto.api.NodeView;
import eu.xenit.testing.ditto.api.data.ContentModel.Content;
import eu.xenit.testing.ditto.api.data.ContentModel.System;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

public class GetNodesMetadata {

    private static NodeView nodeViewMock() {
        NodeView nodeView = mock(NodeView.class);
        when(nodeView.stream()).then((a) -> Stream.of(
                new TestNode(1L, System.STORE_ROOT).setTxnId(6L),
                new TestNode(13L, Content.FOLDER)
                        .setTxnId(6L)
                        .withProperty(Content.NAME, "Company Home")
                        .withProperty(Content.OWNER, "System")
                        .withAspects(Content.AUDITABLE),
                new TestNode(6L, Content.CONTENT).setTxnId(6L),
                new TestNode(7L, Content.CONTENT).setTxnId(6L),
                new TestNode(20L, Content.CONTENT).setTxnId(6L))
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
                    assertThat(node.getTxnId()).isEqualTo(6L);
                    assertThat(node.getProperties().get(Content.NAME.toString())).isEqualTo("Company Home");
                    assertThat(node.getOwner()).isEqualTo("System");
                    assertThat(node.getAspects()).contains(Content.AUDITABLE.toPrefixString());
                });
    }

    @Test
    void getNodesMetadata_companyHome_filterOutput() {

        SolrApiClient client = new FakeSolrApiClient(null, nodeViewMock());
        List<SolrNodeMetaData> nodesMetaData = client
                .getNodesMetaData(
                        new NodeMetaDataQueryParameters()
                                .withNodeIds(13L)
                                .setIncludeTxnId(false)
                                .setIncludeType(false)
                                .setIncludeNodeRef(false)
                                .setIncludeProperties(false)
                                .setIncludeAspects(false)
                                .setIncludeOwner(false)
                                .setIncludeAclId(false)
                                .setIncludeChildAssociations(false)
                                .setIncludeChildIds(false)
                                .setIncludeParentAssociations(false)
                                .setIncludePaths(false)
                );

        assertThat(nodesMetaData)
                .hasOnlyOneElementSatisfying(node -> {
                    assertThat(node.getId()).isEqualTo(13L);
                    assertThat(node.getTxnId()).isEqualTo(-1L);
                    assertThat(node.getType()).isNull();
                    assertThat(node.getNodeRef()).isNull();
                    assertThat(node.getProperties()).isEmpty();
                    assertThat(node.getAspects()).isEmpty();
                    assertThat(node.getOwner()).isNull();
                    assertThat(node.getChildIds()).isEmpty();
                    assertThat(node.getParentAssocs()).isEmpty();
                    assertThat(node.getPaths()).isEmpty();
                    // Alfresco doesn't actually use these filters:
//                    assertThat(node.getAclId()).isEqualTo(-1L);
//                    assertThat(node.getChildAssocs()).isEmpty();
                });
    }
}
