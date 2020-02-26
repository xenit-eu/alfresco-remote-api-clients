package eu.xenit.alfresco.webscripts.client.ditto;

import static org.assertj.core.api.Assertions.assertThat;

import eu.xenit.alfresco.webscripts.client.spi.NodeLocatorClient;
import eu.xenit.alfresco.webscripts.client.spi.SlingShotClient;
import eu.xenit.alfresco.webscripts.client.spi.model.slingshot.Metadata;
import eu.xenit.alfresco.webscripts.tests.SlingShotClientTests;
import eu.xenit.testing.ditto.api.AlfrescoDataSet;
import eu.xenit.testing.ditto.api.data.ContentModel.Content;
import eu.xenit.testing.ditto.api.model.Node;
import org.junit.jupiter.api.Test;

public class SlingShotFakeClientIntegrationTest implements SlingShotClientTests {

    private Node[] nodes = new Node[2];
    private final AlfrescoDataSet dataSet = AlfrescoDataSet.bootstrapAlfresco()
            .skipToTransaction(123L)
            .addTransaction(txn -> {
                nodes[0] = txn.addNode(doc -> {
                    doc.type(Content.FOLDER);
                    doc.name("foo");
                    doc.property("cm:description", "Folder description");
                });
            })
            .skipToTransaction(456L)
            .addTransaction((txn -> {
                nodes[1] = txn.addNode(doc -> {
                    doc.type(Content.CONTENT);
                    doc.name("bar.txt");
                    doc.property("cm:description", "Document description");
                    doc.sourceAssociation(nodes[0], Content.OBJECT);
                });
            }))
            .build();

    @Override
    public SlingShotClient slingShotClient() {
        return new SlingShotFakeClient(dataSet);
    }

    @Override
    public NodeLocatorClient nodeLocatorClient() {
        return new NodeLocatorFakeClient(dataSet);
    }

    @Test
    public void associationsTest() {
        Metadata metadata0 = slingShotClient().get(nodes[0].getNodeRef().toString());
        assertThat(metadata0.getAssocs())
                .isNotEmpty()
                .allSatisfy(association -> {
                    assertThat(association.getAssocType().getName()).isEqualTo(Content.OBJECT.toString());
                    assertThat(association.getTargetRef()).isEqualTo(nodes[1].getNodeRef().toString());
                });
        Metadata metadata1 = slingShotClient().get(nodes[1].getNodeRef().toString());
        assertThat(metadata1.getSourceAssocs())
                .isNotEmpty()
                .allSatisfy(association -> {
                    assertThat(association.getAssocType().getName()).isEqualTo(Content.OBJECT.toString());
                    assertThat(association.getSourceRef()).isEqualTo(nodes[0].getNodeRef().toString());
                });
    }

}
