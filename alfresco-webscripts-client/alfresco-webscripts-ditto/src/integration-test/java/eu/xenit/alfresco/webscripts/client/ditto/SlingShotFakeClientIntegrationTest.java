package eu.xenit.alfresco.webscripts.client.ditto;

import eu.xenit.alfresco.webscripts.client.spi.NodeLocatorClient;
import eu.xenit.alfresco.webscripts.client.spi.SlingShotClient;
import eu.xenit.alfresco.webscripts.tests.SlingShotClientTests;
import eu.xenit.testing.ditto.api.AlfrescoDataSet;

public class SlingShotFakeClientIntegrationTest implements SlingShotClientTests {

    private final AlfrescoDataSet dataSet = AlfrescoDataSet.bootstrapAlfresco().build();

    @Override
    public SlingShotClient slingShotClient() {
        return new SlingShotFakeClient(dataSet);
    }

    @Override
    public NodeLocatorClient nodeLocatorClient() {
        return new NodeLocatorFakeClient(dataSet);
    }

}
