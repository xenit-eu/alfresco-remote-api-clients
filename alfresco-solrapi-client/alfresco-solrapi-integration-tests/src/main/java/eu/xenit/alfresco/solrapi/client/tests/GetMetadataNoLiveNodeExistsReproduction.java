package eu.xenit.alfresco.solrapi.client.tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import eu.xenit.alfresco.client.api.exception.HttpStatusException;
import eu.xenit.alfresco.client.api.exception.StatusCode;
import eu.xenit.alfresco.client.solrapi.api.SolrApiClient;
import eu.xenit.alfresco.client.solrapi.api.model.SolrNodeMetadata;
import eu.xenit.alfresco.client.solrapi.api.query.NodeMetadataQueryParameters;
import java.util.List;
import org.junit.jupiter.api.Test;

public interface GetMetadataNoLiveNodeExistsReproduction {

    SolrApiClient solrApiClient();

    @Test
    default void getNodesMetadata_noLiveNodeExistsError() {
        final String liveNodeUuid = createVersionableNode("TEST");

        List<Long> versionNodeIds = getVersionNodeIds(liveNodeUuid);

        List<SolrNodeMetadata> solrNodeMetadataBeforeDelete =
                solrApiClient().getNodesMetadata(new NodeMetadataQueryParameters().setNodeIds(versionNodeIds));
        assertThat(solrNodeMetadataBeforeDelete)
                .isNotNull()
                .hasSize(versionNodeIds.size());

        deleteNode(liveNodeUuid);

        HttpStatusException e = assertThrows(HttpStatusException.class,
                () -> solrApiClient().getNodesMetadata(new NodeMetadataQueryParameters().setNodeIds(versionNodeIds)));
        assertThat(e.getMessage()).contains("No live node exists");
        assertThat(e.getStatusCode()).isEqualTo(StatusCode.INTERNAL_SERVER_ERROR);
    }

    String createVersionableNode(String name);

    List<Long> getVersionNodeIds(String liveNodeUuid);

    void deleteNode(String liveNodeUuid);

}
