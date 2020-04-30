package eu.xenit.alfresco.solrapi.client.tests;

import static org.assertj.core.api.Assertions.assertThat;

import eu.xenit.alfresco.solrapi.client.spi.SolrApiClient;
import eu.xenit.alfresco.solrapi.client.spi.dto.SolrNodeMetaData;
import eu.xenit.alfresco.solrapi.client.spi.query.NodeMetaDataQueryParameters;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

public interface GetMetadataIntegrationTests {

    String PATTERN_UUID = "\\w{8}-\\w{4}-\\w{4}-\\w{4}-\\w{12}";
    String PATTERN_NODEREF = "workspace://SpacesStore/" + PATTERN_UUID;

    SolrApiClient solrApiClient();

    @Test
    default void getMetadata_companyHome() {

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
                    assertThat(node.getTxnId()).isPositive();
                    assertThat(node.getType()).isEqualTo("cm:folder");
                    assertThat(node.getProperties())
                            .containsEntry("{http://www.alfresco.org/model/content/1.0}name", "Company Home");
                    assertMultiLingualPropertyWithValue(
                            node.getProperties().get("{http://www.alfresco.org/model/content/1.0}description"),
                            "The company root space"
                    );
                    assertThat(node.getAspects()).contains("cm:auditable");
                    assertThat(node.getOwner()).isEqualTo("System");

                    assertThat(node.getPaths())
                            .as("paths")
                            .hasOnlyOneElementSatisfying(path -> {
                                assertThat(path.getPath())
                                        .isEqualTo("/{http://www.alfresco.org/model/application/1.0}company_home");
                                assertThat(path.getApath())
                                        .matches("/" + PATTERN_UUID);
                            });
                    assertThat(node.getNamePaths())
                            .as("namePaths")
                            .hasOnlyOneElementSatisfying(path ->
                                    assertThat(path.getNamePath())
                                            .hasOnlyOneElementSatisfying(p -> assertThat(p).isEqualTo("Company Home")));
                    assertThat(node.getAncestors())
                            .as("ancestors")
                            .hasOnlyOneElementSatisfying(ancestor -> assertThat(ancestor).matches(PATTERN_NODEREF));

                });
    }

    static <T> void assertMultiLingualPropertyWithValue(Object actualValue, T expectedValue) {
        assertThat(actualValue)
                .isNotNull()
                .asList()
                .isNotEmpty();
        Map<String, T> descValue = ((List<Map>) actualValue).get(0);
        assertThat(descValue)
                .isNotNull()
                .containsKey("locale")
                .containsEntry("value", expectedValue);
    }

    @Test
    default void getNodesMetadata_companyHome_filterOutput() {

        SolrApiClient client = solrApiClient();
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

    @Test
    default void getMetadata_nodeWithContent() {
        SolrApiClient client = solrApiClient();

        SolrNodeMetaData nodesMetaData = findAnyNodeWithContent(client);

        assertThat(nodesMetaData).isNotNull();
        assertThat(nodesMetaData.getProperties()).containsKey("{http://www.alfresco.org/model/content/1.0}content");
        assertThat(nodesMetaData.getProperties().get("{http://www.alfresco.org/model/content/1.0}content"))
                .isNotNull()
                .isInstanceOf(Map.class);
        assertThat((Map<String, String>) nodesMetaData.getProperties()
                .get("{http://www.alfresco.org/model/content/1.0}content"))
                .containsOnlyKeys("contentId", "encoding", "locale", "mimetype", "size");

    }

    static SolrNodeMetaData findAnyNodeWithContent(SolrApiClient client) {

        final int transactionBatchSize = 100;
        for (long i = 0; i < 1000; i = i + transactionBatchSize) {
            List<SolrNodeMetaData> nodesMetaData = client.getNodesMetaData(
                    new NodeMetaDataQueryParameters()
                            .setFromNodeId(i)
                            .setToNodeId(i + transactionBatchSize)
                            .setMaxResults(transactionBatchSize));

            Optional<SolrNodeMetaData> metadataWithContent = nodesMetaData.stream()
                    .filter(m -> m.getProperties().containsKey("{http://www.alfresco.org/model/content/1.0}content"))
                    .findAny();

            if (metadataWithContent.isPresent()) {
                return metadataWithContent.get();
            }
        }

        throw new RuntimeException("I need a node with content");
    }

}
