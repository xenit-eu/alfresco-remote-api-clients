package eu.xenit.alfresco.webscripts.client.ditto;

import eu.xenit.alfresco.webscripst.client.ditto.NodeLocatorDittoClient;
import eu.xenit.alfresco.webscripts.client.spi.NodeLocatorClient;
import eu.xenit.alfresco.webscripts.tests.NodeLocatorClientTests;
import eu.xenit.testing.ditto.api.AlfrescoDataSet;

class NodeLocatorDittoClientIntegrationTest implements NodeLocatorClientTests
{
    public NodeLocatorClient nodeLocatorClient() {
        return new NodeLocatorDittoClient(AlfrescoDataSet.bootstrapAlfresco().build());
    }
}
