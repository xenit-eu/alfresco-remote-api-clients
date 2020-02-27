package eu.xenit.alfresco.restapi.client.tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import eu.xenit.alfresco.restapi.client.spi.Constants;
import eu.xenit.alfresco.restapi.client.spi.NodesRestApiClient;
import eu.xenit.alfresco.restapi.client.spi.model.Node;
import eu.xenit.alfresco.restapi.client.spi.model.NodeEntry;
import eu.xenit.alfresco.restapi.client.spi.model.NodeList;
import eu.xenit.alfresco.restapi.client.spi.model.NodeList.NodeChildAssociations;
import eu.xenit.alfresco.restapi.client.spi.model.TargetAssociation;
import eu.xenit.alfresco.restapi.client.spi.model.TargetAssociationEntry;
import eu.xenit.alfresco.restapi.client.spi.model.exceptions.InvalidArgumentException;
import eu.xenit.alfresco.restapi.client.spi.model.exceptions.NotFoundException;
import eu.xenit.alfresco.restapi.client.spi.query.CreateNodeQueryParameters;
import eu.xenit.alfresco.restapi.client.spi.query.DeleteTargetQueryParameters;
import eu.xenit.alfresco.restapi.client.spi.query.GetAssociationsQueryParameters;
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

        NodeList childAssocs = nodesRestApiClient().getChildren(Constants.Node.ROOT,
                new PaginationQueryParameters(), new GetNodeQueryParameters());

        assertThat(childAssocs).isNotNull();
        assertThat(childAssocs.getList().getEntries()).isNotEmpty();
    }

    @Test
    default void deleteNode_nonExisting() {
        assertThrows(NotFoundException.class, () -> nodesRestApiClient().delete("id-do-not-exist"));
    }

    @Test
    default void deleteNode() {
        NodeCreateBody nodeCreateBody = new NodeCreateBody("At the end of this test, I should be gone", "cm:content");

        NodeEntry created = nodesRestApiClient().createChild(Constants.Node.ROOT, nodeCreateBody,
                new CreateNodeQueryParameters().setAutoRename(true));
        assertThat(created).isNotNull()
                .extracting(NodeEntry::getEntry).isNotNull()
                .extracting(Node::getId).isNotNull();
        String createdId = created.getEntry().getId();

        assertThat(nodesRestApiClient().get(createdId).getEntry().getId()).isEqualTo(createdId);

        nodesRestApiClient().delete(createdId);

        assertThrows(NotFoundException.class, () -> nodesRestApiClient().get(createdId));
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

        NodeList nodeList =
                nodesRestApiClient().getChildren(Constants.Node.ROOT, new PaginationQueryParameters(),
                        new GetNodeQueryParameters().withAllIncludes());

        assertThat(nodeList).isNotNull();
        assertThat(nodeList.getList()).isNotNull();
        assertThat(nodeList.getList().getEntries()).isNotNull().isNotEmpty()
                .anySatisfy(validator);
    }

    @Test
    default void associationOperations() {
        Node sourceNode = createNode("Source.txt", "cm:content");
        Node targetNode = createNode("Target.txt", "cm:content");
        final String assocType = "cm:replaces";

        assertNoTargets(sourceNode.getId());
        assertNoTargets(targetNode.getId());

        TargetAssociationEntry targetAssociationEntry = nodesRestApiClient().createTargetAssociation(sourceNode.getId(),
                new TargetAssociation().setTargetId(targetNode.getId()).setAssocType(assocType));
        assertThat(targetAssociationEntry).isNotNull().extracting(TargetAssociationEntry::getEntry).isNotNull();
        assertThat(targetAssociationEntry.getEntry().getTargetId()).isEqualTo(targetNode.getId());
        assertThat(targetAssociationEntry.getEntry().getAssocType()).isEqualTo(assocType);

        Consumer<NodeList> targetAssocNodeListValidator = nodeList -> {
            assertThat(nodeList).isNotNull()
                    .extracting(NodeList::getList).isNotNull()
                    .extracting(NodeChildAssociations::getEntries).isNotNull();
            assertThat(nodeList.getList().getEntries()).hasOnlyOneElementSatisfying(
                    entry -> assertThat(entry.getEntry().getId()).isEqualTo(targetNode.getId()));
        };
        targetAssocNodeListValidator.accept(nodesRestApiClient().getTargets(sourceNode.getId()));
        targetAssocNodeListValidator.accept(nodesRestApiClient().getTargets(sourceNode.getId(),
                new GetAssociationsQueryParameters().filterOnAssocType(assocType)));

        Consumer<NodeList> sourceAssocNodeListValidator = nodeList -> {
            assertThat(nodeList).isNotNull()
                    .extracting(NodeList::getList).isNotNull()
                    .extracting(NodeChildAssociations::getEntries).isNotNull();
            assertThat(nodeList.getList().getEntries()).hasOnlyOneElementSatisfying(
                    entry -> assertThat(entry.getEntry().getId()).isEqualTo(sourceNode.getId()));
        };
        sourceAssocNodeListValidator.accept(nodesRestApiClient().getSources(targetNode.getId()));
        sourceAssocNodeListValidator.accept(nodesRestApiClient().getSources(targetNode.getId(),
                new GetAssociationsQueryParameters().filterOnAssocType(assocType)));

        assertThrows(InvalidArgumentException.class,
                () -> nodesRestApiClient().deleteTargetAssociation(sourceNode.getId(), targetNode.getId(),
                        new DeleteTargetQueryParameters().setAssocType("cm:invalid")));
        nodesRestApiClient().deleteTargetAssociation(sourceNode.getId(), targetNode.getId());

        assertNoTargets(sourceNode.getId());
        assertNoTargets(targetNode.getId());

        cleanup(sourceNode.getId());
        cleanup(targetNode.getId());
    }

    default void assertNoTargets(String nodeId) {
        NodeList targetsAtStart = nodesRestApiClient().getTargets(nodeId);
        assertThat(targetsAtStart).isNotNull()
                .extracting(NodeList::getList).isNotNull()
                .extracting(NodeChildAssociations::getEntries).isNotNull();
        assertThat(targetsAtStart.getList().getEntries()).isEmpty();
    }

    default Node createNode(String name, String type) {
        NodeCreateBody nodeCreateBody = new NodeCreateBody(name, type);

        NodeEntry created = nodesRestApiClient().createChild(Constants.Node.ROOT, nodeCreateBody,
                new CreateNodeQueryParameters().setAutoRename(true));
        assertThat(created).isNotNull().extracting(NodeEntry::getEntry).isNotNull();
        return created.getEntry();
    }

    default void cleanup(String nodeId) {
        try {
            nodesRestApiClient().delete(nodeId);
        } catch (NotFoundException e) {
            // ignore
        }
    }

}
