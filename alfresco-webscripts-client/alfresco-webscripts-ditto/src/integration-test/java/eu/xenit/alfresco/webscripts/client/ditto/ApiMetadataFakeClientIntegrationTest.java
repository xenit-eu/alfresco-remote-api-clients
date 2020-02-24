package eu.xenit.alfresco.webscripts.client.ditto;

import eu.xenit.alfresco.webscripts.client.spi.ApiMetadataClient;
import eu.xenit.alfresco.webscripts.client.spi.NodeLocatorClient;
import eu.xenit.alfresco.webscripts.tests.ApiMetadataClientTests;
import eu.xenit.testing.ditto.api.AlfrescoDataSet;

class ApiMetadataFakeClientIntegrationTest implements ApiMetadataClientTests {

    private final AlfrescoDataSet dataSet = AlfrescoDataSet.bootstrapAlfresco().build();

    @Override
    public ApiMetadataClient apiMetadataClient() {
        return new ApiMetadataFakeClient(dataSet);
    }

    @Override
    public NodeLocatorClient nodeLocatorClient() {
        return new NodeLocatorFakeClient(dataSet);
    }
}
