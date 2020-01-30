package eu.xenit.alfresco.webscripts.client.spring;


import eu.xenit.alfresco.webscripts.client.spi.NodeLocatorClient;
import eu.xenit.alfresco.webscripts.tests.NodeLocatorClientTests;

class NodeLocatorSpringClientIntegrationTests extends WebscriptsSpringClientTestsBase implements
        NodeLocatorClientTests {

    @Override
    public NodeLocatorClient nodeLocatorClient() {
        return new NodeLocatorSpringClient(restTemplateBuilder().build());
    }
}