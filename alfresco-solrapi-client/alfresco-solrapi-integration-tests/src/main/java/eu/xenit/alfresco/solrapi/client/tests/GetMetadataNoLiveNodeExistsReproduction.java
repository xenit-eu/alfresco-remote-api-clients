package eu.xenit.alfresco.solrapi.client.tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import eu.xenit.alfresco.client.exception.HttpStatusException;
import eu.xenit.alfresco.client.exception.StatusCode;
import eu.xenit.alfresco.solrapi.client.spi.SolrApiClient;
import eu.xenit.alfresco.solrapi.client.spi.dto.SolrNodeMetaData;
import eu.xenit.alfresco.solrapi.client.spi.query.NodeMetaDataQueryParameters;
import java.util.List;
import org.junit.jupiter.api.Test;

public interface GetMetadataNoLiveNodeExistsReproduction {

    SolrApiClient solrApiClient();

    @Test
    default void getNodesMetadata_noLiveNodeExistsError() {
        final String liveNodeUuid = createVersionableNode("TEST");

        List<Long> versionNodeIds = getVersionNodeIds(liveNodeUuid);

        List<SolrNodeMetaData> solrNodeMetadataBeforeDelete =
                solrApiClient().getNodesMetaData(new NodeMetaDataQueryParameters().setNodeIds(versionNodeIds));
        assertThat(solrNodeMetadataBeforeDelete)
                .isNotNull()
                .hasSize(versionNodeIds.size());

        deleteNode(liveNodeUuid);

        HttpStatusException e = assertThrows(HttpStatusException.class,
                () -> solrApiClient().getNodesMetaData(new NodeMetaDataQueryParameters().setNodeIds(versionNodeIds)));
        assertThat(e.getMessage()).contains("No live node exists");
        assertThat(e.getStatusCode()).isEqualTo(StatusCode.INTERNAL_SERVER_ERROR);
    }

    String createVersionableNode(String name);

    List<Long> getVersionNodeIds(String liveNodeUuid);

    void deleteNode(String liveNodeUuid);

}
