package eu.xenit.alfresco.webscripts.client.ditto;

import eu.xenit.alfresco.webscripst.client.ditto.NodeLocatorFakeClient;
import eu.xenit.alfresco.webscripts.client.spi.NodeLocatorClient;
import eu.xenit.alfresco.webscripts.tests.NodeLocatorClientTests;
import eu.xenit.testing.ditto.api.AlfrescoDataSet;

class NodeLocatorDittoClientIntegrationTest implements NodeLocatorClientTests {

    public NodeLocatorClient nodeLocatorClient() {
        return new NodeLocatorFakeClient(AlfrescoDataSet.bootstrapAlfresco().build());
    }
}
