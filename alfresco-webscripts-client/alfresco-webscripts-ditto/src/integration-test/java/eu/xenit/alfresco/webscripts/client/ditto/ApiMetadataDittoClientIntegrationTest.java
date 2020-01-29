package eu.xenit.alfresco.webscripts.client.ditto;

import eu.xenit.alfresco.webscripst.client.ditto.ApiMetadataDittoClient;
import eu.xenit.alfresco.webscripst.client.ditto.NodeLocatorDittoClient;
import eu.xenit.alfresco.webscripts.client.spi.ApiMetadataClient;
import eu.xenit.alfresco.webscripts.client.spi.NodeLocatorClient;
import eu.xenit.alfresco.webscripts.tests.ApiMetadataClientTests;
import eu.xenit.testing.ditto.api.AlfrescoDataSet;

class ApiMetadataDittoClientIntegrationTest implements ApiMetadataClientTests
{
    private final AlfrescoDataSet dataSet = AlfrescoDataSet.bootstrapAlfresco().build();

    @Override
    public ApiMetadataClient apiMetadataClient() {
        return new ApiMetadataDittoClient(dataSet);
    }

    @Override
    public NodeLocatorClient nodeLocatorClient() {
        return new NodeLocatorDittoClient(dataSet);
    }
}
