package eu.xenit.alfresco.solrapi.client.tests;

import static org.assertj.core.api.Assertions.assertThat;

import eu.xenit.alfresco.solrapi.client.tests.spi.SolrApiClient;
import eu.xenit.alfresco.solrapi.client.tests.spi.dto.SolrNodeMetaData;
import eu.xenit.alfresco.solrapi.client.tests.spi.query.NodeMetaDataQueryParameters;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import org.junit.jupiter.api.Test;

public interface GetMetadataIntegrationTests {

    SolrApiClient solrApiClient();

    @Test
    default void getMetadata_companyHome() throws GeneralSecurityException, IOException {

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
