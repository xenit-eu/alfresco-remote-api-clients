package eu.xenit.alfresco.webscripts.client.ditto;

import eu.xenit.alfresco.webscripts.client.spi.ApiNodeContentClient;
import eu.xenit.alfresco.webscripts.client.spi.NodeLocatorClient;
import eu.xenit.alfresco.webscripts.tests.ApiNodeContentClientTests;
import eu.xenit.testing.ditto.api.AlfrescoDataSet;

public class ApiNodeContentFakeClientIntegrationTest implements ApiNodeContentClientTests {

    private final AlfrescoDataSet dataSet = AlfrescoDataSet.bootstrapAlfresco().build();

    @Override
    public ApiNodeContentClient apiNodeContentClient() {
        return new ApiNodeContentFakeClient(dataSet);
    }

    @Override
    public NodeLocatorClient nodeLocatorClient() {
        return new NodeLocatorFakeClient(dataSet);
    }
}
