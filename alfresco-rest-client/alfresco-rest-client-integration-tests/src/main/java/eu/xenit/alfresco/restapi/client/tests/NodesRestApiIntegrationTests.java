package eu.xenit.alfresco.restapi.client.tests;

import static org.assertj.core.api.Assertions.assertThat;

import eu.xenit.alfresco.restapi.client.spi.Constants;
import eu.xenit.alfresco.restapi.client.spi.NodesRestApiClient;
import eu.xenit.alfresco.restapi.client.spi.model.NodeChildAssociationsList;
import eu.xenit.alfresco.restapi.client.spi.model.NodeEntry;
import eu.xenit.alfresco.restapi.client.spi.query.CreateNodeQueryParameters;
import eu.xenit.alfresco.restapi.client.spi.query.GetNodeQueryParameters;
import eu.xenit.alfresco.restapi.client.spi.query.NodeCreateBody;
import eu.xenit.alfresco.restapi.client.spi.query.PaginationQueryParameters;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;

public interface NodesRestApiIntegrationTests {

    NodesRestApiClient nodesRestApiClient();

    Consumer<NodeEntry> companyHomeValidator = node -> {
        assertThat(node).isNotNull();
        assertThat(node.getEntry()).isNotNull();
        assertThat(node.getEntry().getName()).isEqualTo("Company Home");
    };

    @Test
    default void getNode_companyHome() {

        NodeEntry nodeEntry = nodesRestApiClient().get(Constants.Node.ROOT, new GetNodeQueryParameters());
        companyHomeValidator.accept(nodeEntry);

    }

    @Test
    default void getChildren_root() {

        NodeChildAssociationsList childAssocs = nodesRestApiClient().getChildren(Constants.Node.ROOT,
                new PaginationQueryParameters(), new GetNodeQueryParameters());

        assertThat(childAssocs).isNotNull();
        assertThat(childAssocs.getList().getEntries()).isNotEmpty();
    }

    default void deleteNode() {

    }

    @Test
    default void createChild_inCompanyHome() {
        NodeCreateBody nodeCreateBody = new NodeCreateBody("Hello World!", "cm:content")
                .withAspect("cm:summarizable")
                .withProperty("cm:summary", "I have no idea what this property is");

        Consumer<NodeEntry> validator = nodeEntry -> {
            assertThat(nodeEntry).isNotNull();
            assertThat(nodeEntry.getEntry()).isNotNull();
            assertThat(nodeEntry.getEntry().getName()).startsWith("Hello World!");
            assertThat(nodeEntry.getEntry().getAspectNames()).contains("cm:summarizable");
            assertThat(nodeEntry.getEntry().getProperties()).hasEntrySatisfying("cm:summary", o -> {
                assertThat(o).isInstanceOf(String.class);
                assertThat((String) o).isEqualTo("I have no idea what this property is");
            });
        };

        NodeEntry created = nodesRestApiClient().createChild(Constants.Node.ROOT, nodeCreateBody,
                new CreateNodeQueryParameters().setAutoRename(true));
        validator.accept(created);

        String id = created.getEntry().getId();

        NodeEntry get = nodesRestApiClient().get(id);
        validator.accept(get);

        NodeChildAssociationsList nodeChildAssociationsList =
                nodesRestApiClient().getChildren(Constants.Node.ROOT, new PaginationQueryParameters(),
                        new GetNodeQueryParameters().withAllIncludes());

        assertThat(nodeChildAssociationsList).isNotNull();
        assertThat(nodeChildAssociationsList.getList()).isNotNull();
        assertThat(nodeChildAssociationsList.getList().getEntries()).isNotNull().isNotEmpty()
                .anySatisfy(validator);
    }

}
