package eu.xenit.alfresco.solrapi.client.ditto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import eu.xenit.alfresco.client.solrapi.api.SolrApiClient;
import eu.xenit.alfresco.client.solrapi.api.model.NodePathInfo;
import eu.xenit.alfresco.client.solrapi.api.model.SolrNodeMetadata;
import eu.xenit.alfresco.client.solrapi.api.query.NodeMetadataQueryParameters;
import eu.xenit.testing.ditto.api.AlfrescoDataSet;
import eu.xenit.testing.ditto.api.NodeView;
import eu.xenit.testing.ditto.api.data.ContentModel.Content;
import eu.xenit.testing.ditto.api.data.ContentModel.System;
import eu.xenit.testing.ditto.api.model.Node;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

public class GetNodesMetadata {

    private static NodeView nodeViewMock() {
        NodeView nodeView = mock(NodeView.class);
        when(nodeView.allNodes()).then((a) -> Stream.of(
                new TestNode(1L, System.STORE_ROOT).setTxnId(6L),
                new TestNode(13L, Content.FOLDER)
                        .setTxnId(6L)
                        .withProperty(Content.NAME, "Company Home")
                        .withProperty(Content.OWNER, "System")
                        .withProperty(System.NODE_UUID, "company_home")
                        .withAspects(Content.AUDITABLE),
                new TestNode(6L, Content.CONTENT).setTxnId(6L),
                new TestNode(7L, Content.CONTENT).setTxnId(6L),
                new TestNode(20L, Content.CONTENT).setTxnId(6L),
                new TestNode(79L, Content.FOLDER)
                        .setTxnId(15L)
                        .withProperty(Content.NAME, "Folder")
                        .withProperty(Content.OWNER, "Neo")
                        .withAspects(Content.AUDITABLE))
        );

        return nodeView;
    }

    @Test
    void getNodesMetadata_companyHome() {

        SolrApiClient client = new SolrApiFakeClient(null, nodeViewMock());
        List<SolrNodeMetadata> nodesMetadata = client
                .getNodesMetadata(new NodeMetadataQueryParameters().withNodeIds(13L));

        assertThat(nodesMetadata)
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

        SolrApiClient client = new SolrApiFakeClient(null, nodeViewMock());
        List<SolrNodeMetadata> nodesMetadata = client
                .getNodesMetadata(
                        new NodeMetadataQueryParameters()
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

        assertThat(nodesMetadata)
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

    @Test
    void getNodesMetadata_folderWithSubFolder() {
        AlfrescoDataSet dataSet = AlfrescoDataSet.bootstrapAlfresco()
                .configure(config -> config.skipToTxnId(15L))
                .addTransaction(t -> {
                    t.skipToNodeId(1079L);
                    Node folder = t.addNode(n -> {
                        n.name("Folder");
                        n.type(Content.FOLDER);
                        n.property(Content.OWNER, "Neo");
                    });
                    t.skipToNodeId(1089L);
                    Node subFolder = t.addNode(folder, n -> {
                        n.name("Sub Folder");
                        n.type(Content.FOLDER);
                        n.property(Content.OWNER, "Morpheus");
                    });
                    t.skipToNodeId(1090L);
                    t.addDocument(subFolder, n -> {
                        n.name("document.txt");
                        n.property(Content.OWNER, "Morpheus");
                    });
                })
                .build();

        SolrApiClient client = new SolrApiFakeClient(dataSet);
        List<SolrNodeMetadata> nodesMetadata = client
                .getNodesMetadata(new NodeMetadataQueryParameters().withNodeIds(1090L));

        assertThat(nodesMetadata)
                .hasOnlyOneElementSatisfying(node -> {
                    assertThat(node.getId()).isEqualTo(1090L);
                    assertThat(node.getType()).isEqualTo("cm:content");
                    assertThat(node.getTxnId()).isEqualTo(15L);
                    assertThat(node.getProperties().get(Content.NAME.toString())).isEqualTo("document.txt");
                    assertThat(node.getOwner()).isEqualTo("Morpheus");
                    assertThat(node.getAspects()).contains(Content.AUDITABLE.toPrefixString());
                    assertThat(node.getPaths()).extracting(NodePathInfo::getPath)
                            .containsExactly(
                                    "/{http://www.alfresco.org/model/application/1.0}company_home" +
                                            "/{http://www.alfresco.org/model/content/1.0}Folder" +
                                            "/{http://www.alfresco.org/model/content/1.0}Sub Folder" +
                                            "/{http://www.alfresco.org/model/content/1.0}document.txt");
                    assertThat(node.getParentAssocs()).isNotEmpty();
                    assertThat(node.getParentAssocs().get(0)).satisfies(assoc -> {
                        assertThat(assoc.getChildRef()).isEqualTo(node.getNodeRef());
                        assertThat(assoc.getAssocTypeQName()).isEqualTo(Content.CONTAINS.toString());
                        assertThat(assoc.getChildQName())
                                .isEqualTo("{http://www.alfresco.org/model/content/1.0}document.txt");
                        assertThat(assoc.isPrimary()).isTrue();
                        assertThat(assoc.getIndex()).isEqualTo(-1);
                    });

                });
    }
}
