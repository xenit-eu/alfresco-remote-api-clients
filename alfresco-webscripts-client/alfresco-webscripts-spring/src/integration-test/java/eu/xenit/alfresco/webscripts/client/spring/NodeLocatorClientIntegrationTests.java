package eu.xenit.alfresco.webscripts.client.spring;


import eu.xenit.alfresco.webscripts.client.spi.NodeLocatorClient;
import eu.xenit.alfresco.webscripts.tests.NodeLocatorClientTests;

class NodeLocatorClientIntegrationTests extends WebscriptsApiBaseTest implements NodeLocatorClientTests {
    @Override
    public NodeLocatorClient nodeLocatorClient() {
        return new NodeLocatorSpringClient(restTemplateBuilder().build());
    }
}