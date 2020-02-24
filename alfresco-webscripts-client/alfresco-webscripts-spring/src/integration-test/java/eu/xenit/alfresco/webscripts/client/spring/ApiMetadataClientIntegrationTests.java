package eu.xenit.alfresco.webscripts.client.spring;


import eu.xenit.alfresco.webscripts.client.spi.ApiMetadataClient;
import eu.xenit.alfresco.webscripts.client.spi.NodeLocatorClient;
import eu.xenit.alfresco.webscripts.tests.ApiMetadataClientTests;

class ApiMetadataClientIntegrationTests extends WebscriptsSpringClientTestsBase implements ApiMetadataClientTests {

    MetadataApiProperties metadataApiProperties() {
        return MetadataApiProperties.builder()
                .host(System.getProperty("alfresco.host", "localhost"))
                .port(Integer.parseInt(System.getProperty("alfresco.tcp.8080", "8080")))
                .build();
    }

    @Override
    public ApiMetadataClient apiMetadataClient() {
        return new ApiMetadataSpringClient(metadataApiProperties(), restTemplateBuilder().build());
    }

    @Override
    public NodeLocatorClient nodeLocatorClient() {
        return new NodeLocatorSpringClient(restTemplateBuilder().build());
    }
}