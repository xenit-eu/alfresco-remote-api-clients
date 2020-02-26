package eu.xenit.alfresco.restapi.client.tests;

import static org.assertj.core.api.Assertions.assertThat;

import eu.xenit.alfresco.restapi.client.spi.Constants;
import eu.xenit.alfresco.restapi.client.spi.NodesRestApiClient;
import eu.xenit.alfresco.restapi.client.spi.model.Node;
import eu.xenit.alfresco.restapi.client.spi.query.GetNodeQueryParameters;
import org.junit.jupiter.api.Test;

public interface NodesRestApiIntegrationTests {

    NodesRestApiClient nodesRestApiClient();

    @Test
    default void getMetadata_root() {

        Node node = nodesRestApiClient().get(Constants.Node.ROOT, new GetNodeQueryParameters());

        assertThat(node).isNotNull();

    }

}
