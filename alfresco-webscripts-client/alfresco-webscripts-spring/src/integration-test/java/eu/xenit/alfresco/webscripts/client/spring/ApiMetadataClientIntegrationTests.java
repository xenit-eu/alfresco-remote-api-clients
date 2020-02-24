package eu.xenit.alfresco.webscripts.client.spring;


import eu.xenit.alfresco.webscripts.client.spi.ApiMetadataClient;
import eu.xenit.alfresco.webscripts.client.spi.NodeLocatorClient;
import eu.xenit.alfresco.webscripts.tests.ApiMetadataClientTests;

class ApiMetadataClientIntegrationTests extends WebscriptsSpringClientTestsBase implements ApiMetadataClientTests {

    @Override
    public ApiMetadataClient apiMetadataClient() {
        return new ApiMetadataSpringClient(alfrescoProperties(), restTemplateBuilder().build());
    }

    @Override
    public NodeLocatorClient nodeLocatorClient() {
        return new NodeLocatorSpringClient(alfrescoProperties(), restTemplateBuilder().build());
    }
}