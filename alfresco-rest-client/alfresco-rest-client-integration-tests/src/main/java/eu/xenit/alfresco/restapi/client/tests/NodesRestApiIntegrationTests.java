package eu.xenit.alfresco.restapi.client.tests;

import static org.assertj.core.api.Assertions.assertThat;

import eu.xenit.alfresco.restapi.client.spi.Constants;
import eu.xenit.alfresco.restapi.client.spi.NodesRestApiClient;
import eu.xenit.alfresco.restapi.client.spi.model.Node;
import eu.xenit.alfresco.restapi.client.spi.model.NodeChildAssociationsList;
import eu.xenit.alfresco.restapi.client.spi.query.GetNodeQueryParameters;
import eu.xenit.alfresco.restapi.client.spi.query.PaginationQueryParameters;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;

public interface NodesRestApiIntegrationTests {

    NodesRestApiClient nodesRestApiClient();

    Consumer<Node> companyHomeValidator = node -> {
        assertThat(node).isNotNull();
        assertThat(node.getName()).isEqualTo("Company Home");
    };

    @Test
    default void getMetadata_root() {

        Node node = nodesRestApiClient().get(Constants.Node.ROOT, new GetNodeQueryParameters());

        companyHomeValidator.accept(node);

    }

    @Test
    default void getChildren_root() {

        NodeChildAssociationsList childAssocs = nodesRestApiClient().getChildren(Constants.Node.ROOT,
                new PaginationQueryParameters(), new GetNodeQueryParameters());

        assertThat(childAssocs).isNotNull();
        assertThat(childAssocs.getList().getEntries()).isNotEmpty();
    }

}
