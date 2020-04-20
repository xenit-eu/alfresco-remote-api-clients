package eu.xenit.alfresco.solrapi.client.ditto;

import eu.xenit.alfresco.solrapi.client.spi.SolrApiClient;
import eu.xenit.alfresco.solrapi.client.tests.GetMetadataNoLiveNodeExistsReproduction;
import eu.xenit.testing.ditto.api.AlfrescoDataSet;
import eu.xenit.testing.ditto.api.DataSetBuilder;
import eu.xenit.testing.ditto.api.data.ContentModel.Content;
import eu.xenit.testing.ditto.api.data.ContentModel.System;
import eu.xenit.testing.ditto.api.data.ContentModel.Version2;
import eu.xenit.testing.ditto.api.model.Node;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;

public class DittoClientNoLiveNodeExistsReproduction implements GetMetadataNoLiveNodeExistsReproduction {

    private AlfrescoDataSet dataSet;

    @Override
    public SolrApiClient solrApiClient() {
        return new SolrApiFakeClient(dataSet);
    }

    @Override
    public String createVersionableNode(String name) {
        final String uuid = UUID.randomUUID().toString();

        dataSet = AlfrescoDataSet.builder()
                .addTransaction(txn -> {
                    txn.addNode(node -> {
                        node.storeRefProtocol("workspace");
                        node.storeRefIdentifier("version2Store");
                        node.name(name);
                        node.type(Content.CONTENT);
                        node.property(Version2.FROZEN_NODE_REF, "workspace://SpacesStore/" + uuid);
                    });
                    txn.addNode(node -> {
                        node.uuid(uuid);
                        node.name(name);
                        node.type(Content.CONTENT);
                    });
                })
                .build();

        return uuid;
    }

    @Override
    public List<Long> getVersionNodeIds(String liveNodeUuid) {
        return dataSet.getNodeView().stream()
                .filter(node -> ("workspace://SpacesStore/" + liveNodeUuid).equals(
                        node.getProperties().get(Version2.FROZEN_NODE_REF).orElse(null)))
                .map(Node::getNodeId)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteNode(String liveNodeUuid) {
        DataSetBuilder dataSetBuilder = AlfrescoDataSet.builder();
        dataSet.getNodeView().stream().forEach(node -> {
            dataSetBuilder.addTransaction(tnx -> {
                tnx.addNode(newNode -> {
                    if (node.getNodeRef().getUuid().equals(liveNodeUuid)) {
                        newNode.storeRefProtocol("archive");
                        newNode.storeRefIdentifier("SpacesStore");
                    } else {
                        newNode.storeRefProtocol(node.getNodeRef().getStoreProtocol());
                        newNode.storeRefIdentifier(node.getNodeRef().getStoreIdentifier());
                    }
                    newNode.uuid(node.getNodeRef().getUuid());
                    newNode.name(node.getName());
                    newNode.type(node.getType());
                    newNode.properties(node.getProperties().stream()
                            .filter(entry -> !(System.STORE_IDENTIFIER.equals(entry.getKey())))
                            .filter(entry -> !(System.STORE_PROTOCOL.equals(entry.getKey())))
                            .collect(Collectors.toMap(Entry::getKey,
                                    Entry::getValue)));
                });
            });
        });
        dataSet = dataSetBuilder.build();

    }
}
