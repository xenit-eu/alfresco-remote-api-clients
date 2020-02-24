package eu.xenit.alfresco.webscripts.client.spring;


import eu.xenit.alfresco.webscripts.client.spi.NodeLocatorClient;
import eu.xenit.alfresco.webscripts.tests.NodeLocatorClientTests;
import org.junit.jupiter.api.Nested;

class NodeLocatorSpringClientIntegrationTests extends WebscriptsSpringClientTestsBase {

    @Nested
    class WithProvidedRestTemplate implements NodeLocatorClientTests {

        @Override
        public NodeLocatorClient nodeLocatorClient() {
            return new NodeLocatorSpringClient(alfrescoProperties());
        }
    }

    @Nested
    class WithCustomRestTemplate implements NodeLocatorClientTests {

        @Override
        public NodeLocatorClient nodeLocatorClient() {
            return new NodeLocatorSpringClient(alfrescoProperties());
        }
    }
}